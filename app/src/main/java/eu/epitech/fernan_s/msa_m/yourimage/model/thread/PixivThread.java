package eu.epitech.fernan_s.msa_m.yourimage.model.thread;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.model.fav.ImgurFav;
import eu.epitech.fernan_s.msa_m.yourimage.model.fav.PixivFav;
import eu.epitech.fernan_s.msa_m.yourimage.model.image.IImage;
import eu.epitech.fernan_s.msa_m.yourimage.model.image.PixivImage;
import eu.epitech.fernan_s.msa_m.yourimage.model.user.IUser;

/**
 * Created by quent on 14/02/2017.
 */

public class PixivThread implements IThread {
    String _title;
    String _desc;
    String _id;
    String _Type = "Pixiv";
    String _pic;
    int _page_nbr;

    public PixivThread(String title, String desc, String id, String pic, int PageNbr) {
        _title = title;
        _desc = desc;
        _id = id;
        _pic = pic;
        _page_nbr = PageNbr;
    }

    @Override
    public void getImages(IImage.getImageCallback callback) {
        List<IImage> images = new ArrayList<>();
        for (int i = 0; i <= _page_nbr; i++) {
            images.add(new PixivImage(_pic.replace("p0", "p" + i), _title, _desc));
        }
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
        PixivFav fav = new PixivFav(this);
        fav.save();
    }

    @Override
    public void unfav() {
        long id = Long.parseLong(_id, 10);
        PixivFav favs = PixivFav.findById(PixivFav.class, id);
        if (favs != null)
            favs.delete();
    }

    @Override
    public boolean isFav() {
        long id = Long.parseLong(_id, 10);
        PixivFav favs = PixivFav.findById(PixivFav.class, id);
        if (favs != null) {
            Log.d("BLAAAA", "isFav: " + favs.getId());
        }
        return favs != null;
    }
}
