package eu.epitech.fernan_s.msa_m.yourimage.model.thread;

import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.model.api.IApi;
import eu.epitech.fernan_s.msa_m.yourimage.model.fav.PX500Fav;
import eu.epitech.fernan_s.msa_m.yourimage.model.image.PX500Image;
import eu.epitech.fernan_s.msa_m.yourimage.model.image.IImage;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.IToken;
import eu.epitech.fernan_s.msa_m.yourimage.model.user.PX500User;
import eu.epitech.fernan_s.msa_m.yourimage.model.user.IUser;
import eu.epitech.fernan_s.msa_m.yourimage.singleton.SHttpClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Table
public class PX500Thread implements IThread {
    Long id;
    String _id;
    String _title;
    String _desc;
    String _authorName;
    long _authorId;
    String _Type = "500px";
    String _token;


    public PX500Thread(){}

    public PX500Thread(String title, String id, String desc, String authorName, long authorId, IApi api) {
        _title = title;
        _id = id;
        _desc = desc;
        _authorId = authorId;
        _authorName = authorName;
        _token = api.getToken().getToken();
    }

    @Override
    public void getImages(final IImage.getImageCallback callback) {
        OkHttpClient client = SHttpClient.getInstance().getClient();
        Request request = new Request.Builder().url("https://api.500px.com/v1/photos/" + _id + "?image_size=4&comments=1").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                try {
                    JSONObject obj = new JSONObject(str);
                    JSONArray arr = obj.getJSONArray("photo");
                    List<IImage> limage = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        limage.add(new PX500Image(arr.getJSONObject(i).getString("image_url"),
                                arr.getJSONObject(i).getString("name"),
                                arr.getJSONObject(i).getString("description")));
                    }
                    callback.onGetImageFinished(limage);
                } catch (JSONException e) {
                    return;
                }
            }
        });
    }

    @Override
    public void getAuthor(IUser.GetUserCallback callback) {
        callback.OnGetUserFinished(new PX500User("", _authorName));
    }

    public PX500Thread UpdateToken(IToken token) {
        _token = token.getToken();
        return this;
    }

    @Override
    public String getTitle() {
        return _title;
    }

    @Override
    public String getDesc() {
        return _desc;
    }

    @Override
    public String getType() {
        return _Type;
    }

    @Override
    public String getID() {
        return _id;
    }

    public void setId(long aid) {
        id = aid;
    }

    @Override
    public void fav() {
        PX500Fav fav = new PX500Fav(this);
        fav.save();
    }

    @Override
    public void unfav() {
        long id = Long.parseLong(_id, 36);
        PX500Fav favs = PX500Fav.findById(PX500Fav.class, id);
        if (favs != null) {
            SugarRecord.delete(favs.getThread());
            favs.delete();
        }
    }

    @Override
    public boolean isFav() {
        long id = Long.parseLong(_id, 36);
        PX500Fav favs = PX500Fav.findById(PX500Fav.class, id);
        if (favs != null) {
            Log.d("PX500Thread", "isFav: " + favs.getId());
        }
        return favs != null;
    }

    @Override
    public String ShareLink() {
        return "http://500px.com/photo/" + _id;
    }
}
