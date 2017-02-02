package eu.epitech.fernan_s.msa_m.yourimage.model.user;

/**
 * Created by quent on 31/01/2017.
 */

public class ImgurUser implements IUser {
    String _PictureLink;
    String _Username;

    public ImgurUser(String PictureLink, String Username) {
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
