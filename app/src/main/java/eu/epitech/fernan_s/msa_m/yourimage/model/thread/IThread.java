package eu.epitech.fernan_s.msa_m.yourimage.model.thread;

import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.model.image.IImage;
import eu.epitech.fernan_s.msa_m.yourimage.model.user.IUser;

/**
 * Created by quent on 30/01/2017.
 */

public interface IThread {
    public void getImages(IImage.getImageCallback callback);
    public void getAuthor(IUser.GetUserCallback callback);
    public String getTitle();
    public String getDesc();
    public String getType();
    public String getID();
    public void fav();
    public void unfav();
    public boolean isFav();
    public String ShareLink();

    public interface GetThreadCallback {
        public void onGetThreadComplete(List<IThread> lThread);
    }
}
