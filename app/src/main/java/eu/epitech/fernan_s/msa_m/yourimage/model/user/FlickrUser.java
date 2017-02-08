package eu.epitech.fernan_s.msa_m.yourimage.model.user;

/**
 * Created by quent on 08/02/2017.
 */

public class FlickrUser implements IUser {
    public String _link;
    public String _UserName;

    public FlickrUser(String link, String User) {
        _UserName = User;
        _link = link;
    }

    @Override
    public String getPictureLink() {
        return _link;
    }

    @Override
    public String getUsername() {
        return _UserName;
    }
}
