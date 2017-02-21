package eu.epitech.fernan_s.msa_m.yourimage.model.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.dialog.UserPassDialog;
import eu.epitech.fernan_s.msa_m.yourimage.model.fav.PixivFav;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.PixivThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.IToken;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.PixivToken;
import eu.epitech.fernan_s.msa_m.yourimage.singleton.SHttpClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by quent on 13/02/2017.
 */

public class PixivAPI implements IApi {
    private IToken _token;
    private Context _ctx;
    private OkHttpClient _client = SHttpClient.getInstance().getClient();

    public PixivAPI(Context ctx) {
        _ctx = ctx;
        String str = _ctx.getSharedPreferences("tokens", 0).getString("PixivToken", null);
        if (str != null) {
            try {
                _token = PixivToken.Parse(new JSONObject(str));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getAuthlink(AuthLinkCallback callback) {

    }

    @Override
    public void connect(Context ctx, ConnectCallback callback) {
        UserPassDialog dialog = new UserPassDialog(ctx, this, callback);
        dialog.Show();
    }


    //Only for Instrumented Test (Synchronous HTTP call)
    public void SyncAuth(String username, String password) throws Exception {
        RequestBody body = new FormBody.Builder()
                .add("client_id", "bYGKuGVw91e0NMfPGp44euvGt59s")
                .add("client_secret", "HP3RmkgAmEGro0gn1x9ioawQE8WMfvLXDz3ZqxpK")
                .add("grant_type", "password")
                .add("username", username)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url("https://oauth.secure.pixiv.net/auth/token")
                .header("Referer", "http://www.pixiv.net/")
                .post(body)
                .build();
        Response resp = _client.newCall(request).execute();
        String ret = resp.body().string();
        JSONObject jobj = new JSONObject(ret);
        jobj = jobj.getJSONObject("response");
        _token = new PixivToken(
                jobj.getJSONObject("user").getString("name"),
                jobj.getString("access_token"),
                jobj.getString("refresh_token"),
                jobj.getString("token_type")
        );
        _ctx.getSharedPreferences("tokens", 0).edit().putString("PixivToken", _token.ToJson().toString()).apply();
    }

    @Override
    public void auth(String query, final ConnectCallback callback) {
        try {
            JSONObject obj = new JSONObject(query);
            RequestBody body = new FormBody.Builder()
                    .add("client_id", "bYGKuGVw91e0NMfPGp44euvGt59s")
                    .add("client_secret", "HP3RmkgAmEGro0gn1x9ioawQE8WMfvLXDz3ZqxpK")
                    .add("grant_type", "password")
                    .add("username", obj.getString("username"))
                    .add("password", obj.getString("password"))
                    .build();
            Request request = new Request.Builder()
                    .url("https://oauth.secure.pixiv.net/auth/token")
                    .header("Referer", "http://www.pixiv.net/")
                    .post(body)
                    .build();
            _client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onConnectFailed();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String ret = response.body().string();
                        try {
                            JSONObject jobj = new JSONObject(ret);
                            jobj = jobj.getJSONObject("response");
                            _token = new PixivToken(
                                    jobj.getJSONObject("user").getString("name"),
                                    jobj.getString("access_token"),
                                    jobj.getString("refresh_token"),
                                    jobj.getString("token_type")
                            );
                            callback.onConnectSuccess();
                            Handler handler = new Handler(_ctx.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    _ctx.getSharedPreferences("tokens", 0).edit().putString("PixivToken", _token.ToJson().toString()).apply();
                                }
                            });
                        } catch (JSONException e) {
                            callback.onConnectFailed();
                            e.printStackTrace();
                        }
                    } else {
                        callback.onConnectFailed();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onConnectFailed();
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

        _ctx.getSharedPreferences("tokens", 0).edit().putString("PixivToken", null).apply();
        _token = null;
    }

    @Override
    public void getThread(int page, final IThread.GetThreadCallback callback) {
        String url = "https://public-api.secure.pixiv.net/v1/trends/works.json"
                .concat("?page=" + (page + 1))
                .concat("&per_page=" + 45)
                .concat("&include_stats=" + "false")
                .concat("&image_sizes=" + "large")
                .concat("&profile_image_sizes=" + "px_170x170")
                .concat("&include_sanity_level=" + "false")
                .concat("&order=" + "desc")
                .concat("&sort=" + "date");
        Request request = new Request.Builder()
                .get()
                .url(url)
                .addHeader("UserAgent", "PixivIOSApp/5.8.3")
                .addHeader("Referer", "http://spapi.pixiv.net/")
                .addHeader("Authorization", "Bearer " + _token.getToken())
                .build();
        _client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject obj = new JSONObject(response.body().string());
                        JSONArray ret = obj.getJSONArray("response");
                        List<IThread> lthread = new ArrayList<>();
                        for (int i = 0; i < ret.length(); i++) {
                            JSONObject current = ret.getJSONObject(i);
                            lthread.add(new PixivThread(current.getString("title"),
                                    "",
                                    current.getString("id"),
                                    current.getJSONObject("image_urls").getString("large"),
                                    current.getInt("page_count")));
                        }
                        callback.onGetThreadComplete(lthread);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //Only for Instrumented Test (Synchronous HTTP call)
    public List<PixivThread> Sync_GetThread(int page) throws Exception {
        String url = "https://public-api.secure.pixiv.net/v1/trends/works.json"
                .concat("?page=" + (page + 1))
                .concat("&per_page=" + 45)
                .concat("&include_stats=" + "false")
                .concat("&image_sizes=" + "large")
                .concat("&profile_image_sizes=" + "px_170x170")
                .concat("&include_sanity_level=" + "false")
                .concat("&order=" + "desc")
                .concat("&sort=" + "date");
        Request request = new Request.Builder()
                .get()
                .url(url)
                .addHeader("UserAgent", "PixivIOSApp/5.8.3")
                .addHeader("Referer", "http://spapi.pixiv.net/")
                .addHeader("Authorization", "Bearer " + _token.getToken())
                .build();
        Response resp = _client.newCall(request).execute();
        String reta = resp.body().string();
        JSONObject obj = new JSONObject(reta);
        JSONArray ret = obj.getJSONArray("response");
        List<PixivThread> lthread = new ArrayList<>();
        for (int i = 0; i < ret.length(); i++) {
            JSONObject current = ret.getJSONObject(i);
            lthread.add(new PixivThread(current.getString("title"),
                    "",
                    current.getString("id"),
                    current.getJSONObject("image_urls").getString("large"),
                    current.getInt("page_count")));
        }
        return lthread;
    }

    @Override
    public void getThread(String tags, int page, final IThread.GetThreadCallback callback) {

        String url = null;
        try {
            url = "https://public-api.secure.pixiv.net/v1/search/works.json"
                    .concat("?page=" + (page + 1))
                    .concat("&per_page=" + 45)
                    .concat("&include_stats=" + "false")
                    .concat("&image_sizes=" + "large")
                    .concat("&profile_image_sizes=" + "px_170x170")
                    .concat("&include_sanity_level=" + "false")
                    .concat("&order=" + "desc")
                    .concat("&sort=" + "date")
                    .concat("&mode=" + "tag")
                    .concat("&period=" + "all")
                    .concat("&q=" + URLEncoder.encode(tags, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .get()
                .url(url)
                .addHeader("UserAgent", "PixivIOSApp/5.8.3")
                .addHeader("Referer", "http://spapi.pixiv.net/")
                .addHeader("Authorization", "Bearer " + _token.getToken())
                .build();
        _client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject obj = new JSONObject(response.body().string());
                        JSONArray ret = obj.getJSONArray("response");
                        List<IThread> lthread = new ArrayList<>();
                        for (int i = 0; i < ret.length(); i++) {
                            JSONObject current = ret.getJSONObject(i);
                            lthread.add(new PixivThread(current.getString("title"),
                                    "",
                                    current.getString("id"),
                                    current.getJSONObject("image_urls").getString("large"),
                                    current.getInt("page_count")));
                        }
                        callback.onGetThreadComplete(lthread);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void SendPic(String Title, String Desc, List<Bitmap> images) {

    }

    @Override
    public Bitmap getIcon() {
        return BitmapFactory.decodeResource(_ctx.getResources(), R.drawable.ic_pixiv);
    }

    @Override
    public void getFavs(int page, IThread.GetThreadCallback callback) {
        List<PixivFav> fav = PixivFav.listAll(PixivFav.class);
        List<IThread> favs = new ArrayList<>();
        for (PixivFav f : fav) {
            favs.add(f.getThread());
        }
        callback.onGetThreadComplete(favs);
    }

    @Override
    public void getUserThread(int page, IThread.GetThreadCallback callback) {

    }

    @Override
    public String getName() {
        return "Pixiv";
    }

    @Override
    public boolean CanUpload() {
        return false;
    }
}
