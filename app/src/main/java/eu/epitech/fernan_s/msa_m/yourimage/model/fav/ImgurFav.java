package eu.epitech.fernan_s.msa_m.yourimage.model.fav;

import com.orm.SugarRecord;

import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.ImgurThread;

/**
 * Created by quent on 08/02/2017.
 */

public class ImgurFav extends SugarRecord {
    private ImgurThread _Thread;
    private String _ThreadID;

    public ImgurFav(ImgurThread thread) {
        _Thread = thread;
        _ThreadID = thread.getID();
    }

    public ImgurThread getThread() {
        return _Thread;
    }

    public String getID() {
        return _ThreadID;
    }
}
