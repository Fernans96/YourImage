package eu.epitech.fernan_s.msa_m.yourimage.model.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.dialog.AuthDialog;
import eu.epitech.fernan_s.msa_m.yourimage.model.fav.DeviantArtFav;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.DeviantArtThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.DeviantArtToken;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.IToken;
import eu.epitech.fernan_s.msa_m.yourimage.singleton.SHttpClient;
import eu.epitech.fernan_s.msa_m.yourimage.tools.ImagesTools;
import eu.epitech.fernan_s.msa_m.yourimage.tools.RequestTool;
import oauth.signpost.http.HttpParameters;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by quent on 21/02/2017.
 */

public class DeviantArtApi implements IApi {
    IToken _token;
    Context _ctx;
    private static String _clientID = "5842";
    private static String _client_secret = "ecddb3eb86763e5f98b50c91a405b821";
    OkHttpClient _client = SHttpClient.getInstance().getClient();

    public DeviantArtApi(Context ctx) {
        _ctx = ctx;
        String str = _ctx.getSharedPreferences("tokens", 0).getString("DeviantToken", null);
        if (str != null) {
            try {
                _token = DeviantArtToken.Parse(new JSONObject(str));
                Log.d("Token", "DeviantArtApi: " + _token.getToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void getAuthlink(AuthLinkCallback callback) {
        String ret = "https://www.deviantart.com/oauth2/authorize"
                + "?response_type=" + "code"
                + "&client_id=" + _clientID
                + "&redirect_uri=" + "http://127.0.0.1/"
                + "&scope=user browse feed stash publish";
        callback.onAuthLinkFinished(ret);
    }

    @Override
    public void connect(Context ctx, ConnectCallback callback) {
        AuthDialog diag = new AuthDialog(ctx, this, callback);
        diag.show();
    }

    @Override
    public void auth(String query, final ConnectCallback callback) {
        Request request = new Request.Builder()
                .get()
                .url("https://www.deviantart.com/oauth2/token"
                        + "?client_id=" + _clientID
                        + "&client_secret=" + _client_secret
                        + "&grant_type=authorization_code"
                        + "&code=" + query.substring("http://127.0.0.1/?code=".length())
                        + "&redirect_uri=" + "http://127.0.0.1/")
                .build();
        _client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onConnectFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Not success");
                }
                try {
                    _token = DeviantArtToken.Parse(new JSONObject(response.body().string()));
                    _ctx.getSharedPreferences("tokens", 0).edit().putString("DeviantToken", _token.ToJson().toString()).apply();
                    callback.onConnectSuccess();
                } catch (JSONException e) {
                    throw new IOException("Not success");
                }
            }
        });
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
        _token = null;
        _ctx.getSharedPreferences("tokens", 0).edit().putString("DeviantToken", null).apply();
    }

