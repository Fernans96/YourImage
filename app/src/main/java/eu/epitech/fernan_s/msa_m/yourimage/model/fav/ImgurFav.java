package eu.epitech.fernan_s.msa_m.yourimage.model.fav;

import com.orm.SugarRecord;

import eu.epitech.fernan_s.msa_m.yourimage.model.thread.ImgurThread;

/**
 * Created by quent on 08/02/2017.
 */

public class ImgurFav extends SugarRecord {
    ImgurThread _thread;

    public ImgurFav() {
    }

    public ImgurFav(ImgurThread thread) {
        _thread = thread;
        long threadid = Long.parseLong(_thread.getID(), 36);
        _thread.setId(threadid);
        SugarRecord.save(_thread);
        this.setId(threadid);
    }

    public ImgurThread getThread() {
        return SugarRecord.findById(ImgurThread.class, getId());
    }
}
