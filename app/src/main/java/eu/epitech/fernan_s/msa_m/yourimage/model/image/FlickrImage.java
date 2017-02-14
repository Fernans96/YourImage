package eu.epitech.fernan_s.msa_m.yourimage.model.image;

import com.bumptech.glide.load.model.GlideUrl;

import eu.epitech.fernan_s.msa_m.yourimage.model.thread.FlickrThread;

/**
 * Created by quent on 05/02/2017.
 */

public class FlickrImage implements IImage {
    private String _Link;
    private String _Title;
    private String _Desc;

    public FlickrImage(String link, String title, String desc) {
        _Link = link;
        _Title = title;
        _Desc = desc;
    }

    @Override
    public GlideUrl getLink() {
        return new GlideUrl(_Link);
    }

    @Override
    public String getTitle() {
        return _Title;
    }

    @Override
    public String getDesc() {
        return _Desc;
    }
}
