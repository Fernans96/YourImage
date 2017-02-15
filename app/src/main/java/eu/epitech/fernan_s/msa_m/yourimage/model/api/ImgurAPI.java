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
import eu.epitech.fernan_s.msa_m.yourimage.model.fav.ImgurFav;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.ImgurThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.IToken;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.ImgurToken;
import eu.epitech.fernan_s.msa_m.yourimage.singleton.SHttpClient;
import eu.epitech.fernan_s.msa_m.yourimage.tools.ImagesTools;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by quent on 30/01/2017.
 */

public class ImgurAPI implements IApi {
    private static String _client_id = "47bb2cf28f17756";
    private static String _secret = "aef3b44d9985f67134e0ab82bbaddb2e7d95f7f8";
    private static String _searchLink = "https://api.imgur.com/3/gallery/search/time/";
    private Context _ctx = null;
    private IToken _token = null;
    private IApi _api = this;

    public ImgurAPI(Context ctx) {
        _ctx = ctx;
        String str = _ctx.getSharedPreferences("tokens", 0).getString("ImgurToken", null);
        if (str != null) {
            try {
                _token = ImgurToken.Parse(new JSONObject(str));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getAuthlink(AuthLinkCallback callback) {
        String ret = "https://api.imgur.com/oauth2/authorize?"
                + "client_id=" + _client_id
                + "&response_type=" + "token"
                + "&state=" + "";
        callback.onAuthLinkFinished(ret);
    }

    @Override
    public void connect(Context ctx, ConnectCallback callback) {
        if (_token == null) {
            AuthDialog diag = new AuthDialog(ctx, this, callback);
            diag.show();
        } else {
            Toast.makeText(_ctx, "Already auth", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void auth(String query, ConnectCallback callback) {
        String[] ret = query.split("#");
        Map<String, String> map = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(ret[ret.length - 1]);
        _token = new ImgurToken(map);
        _ctx.getSharedPreferences("tokens", 0).edit().putString("ImgurToken", _token.ToJson().toString()).apply();
        callback.onConnectSuccess();
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
        _ctx.getSharedPreferences("tokens", 0).edit().putString("ImgurToken", null).apply();
        _token = null;
    }

    @Override
    public void getThread(int page, final IThread.GetThreadCallback callback) {
        OkHttpClient client = SHttpClient.getInstance().getClient();
        Request request = new Request.Builder().url("https://api.imgur.com/3/gallery/hot/time/"+ page +".json").addHeader("Authorization", "Bearer " + _token.getToken()).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject obj = new JSONObject(response.body().string());
                    JSONArray arr = obj.getJSONArray("data");
                    List<IThread> lThread = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject ji = arr.getJSONObject(i);
                        lThread.add(new ImgurThread(
                                ji.getString("title"),
                                ji.getString("id"),
                                ji.getString("topic"),
                                ji.getString("account_url"),
                                ji.getLong("account_id"),
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
            request = new Request.Builder().url(_searchLink + page + "/?q_any=" + URLEncoder.encode(tags, "UTF-8")).addHeader("Authorization", "Bearer " + _token.getToken()).build();
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
                    JSONArray arr = obj.getJSONArray("data");
                    List<IThread> lThread = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject ji = arr.getJSONObject(i);
                        lThread.add(new ImgurThread(
                                ji.getString("title"),
                                ji.getString("id"),
                                ji.getString("topic"),
                                ji.getString("account_url"),
                                ji.getLong("account_id"),
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
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                String album_id = "";
                try {
                    OkHttpClient client = SHttpClient.getInstance().getClient();
                    {
                        RequestBody body = new FormBody.Builder()
                                .add("title", URLEncoder.encode(Title, "UTF-8"))
                                .add("description", URLEncoder.encode(Desc, "UTF-8"))
                                .build();
                        Request request = new Request.Builder()
                                .url("https://api.imgur.com/3/album")
                                .post(body)
                                .addHeader("Authorization", "Bearer " + _token.getToken())
                                .build();
                        Response res = client.newCall(request).execute();
                        if (!res.isSuccessful()) {
                            Toast.makeText(_ctx, "Upload Failed auth", Toast.LENGTH_LONG).show();
                            return;
                        }
                        String sres = res.body().string();
                        album_id = new JSONObject(sres).getJSONObject("data").getString("id");
                    }
                    for (Bitmap img : images) {
                        RequestBody body = new FormBody.Builder()
                                .add("image", URLEncoder.encode(ImagesTools.toBase64(img), "UTF-8"))
                                .add("name", URLEncoder.encode(Title + ".jpg", "UTF-8"))
                                .add("album", album_id)
                                .build();
                        Request request = new Request.Builder()
                                .url("https://api.imgur.com/3/upload")
                                .post(body)
                                .addHeader("Authorization", "Bearer " + _token.getToken())
                                .build();
                        Response res = client.newCall(request).execute();
                        if (!res.isSuccessful()) {
                            Handler handler = new Handler(_ctx.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(_ctx, "Upload Failed up", Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }
                    }
                    Handler handler = new Handler(_ctx.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(_ctx, "Upload Successful", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (IOException | JSONException ignored) {
                }
            }
        });
        th.start();
    }

    @Override
    public Bitmap getIcon() {
        return BitmapFactory.decodeResource(_ctx.getResources(), R.drawable.ic_imgur);
    }

    @Override
    public void getFavs(int page, IThread.GetThreadCallback callback) {
        List<ImgurFav> fav = ImgurFav.listAll(ImgurFav.class);
        List<IThread> favs = new ArrayList<>();
        for (ImgurFav f : fav) {
            Log.d("ImgurFav", "getFavs: " + f.getId());
            ImgurThread th = f.getThread();
            favs.add(th.UpdateToken(_token));
        }
        callback.onGetThreadComplete(favs);
    }

    @Override
    public String getName() {
        return ("Imgur");
    }

}
