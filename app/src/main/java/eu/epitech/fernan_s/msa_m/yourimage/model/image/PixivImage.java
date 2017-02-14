package eu.epitech.fernan_s.msa_m.yourimage.model.image;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

/**
 * Created by quent on 14/02/2017.
 */

public class PixivImage implements IImage {
    String _link;
    String _title;
    String _desc;

    public PixivImage(String link, String title, String desc) {
        _link = link;
        _title = title;
        _desc = desc;
    }


    @Override
    public GlideUrl getLink() {
        return new GlideUrl(_link,
                new LazyHeaders.Builder()
                        .addHeader("Referer","http://www.pixiv.net/")
                        .build());
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
