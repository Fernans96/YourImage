package eu.epitech.fernan_s.msa_m.yourimage.model.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.test.espresso.core.deps.guava.base.Splitter;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.dialog.AuthDialog;
import eu.epitech.fernan_s.msa_m.yourimage.model.fav.FlickrFav;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.FlickrThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.FlickrToken;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.IToken;
import eu.epitech.fernan_s.msa_m.yourimage.singleton.SHttpClient;
import eu.epitech.fernan_s.msa_m.yourimage.tools.ImagesTools;
import eu.epitech.fernan_s.msa_m.yourimage.tools.RequestTool;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpParameters;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;

/**
 * Created by quent on 02/02/2017.
 */

public class FlickrAPI implements IApi {
    public static String CONSUMER_KEY = "e98a6d1cf67c79b5a3ab18dc675cc68b";
    public static String CONSUMER_SECRET = "c2a30235b6af3616";
    private String temp_secret = "";
    private String temp_token = "";
    private IToken _token;
    private Context _ctx;

    public FlickrAPI(Context ctx) {
        _ctx = ctx;
        String str = _ctx.getSharedPreferences("tokens", 0).getString("FlickrToken", null);
        if (str != null) {
            try {
                _token = FlickrToken.Parse(new JSONObject(str));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void getAuthlink(final AuthLinkCallback callback) {
        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        OkHttpClient client = SHttpClient.getInstance().getClient();
        Request request = new Request.Builder().get().url("https://www.flickr.com/services/oauth/request_token?oauth_callback=http://127.0.0.1/").build();
        try {
            request = (Request) consumer.sign(request).unwrap();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String str = response.body().string();
                    Map<String, String> map = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(str);
                    temp_token = map.get("oauth_token");
                    temp_secret = map.get("oauth_token_secret");
                    callback.onAuthLinkFinished("https://www.flickr.com/services/oauth/authorize?oauth_token=" + temp_token);
                }
            });
        } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect(Context ctx, ConnectCallback callback) {
        AuthDialog diag = new AuthDialog(ctx, this, callback);
        diag.show();
    }

    @Override
    public void auth(String query, final ConnectCallback callback) {
        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        consumer.setTokenWithSecret(temp_token, temp_secret);
        Map<String, String> map = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query.substring(18));
        OkHttpClient client = SHttpClient.getInstance().getClient();
        Request request = new Request.Builder().get().url("https://www.flickr.com/services/oauth/access_token?oauth_verifier=" + map.get("oauth_verifier")).build();
        try {
            request = (Request) consumer.sign(request).unwrap();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onConnectFailed();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    callback.onConnectSuccess();
                    String str = response.body().string();
                    Map<String, String> map = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(str);
                    _token = new FlickrToken(map);
                    Handler handler = new Handler(_ctx.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            _ctx.getSharedPreferences("tokens", 0).edit().putString("FlickrToken", _token.ToJson().toString()).apply();
                        }
                    });
                }
            });
        } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException e) {
            callback.onConnectFailed();
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        return _token != null;
    }

    @Override
    public IToken getToken() {
        return _token;
    }

    @Override
    public void RemoveToken() {
        _ctx.getSharedPreferences("tokens", 0).edit().putString("FlickrToken", null).apply();
        _token = null;
    }

    @Override
    public void getThread(int page, final IThread.GetThreadCallback callback) {
        String url = "https://api.flickr.com/services/rest/?";
        OkHttpClient client = SHttpClient.getInstance().getClient();
        Request request = new Request.Builder().url(url + "method=flickr.photos.getRecent&per_page=45&api_key=" + CONSUMER_KEY + "&format=json&page=" + page).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                List<IThread> lThread = new ArrayList<>();
                String str = response.body().string();
                str = str.substring(14, str.length() - 1);
                try {
                    JSONObject jo = new JSONObject(str);
                    JSONArray photos = jo.getJSONObject("photos").getJSONArray("photo");
                    for (int i = 0; i < photos.length(); i++) {
                        JSONObject joo = photos.getJSONObject(i);
                        lThread.add(new FlickrThread(
                                joo.getString("owner"),
                                joo.getString("id"),
                                joo.getString("title")
                        ));
                    }
                    callback.onGetThreadComplete(lThread);
                } catch (JSONException e) {
                    return;
                }

            }
        });
    }

    @Override
    public void getThread(String tags, int page, final IThread.GetThreadCallback callback) {
        String url = "https://api.flickr.com/services/rest/?";
        OkHttpClient client = SHttpClient.getInstance().getClient();
        Request request = null;
        try {
            request = new Request.Builder().url(url + "method=flickr.photos.search&per_page=45&api_key=" + CONSUMER_KEY + "&format=json&text=" + URLEncoder.encode(tags, "UTF-8") + "&page=" + page).build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                List<IThread> lThread = new ArrayList<>();
                String str = response.body().string();
                str = str.substring(14, str.length() - 1);
                try {
                    JSONObject jo = new JSONObject(str);
                    JSONArray photos = jo.getJSONObject("photos").getJSONArray("photo");
                    for (int i = 0; i < photos.length(); i++) {
                        JSONObject joo = photos.getJSONObject(i);
                        lThread.add(new FlickrThread(
                                joo.getString("owner"),
                                joo.getString("id"),
                                joo.getString("title")
                        ));
                    }
                    callback.onGetThreadComplete(lThread);
                } catch (JSONException e) {
                    return;
                }

            }
        });
    }

    @Override
    public void SendPic(final String Title, final String Desc, final List<Bitmap> images, final SendPictureCallback callback) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpParameters params =  new HttpParameters();
                params.put("title", Title);
                params.put("description", Desc);
                OAuthConsumer dealabsConsumer = new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
                dealabsConsumer.setTokenWithSecret(_token.getToken(), _token.getSecret());
                try {
                    Log.d("RES", "run: " + RequestTool.POSTRequest("https://up.flickr.com/services/upload/", params, dealabsConsumer, ImagesTools.toByteArray(images.get(0))));
                    callback.onSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFailed();
                }
            }
        });
        th.start();
    }

    @Override
    public Bitmap getIcon() {
        return BitmapFactory.decodeResource(_ctx.getResources(), R.drawable.social_flickr_box);
    }

    @Override
    public void getFavs(int page, IThread.GetThreadCallback callback) {
        List<FlickrFav> fav = FlickrFav.listAll(FlickrFav.class);
        List<IThread> favs = new ArrayList<>();
        for (FlickrFav f : fav) {
            favs.add(f.getThread());
        }
        callback.onGetThreadComplete(favs);

    }

    @Override
    public void getUserThread(int page, final IThread.GetThreadCallback callback) {
        String url = "https://api.flickr.com/services/rest/?";
        OkHttpClient client = SHttpClient.getInstance().getClient();
        Request request = null;
        request = new Request.Builder().url(url + "method=flickr.people.getPhotos&per_page=45&api_key=" + CONSUMER_KEY + "&format=json&user_id=" + ((FlickrToken)_token).get_id() + "&page=" + page).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                List<IThread> lThread = new ArrayList<>();
                String str = response.body().string();
                Log.d("STR", "onResponse: " + str);
                str = str.substring(14, str.length() - 1);
                try {
                    JSONObject jo = new JSONObject(str);
                    JSONArray photos = jo.getJSONObject("photos").getJSONArray("photo");
                    for (int i = 0; i < photos.length(); i++) {
                        JSONObject joo = photos.getJSONObject(i);
                        lThread.add(new FlickrThread(
                                joo.getString("owner"),
                                joo.getString("id"),
                                joo.getString("title")
                        ));
                    }
                    callback.onGetThreadComplete(lThread);
                } catch (JSONException e) {
                    return;
                }

            }
        });
    }

    @Override
    public String getName() {
        return ("Flickr");
    }

    @Override
    public boolean CanUpload() {
        return true;
    }
}
