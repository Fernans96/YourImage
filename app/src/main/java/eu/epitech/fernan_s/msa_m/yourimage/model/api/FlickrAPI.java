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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.dialog.AuthDialog;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.FlickrThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.FlickrToken;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.IToken;
import eu.epitech.fernan_s.msa_m.yourimage.singleton.SHttpClient;
import eu.epitech.fernan_s.msa_m.yourimage.tools.ImagesTools;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import okhttp3.Call;
import okhttp3.Callback;
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
                    Log.d("rep", "onResponse: " + str);
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
    public void connect(Context ctx) {
        AuthDialog diag = new AuthDialog(ctx, this);
        diag.show();
    }

    @Override
    public void auth(String query) {
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

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String str = response.body().string();
                    Log.d("rep", "onResponse: " + str);
                    Map<String, String> map = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(str);
                    _token = new FlickrToken(map);
                    Handler handler = new Handler(_ctx.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(_ctx, "Auth Succeed", Toast.LENGTH_LONG).show();
                            _ctx.getSharedPreferences("tokens", 0).edit().putString("FlickrToken", _token.ToJson().toString()).apply();
                        }
                    });
                }
            });
        } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException e) {
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
    public void SendPic(final String Title, final String Desc, final List<Bitmap> images) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = SHttpClient.getInstance().getClient();
                OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
                consumer.setTokenWithSecret(temp_token, temp_secret);
                try {
                    for (Bitmap img : images) {
                        RequestBody body = new MultipartBody.Builder()
                                .addPart(Headers.of("Content-Disposition", "form-data; name=\"photo\"; filename=\"C:\\android.jpg\""), RequestBody.create(MediaType.parse("image/jpeg"), ImagesTools.toByteArray(img)))
                                .addFormDataPart("title",Title)
                                .addFormDataPart("description",Desc)
                                .build();
                        Request request = new Request.Builder()
                                .url("https://api.imgur.com/3/upload")
                                .post(body)
                                .build();
                        request = (Request) consumer.sign(request).unwrap();
                        Response res = client.newCall(request).execute();
                        if (!res.isSuccessful()) {
                            Handler handler = new Handler(_ctx.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(_ctx, "Upload Failed", Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }
                    }
                    Toast.makeText(_ctx, "Upload Successful", Toast.LENGTH_LONG).show();
                } catch (IOException | OAuthExpectationFailedException | OAuthMessageSignerException | OAuthCommunicationException e) {
                }
            }
        });
        th.start();
    }

    @Override
    public void Fav(int id) {

    }

    @Override
    public void unFav(int id) {

    }

    @Override
    public Bitmap getIcon() {
        return BitmapFactory.decodeResource(_ctx.getResources(), R.drawable.ic_flickr);
    }

    @Override
    public void getFavs(int page, IThread.GetThreadCallback callback) {

    }

    @Override
    public String getName() {
        return ("Flickr");
    }
}
