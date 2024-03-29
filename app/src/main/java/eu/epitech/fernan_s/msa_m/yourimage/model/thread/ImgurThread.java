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
import eu.epitech.fernan_s.msa_m.yourimage.model.fav.ImgurFav;
import eu.epitech.fernan_s.msa_m.yourimage.model.image.IImage;
import eu.epitech.fernan_s.msa_m.yourimage.model.image.ImgurImage;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.IToken;
import eu.epitech.fernan_s.msa_m.yourimage.model.user.IUser;
import eu.epitech.fernan_s.msa_m.yourimage.model.user.ImgurUser;
import eu.epitech.fernan_s.msa_m.yourimage.singleton.SHttpClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by quent on 31/01/2017.
 */

@Table
public class ImgurThread implements IThread {
    Long id;
    String _id;
    String _title;
    String _desc;
    String _authorName;
    long _authorId;
    String _Type = "Imgur";
    String _token;


    public ImgurThread(){}

    public ImgurThread(String title, String id, String desc, String authorName, long authorId, IApi api) {
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
        Request request = new Request.Builder().url("https://api.imgur.com/3/album/" + _id + "/images").addHeader("Authorization", "Bearer " + _token).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                try {
                    JSONObject obj = new JSONObject(str);
                    JSONArray arr = obj.getJSONArray("data");
                    List<IImage> limage = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        limage.add(new ImgurImage(arr.getJSONObject(i).getString("link"),
                                arr.getJSONObject(i).getString("title"),
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
        callback.OnGetUserFinished(new ImgurUser("", _authorName));
    }

    public ImgurThread UpdateToken(IToken token) {
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
        ImgurFav fav = new ImgurFav(this);
        fav.save();
    }

    @Override
    public void unfav() {
        long id = Long.parseLong(_id, 36);
        ImgurFav favs = ImgurFav.findById(ImgurFav.class, id);
        if (favs != null) {
            SugarRecord.delete(favs.getThread());
            favs.delete();
        }
    }

    @Override
    public boolean isFav() {
        long id = Long.parseLong(_id, 36);
        ImgurFav favs = ImgurFav.findById(ImgurFav.class, id);
        if (favs != null) {
            Log.d("BLAAAA", "isFav: " + favs.getId());
        }
        return favs != null;
    }

    @Override
    public String ShareLink() {
        return "http://imgur.com/gallery/" + _id;
    }
}
