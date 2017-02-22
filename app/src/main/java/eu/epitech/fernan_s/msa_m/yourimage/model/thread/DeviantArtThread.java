package eu.epitech.fernan_s.msa_m.yourimage.model.thread;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

import java.util.ArrayList;
import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.model.fav.DeviantArtFav;
import eu.epitech.fernan_s.msa_m.yourimage.model.fav.FlickrFav;
import eu.epitech.fernan_s.msa_m.yourimage.model.image.DeviantArtImage;
import eu.epitech.fernan_s.msa_m.yourimage.model.image.IImage;
import eu.epitech.fernan_s.msa_m.yourimage.model.user.IUser;

/**
 * Created by quent on 14/02/2017.
 */

@Table
public class DeviantArtThread implements IThread {
    String _title;
    String _desc;
    String _id;
    String _Type = "Deviant";
    String _pic;
    String _url;
    private Long id;

    public DeviantArtThread(String title, String desc, String id, String pic, String URL) {
        _title = title;
        _desc = desc;
        _id = id;
        _pic = pic;
        _url = URL;
    }

    @Override
    public void getImages(IImage.getImageCallback callback) {
        List<IImage> images = new ArrayList<>();
        images.add(new DeviantArtImage(_pic, _title, ""));
        callback.onGetImageFinished(images);
    }

    @Override
    public void getAuthor(IUser.GetUserCallback callback) {

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

    @Override
    public void fav() {
        DeviantArtFav fav = new DeviantArtFav(this);
        fav.save();
    }

    public DeviantArtThread() {

    }

    @Override
    public void unfav() {
        long id = 0;
        byte[] bytes = getID().getBytes();
        for (int i = 0; i < bytes.length; i++) {
            id ^= bytes[i] << ((i % 8) * 8);
        }
        DeviantArtFav favs = DeviantArtFav.findById(DeviantArtFav.class, id);
        if (favs != null) {
            SugarRecord.delete(favs.getThread());
            favs.delete();
        }
    }

    public void setId(long aid) {
        id = aid;
    }

    @Override
    public boolean isFav() {
        long id = 0;
        byte[] bytes = getID().getBytes();
        for (int i = 0; i < bytes.length; i++) {
            id ^= bytes[i] << ((i % 8) * 8);
        }
        DeviantArtFav favs = FlickrFav.findById(DeviantArtFav.class, id);
        return favs != null;
    }

    @Override
    public String ShareLink() {
        return _url;
    }
}
