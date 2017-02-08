package eu.epitech.fernan_s.msa_m.yourimage.model.fav;

import com.orm.SugarRecord;

import eu.epitech.fernan_s.msa_m.yourimage.model.thread.FlickrThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.ImgurThread;

/**
 * Created by quent on 08/02/2017.
 */

public class FlickrFav extends SugarRecord {
    private FlickrThread _Thread;
    private String _ThreadID;

    public FlickrFav(FlickrThread thread) {
        _Thread = thread;
        _ThreadID = thread.getID();
    }

    public FlickrThread getThread() {
        return _Thread;
    }

    public String getID() {
        return _ThreadID;
    }
}
