package eu.epitech.fernan_s.msa_m.yourimage.model.thread;

import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.model.image.IImage;
import eu.epitech.fernan_s.msa_m.yourimage.model.user.IUser;

/**
 * Created by quent on 31/01/2017.
 */

public class ImgurThread implements IThread {
    private String _id;
    private String _title;
    private String _desc;
    private String _authorName;
    private long _authorId;

    public ImgurThread(String title, String id, String desc, String authorName, long authorId) {
        _title = title;
        _id = id;
        _desc = desc;
        _authorId = authorId;
        _authorName = authorName;
    }

    @Override
    public List<IImage> getImages() {
        return null;
    }

    @Override
    public IUser getAuthor() {
        return null;
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
