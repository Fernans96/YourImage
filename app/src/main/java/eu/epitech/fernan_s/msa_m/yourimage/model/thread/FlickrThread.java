package eu.epitech.fernan_s.msa_m.yourimage.model.thread;

import eu.epitech.fernan_s.msa_m.yourimage.model.image.IImage;
import eu.epitech.fernan_s.msa_m.yourimage.model.user.IUser;

/**
 * Created by quent on 05/02/2017.
 */

public class FlickrThread implements IThread {
    String _owner_id;
    String _id;
    String _title;
    String _secret;
    String _server;

    public FlickrThread(String owner_id, String id, String title, String secret, String server) {
        _title = title;
        _id = id;
        _owner_id = owner_id;
        _secret = secret;
        _server = server;
    }

    @Override
    public void getImages(IImage.getImageCallback callback) {

    }

    @Override
    public void getAuthor(IUser.GetUserCallback callback) {

    }

    @Override
    public String getTitle() {
        return _title;
    }

    @Override
    public String getDesc() {
        return "FlickrPictures";
    }
}
