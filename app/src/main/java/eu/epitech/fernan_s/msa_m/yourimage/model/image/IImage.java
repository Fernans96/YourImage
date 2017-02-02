package eu.epitech.fernan_s.msa_m.yourimage.model.image;

import java.util.List;

/**
 * Created by quent on 30/01/2017.
 */

public interface IImage {
    public String getLink();
    public String getTitle();
    public String getDesc();

    public interface getImageCallback {
        public void onGetImageFinished(List<IImage> lThread);
    }
}
