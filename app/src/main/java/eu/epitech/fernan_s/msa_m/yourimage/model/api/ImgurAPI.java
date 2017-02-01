package eu.epitech.fernan_s.msa_m.yourimage.model.api;

import android.content.Context;
import android.support.test.espresso.core.deps.guava.base.Splitter;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.epitech.fernan_s.msa_m.yourimage.SHttpClient;
import eu.epitech.fernan_s.msa_m.yourimage.dialog.AuthDialog;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.ImgurThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.IToken;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.ImgurToken;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by quent on 30/01/2017.
 */

public class ImgurAPI implements IApi {
    private static String _client_id = "47bb2cf28f17756";
    private static String _secret = "aef3b44d9985f67134e0ab82bbaddb2e7d95f7f8";
    private static String _searchLink = "https://api.imgur.com/3/gallery/search/time/";
    private Context _ctx = null;
    private IToken token = null;

    public ImgurAPI(Context ctx) {
        _ctx = ctx;
        String str = _ctx.getSharedPreferences("tokens",0).getString("ImgurToken",null);
        if (str != null) {
            try {
                token = ImgurToken.Parse(new JSONObject(str));
                Log.d("TAG", "ImgurAPI: " + token.getToken());
                Log.d("TAG", "ImgurAPI: " + token.getAuthType());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getAuthlink() {
        String ret = "https://api.imgur.com/oauth2/authorize?"
                + "client_id=" + _client_id
                + "&response_type=" + "token"
                + "&state=" + "";
        return ret;
    }

    @Override
    public void connect(Context ctx) {
        if (token == null) {
            AuthDialog diag = new AuthDialog(ctx, this);
            diag.show();
        } else {
            Toast.makeText(_ctx, "Already auth", Toast.LENGTH_LONG).show();
            getThread(0, new IThread.GetThreadCallback() {
                @Override
                public void onGetThreadComplete(List<IThread> lThread) {
                    for (IThread thread : lThread) {
                        Log.d("test", "onGetThreadComplete: " + thread.getTitle());
                    }
                }
            });
        }
    }

    @Override
    public void auth(String query) {
        String[] ret = query.split("#");
        Map<String, String> map = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(ret[ret.length - 1]);
        token = new ImgurToken(map);
        _ctx.getSharedPreferences("tokens",0).edit().putString("ImgurToken", token.ToJson().toString()).apply();
        Toast.makeText(_ctx, "Auth Succeed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void getThread(int page, final IThread.GetThreadCallback callback) {
        OkHttpClient client = SHttpClient.getInstance().getClient();
        Request request = new Request.Builder().url(_searchLink + page + "?q_any=title:").addHeader("Authorization", "Bearer " + token.getToken()).build();
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
                                ji.getLong("account_id")
                        ));
                    }
                    callback.onGetThreadComplete(lThread);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void getThread(String tags, int page, final IThread.GetThreadCallback callback) {
        OkHttpClient client = SHttpClient.getInstance().getClient();
        Request request = new Request.Builder().url(_searchLink + page + "?q_any=title:" + tags).addHeader("Authorization", "Bearer " + token.getToken()).build();
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
                                ji.getLong("account_id")
                        ));
                    }
                    callback.onGetThreadComplete(lThread);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void SendPic(String pic) {

    }

    @Override
    public void Fav(int id) {

    }

    @Override
    public void unFav(int id) {

    }

    @Override
    public void getFavs(int page, IThread.GetThreadCallback callback) {

    }

}
