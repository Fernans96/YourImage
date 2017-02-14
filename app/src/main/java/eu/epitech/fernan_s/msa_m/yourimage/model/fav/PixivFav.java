package eu.epitech.fernan_s.msa_m.yourimage.model.fav;

import com.orm.SugarRecord;

import eu.epitech.fernan_s.msa_m.yourimage.model.thread.FlickrThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.PixivThread;

/**
 * Created by quent on 08/02/2017.
 */

public class PixivFav extends SugarRecord {
    PixivThread _thread;

    public PixivFav() {}

    public PixivFav(PixivThread thread) {
        _thread = thread;
        this.setId(Long.parseLong(_thread.getID(), 10));
    }

    public PixivThread getThread() {
        return _thread;
    }
}
