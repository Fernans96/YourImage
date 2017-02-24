package eu.epitech.fernan_s.msa_m.yourimage.model.user;

/**
 * Created by Zacka on 2/20/2017.
 */

public class PX500User implements IUser {
    String _PictureLink;
    String _Username;

    public PX500User(String PictureLink, String Username) {
        _PictureLink = PictureLink;
        _Username = Username;
    }

    @Override
    public String getPictureLink() {
        return _PictureLink;
    }

    @Override
    public String getUsername() {
        return _Username;
    }
}
