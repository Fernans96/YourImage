package eu.epitech.fernan_s.msa_m.yourimage.model.image;

import com.bumptech.glide.load.model.GlideUrl;

import java.util.List;

/**
 * Created by quent on 30/01/2017.
 */

public interface IImage {
    public GlideUrl getLink();
    public String getTitle();
    public String getDesc();

    public interface getImageCallback {
        public void onGetImageFinished(List<IImage> lThread);
    }
}
