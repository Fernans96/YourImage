package eu.epitech.fernan_s.msa_m.yourimage.model.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.dialog.UserPassDialog;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;
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
    public void getThread(int page, IThread.GetThreadCallback callback) {

    }

    @Override
    public void getThread(String tags, int page, IThread.GetThreadCallback callback) {

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

    }

    @Override
    public String getName() {
        return "Pixiv";
    }
}
