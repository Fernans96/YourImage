package eu.epitech.fernan_s.msa_m.yourimage.model.fav;

import android.util.Log;

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
        long threadid = Long.parseLong(_thread.getID(), 10);
        _thread.setId(threadid);
        SugarRecord.save(_thread);
        this.setId(threadid);
    }

    public PixivThread getThread() {
        return SugarRecord.findById(PixivThread.class, getId());
    }
}
