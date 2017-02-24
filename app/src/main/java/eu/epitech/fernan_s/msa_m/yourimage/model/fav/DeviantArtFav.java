package eu.epitech.fernan_s.msa_m.yourimage.model.fav;

import com.orm.SugarRecord;

import eu.epitech.fernan_s.msa_m.yourimage.model.thread.DeviantArtThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.ImgurThread;

/**
 * Created by quent on 08/02/2017.
 */

public class DeviantArtFav extends SugarRecord {
    DeviantArtThread _thread;

    public DeviantArtFav() {

    }

    public DeviantArtFav(DeviantArtThread thread) {
        _thread = thread;
        long id = 0;
        byte[] bytes = thread.getID().getBytes();
        for (int i = 0; i < bytes.length; i++) {
            id ^= bytes[i] << ((i % 8) * 8);
        }
        _thread.setId(id);
        SugarRecord.save(_thread);
        this.setId(id);
    }

    public DeviantArtThread getThread() {
        return SugarRecord.findById(DeviantArtThread.class, getId());
    }
}
