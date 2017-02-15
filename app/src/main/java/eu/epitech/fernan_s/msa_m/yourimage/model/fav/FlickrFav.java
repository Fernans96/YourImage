package eu.epitech.fernan_s.msa_m.yourimage.model.fav;

import com.orm.SugarApp;
import com.orm.SugarDb;
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
        long threadid = Long.parseLong(_thread.getID(), 10);
        _thread.setId(threadid);
        SugarRecord.save(_thread);
        this.setId(threadid);
    }

    public FlickrThread getThread() {
        return SugarRecord.findById(FlickrThread.class, getId());
    }
}
