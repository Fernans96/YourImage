package eu.epitech.fernan_s.msa_m.yourimage.model.thread;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.model.fav.FlickrFav;
import eu.epitech.fernan_s.msa_m.yourimage.model.image.FlickrImage;
import eu.epitech.fernan_s.msa_m.yourimage.model.image.IImage;
import eu.epitech.fernan_s.msa_m.yourimage.model.user.FlickrUser;
import eu.epitech.fernan_s.msa_m.yourimage.model.user.IUser;
import eu.epitech.fernan_s.msa_m.yourimage.singleton.SHttpClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static eu.epitech.fernan_s.msa_m.yourimage.model.api.FlickrAPI.CONSUMER_KEY;

/**
 * Created by quent on 05/02/2017.
 */

@Table
public class FlickrThread implements IThread {
    String _owner_id;
    String _id;
    String _title;
    String _Type = "Flickr";
    private Long id;

    public FlickrThread(String owner_id, String id, String title) {
        _id = id;
        _owner_id = owner_id;
        _title = title;
    }

    @Override
    public void getImages(final IImage.getImageCallback callback) {
        String url = "https://api.flickr.com/services/rest/?";
        OkHttpClient client = SHttpClient.getInstance().getClient();
        Request request = new Request.Builder().url(url + "method=flickr.photos.getInfo&api_key=" + CONSUMER_KEY + "&format=json&photo_id=" + _id).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                str = str.substring(14, str.length() - 1);
                try {
                    JSONObject photo = new JSONObject(str).getJSONObject("photo");
                    String link = "https://farm" +
                            photo.getInt("farm") +
                            ".staticflickr.com/" +
                            photo.getString("server") +
                            "/" + _id + "_" +
                            photo.getString("secret") + ".jpg";
                    FlickrImage img = new FlickrImage(
                            link,
                            photo.getJSONObject("title").getString("_content"),
                            photo.getJSONObject("description").getString("_content")
                    );
                    List<IImage> lim = new ArrayList<IImage>();
                    lim.add(img);
                    callback.onGetImageFinished(lim);
                } catch (JSONException e) {
                }
            }
        });
    }

    @Override
    public void getAuthor(IUser.GetUserCallback callback) {
        callback.OnGetUserFinished(new FlickrUser("", _owner_id));
    }


    @Override
    public void fav() {
        FlickrFav fav = new FlickrFav(this);
        fav.save();
    }

    public FlickrThread() {

    }

    @Override
    public void unfav() {
        long id = Long.parseLong(_id, 10);
        FlickrFav favs = FlickrFav.findById(FlickrFav.class, id);
        if (favs != null) {
            SugarRecord.delete(favs.getThread());
            favs.delete();
        }
    }

    @Override
    public boolean isFav() {
        long id = Long.parseLong(_id, 10);
        FlickrFav favs = FlickrFav.findById(FlickrFav.class, id);
        return favs != null;
    }

    @Override
    public String ShareLink() {
        return "https://www.flickr.com/photos/" + _owner_id + "/" + _id;
    }

    @Override
    public String getTitle() {
        return _title;
    }

    @Override
    public String getDesc() {
        return "FlickrPictures";
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
}
