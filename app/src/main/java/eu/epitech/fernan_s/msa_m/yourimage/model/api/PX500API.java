package eu.epitech.fernan_s.msa_m.yourimage.model.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.test.espresso.core.deps.guava.base.Splitter;
import android.util.Log;

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
import eu.epitech.fernan_s.msa_m.yourimage.model.fav.PX500Fav;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.PX500Thread;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.PX500Token;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.IToken;
import eu.epitech.fernan_s.msa_m.yourimage.singleton.SHttpClient;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;

public class PX500API implements IApi {
    private static String _consumer_key = "w6DJp5yU516NkXuO5DLU0hgW5Ph5LZqounfGiCeE";
    private static String _consumer_secret = "3kEaoCioh0McyjjTzwyZ8FySQTvoCrP8auY1xuQR";
    private static String _apilink = "https://api.500px.com/v1/";
    private String temp_secret = "";
    private String temp_token = "";
    private Context _ctx = null;
    private IToken _token = null;
    private IApi _api = this;

    public PX500API(Context ctx) {
        _ctx = ctx;
        String str = _ctx.getSharedPreferences("tokens", 0).getString("PX500Token", null);
        if (str != null) {
            try {
                _token = PX500Token.Parse(new JSONObject(str));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getAuthlink(final AuthLinkCallback callback) {
        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(_consumer_key, _consumer_secret);
        OkHttpClient client = SHttpClient.getInstance().getClient();
        Request request = new Request.Builder().get().url(_apilink + "oauth/request_token?oauth_callback=http://127.0.0.1/").build();
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
                    callback.onAuthLinkFinished(_apilink + "oauth/authorize?oauth_token=" + temp_token + "&oauth_callback=http://127.0.0.1/");
                }
            });
        } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect(Context ctx, ConnectCallback callback) {
        if (_token == null) {
            AuthDialog diag = new AuthDialog(ctx, this, callback);
            diag.show();
        }
    }

    @Override
    public void auth(String query, final ConnectCallback callback) {
        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(_consumer_key, _consumer_secret);
        consumer.setTokenWithSecret(temp_token, temp_secret);
        Map<String, String> map = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query.substring(18));
        OkHttpClient client = SHttpClient.getInstance().getClient();
        Request request = new Request.Builder().get().url(_apilink + "oauth/access_token?oauth_verifier=" + map.get("oauth_verifier")).build();
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
                    _token = new PX500Token(map);
                    Handler handler = new Handler(_ctx.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            _ctx.getSharedPreferences("tokens", 0).edit().putString("PX500Token", _token.ToJson().toString()).apply();
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
        return (_token != null);
    }

    @Override
    public IToken getToken() {
        return _token;
    }

    @Override
    public void RemoveToken() {
        _ctx.getSharedPreferences("tokens", 0).edit().putString("PX500Token", null).apply();
        _token = null;
    }

    @Override
    public void getThread(int page, final IThread.GetThreadCallback callback) {
        OkHttpClient client = SHttpClient.getInstance().getClient();
        Request request = new Request.Builder().url(_apilink + "photos?feature=popular&sort=rating&page=" + page + "&image_size=3&include_store=store_download&include_states=voted").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    JSONArray arr = obj.getJSONArray("photos");
                    List<IThread> lThread = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject ji = arr.getJSONObject(i);
                        lThread.add(new PX500Thread(
                                ji.getString("name"),
                                ji.getString("id"),
                                ji.getString("description"),
                                ji.getJSONObject("user").getString("username"),
                                ji.getJSONObject("user").getLong("id"),
                                _api
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
        OkHttpClient client = SHttpClient.getInstance().getClient();
        Request request = null;
        try {
            request = new Request.Builder().url(_apilink + "photos/search?page=" + page + "&tag=" + URLEncoder.encode(tags, "UTF-8")).build();
        } catch (UnsupportedEncodingException e) {
            return;
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    JSONArray arr = obj.getJSONArray("photos");
                    List<IThread> lThread = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject ji = arr.getJSONObject(i);
                        lThread.add(new PX500Thread(
                                ji.getString("name"),
                                ji.getString("id"),
                                ji.getString("description"),
                                ji.getJSONObject("user").getString("username"),
                                ji.getJSONObject("user").getLong("id"),
                                _api
                        ));
                    }
                    callback.onGetThreadComplete(lThread);
                } catch (JSONException ignored) {

                }
            }
        });
    }

    @Override
    public void SendPic(final String Title, final String Desc, final List<Bitmap> images) {
        //Humm cancer v2
    }

    @Override
    public Bitmap getIcon() {
        return BitmapFactory.decodeResource(_ctx.getResources(), R.drawable.ic_500px);
    }

    @Override
    public void getFavs(int page, IThread.GetThreadCallback callback) {
        List<PX500Fav> fav = PX500Fav.listAll(PX500Fav.class);
        List<IThread> favs = new ArrayList<>();
        for (PX500Fav f : fav) {
            Log.d("PX500Fav", "getFavs: " + f.getId());
            PX500Thread th = f.getThread();
            favs.add(th.UpdateToken(_token));
        }
        callback.onGetThreadComplete(favs);
    }

    @Override
    public String getName() {
        return ("500px");
    }

    @Override
    public boolean CanUpload() {
        return true;
    }
}
