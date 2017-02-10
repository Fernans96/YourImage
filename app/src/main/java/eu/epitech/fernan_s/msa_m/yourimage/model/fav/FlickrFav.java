package eu.epitech.fernan_s.msa_m.yourimage.model.fav;

import com.orm.SugarRecord;

import eu.epitech.fernan_s.msa_m.yourimage.model.thread.FlickrThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.ImgurThread;

/**
 * Created by quent on 08/02/2017.
 */

public class FlickrFav extends SugarRecord {
    FlickrThread _thread;

    public FlickrFav() {}

    public FlickrFav(FlickrThread thread) {
        _thread = thread;
        this.setId(Long.parseLong(_thread.getID(), 36));
    }

    public FlickrThread getThread() {
        return _thread;
    }
}
