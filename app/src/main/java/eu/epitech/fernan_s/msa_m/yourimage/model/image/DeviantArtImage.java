package eu.epitech.fernan_s.msa_m.yourimage.model.image;

import com.bumptech.glide.load.model.GlideUrl;

/**
 * Created by quent on 31/01/2017.
 */

public class DeviantArtImage implements IImage {
    public String _link;
    public String _title;
    public String _desc;

    public DeviantArtImage(String link, String title, String desc) {
        _link = link;
        _title = title;
        _desc = desc;
    }

    @Override
    public GlideUrl getLink() {
        return new GlideUrl(_link);
    }

    @Override
    public String getTitle() {
        return _title;
    }

    @Override
    public String getDesc() {
        return _desc;
    }
}