    @Override
    public void getThread(int page, final IThread.GetThreadCallback callback) {
        Request request = new Request.Builder()
                .get()
                .url("https://www.deviantart.com/api/v1/oauth2/browse/hot"
                        + "?offset=" + (page * 24)
                        + "&limit=" + 24
                        + "&access_token=" + _token.getToken())
                .build();
        _client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Error");
                String str = response.body().string();
                try {
                    JSONArray array = new JSONObject(str).getJSONArray("results");
                    List<IThread> threads = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        if (obj.has("content"))
                            threads.add(new DeviantArtThread(
                                    obj.getString("title"),
                                    "",
                                    obj.getString("deviationid"),
                                    obj.getJSONObject("content").getString("src"),
                                    obj.getString("url")));
                    }
                    callback.onGetThreadComplete(threads);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IOException("Error");
                }
            }
        });
    }

    @Override
    public void getThread(String tags, int page, final IThread.GetThreadCallback callback) {
        Request request = new Request.Builder()
                .get()
                .url("https://www.deviantart.com/api/v1/oauth2/browse/tags?tag=" + tags
                        + "&offset=" + (page * 24)
                        + "&limit=" + 24
                        + "&access_token=" + _token.getToken())
                .build();
        _client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Error");
                String str = response.body().string();
                try {
                    JSONArray array = new JSONObject(str).getJSONArray("results");
                    List<IThread> threads = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        threads.add(new DeviantArtThread(
                                obj.getString("title"),
                                "",
                                obj.getString("deviationid"),
                                obj.getJSONObject("content").getString("src"),
                                obj.getString("url")));
                    }
                    callback.onGetThreadComplete(threads);
                } catch (Exception e) {
                    throw new IOException("Error");
                }
            }
        });
    }

    @Override
    public void SendPic(final String Title, final String Desc, final List<Bitmap> images, final SendPictureCallback callback) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("https://www.deviantart.com/api/v1/oauth2/gallery/folders?access_token=" + _token.getToken())
                            .get()
                            .build();
                    Response res = _client.newCall(request).execute();
                    String str = res.body().string();
                    JSONObject obj = new JSONObject(str);
                    String folder_id = obj.getJSONArray("results").getJSONObject(0).getString("folderid");
                    String url = "https://www.deviantart.com/api/v1/oauth2/stash/submit?access_token=" + _token.getToken();
                    HttpParameters params = new HttpParameters();
                    params.put("title", Title);
                    params.put("artist_comments", Desc);
                    str = RequestTool.POSTRequest(url, params, ImagesTools.toByteArray(images.get(0)));
                    Log.d("TAG", "run: " + str);
                    obj = new JSONObject(str);
                    String ItemID = obj.getString("itemid");
                    RequestBody body = new FormBody.Builder()
                            .add("is_mature", "false")
                            .add("agree_submission", "true")
                            .add("agree_tos", "true")
                            .add("allow_comments", "false")
                            .add("catpath", "/digitalart/mixedmed/other")
                            .add("itemid", ItemID)
                            .add("access_token", _token.getToken())
                            .add("feature", "false")
                            .add("sharing", "allow")
                            .add("galleryids", folder_id)
                            .build();
                    request = new Request.Builder()
                            .url("https://www.deviantart.com/api/v1/oauth2/stash/publish?access_token=" + _token.getToken())
                            .post(body)
                            .build();
                    res = _client.newCall(request).execute();
                    str = res.body().string();
                    Log.d("TAG", "run: " + str);
                    if (!res.isSuccessful())
                        throw new Exception("Can't Upload");
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
        return BitmapFactory.decodeResource(_ctx.getResources(), R.drawable.ic_deviant);
    }

    @Override
    public void getFavs(int page, IThread.GetThreadCallback callback) {
        List<DeviantArtFav> fav = DeviantArtFav.listAll(DeviantArtFav.class);
        List<IThread> favs = new ArrayList<>();
        for (DeviantArtFav f : fav) {
            favs.add(f.getThread());
        }
        callback.onGetThreadComplete(favs);
    }

    @Override
    public void getUserThread(final int page, final IThread.GetThreadCallback callback) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Request request = new Request.Builder()
                            .url("https://www.deviantart.com/api/v1/oauth2/gallery/folders?access_token=" + _token.getToken())
                            .get()
                            .build();
                    Response response = _client.newCall(request).execute();
                    String str = response.body().string();
                    JSONObject obj = new JSONObject(str);
                    String folder_id = obj.getJSONArray("results").getJSONObject(0).getString("folderid");
                    if (!response.isSuccessful())
                        throw new IOException("Error");
                    request = new Request.Builder()
                            .get()
                            .url("https://www.deviantart.com/api/v1/oauth2/gallery/" + folder_id
                                    + "?access_token=" + _token.getToken()
                                    + "&offset=" + (page * 24)
                                    + "&limit=" + 24)
                            .build();
                    response = _client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Error");
                    str = response.body().string();
                    try {
                        JSONArray array = new JSONObject(str).getJSONArray("results");
                        List<IThread> threads = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            obj = array.getJSONObject(i);
                            threads.add(new DeviantArtThread(
                                    obj.getString("title"),
                                    "",
                                    obj.getString("deviationid"),
                                    obj.getJSONObject("content").getString("src"),
                                    obj.getString("url")));
                        }
                        callback.onGetThreadComplete(threads);
                    } catch (Exception e) {
                        throw new IOException("Error");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }

    @Override
    public String getName() {
        return "Deviant";
    }

    @Override
    public boolean CanUpload() {
        return true;
    }
}
