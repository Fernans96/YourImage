package eu.epitech.fernan_s.msa_m.yourimage.model.user;

/**
 * Created by quent on 31/01/2017.
 */

public interface IUser {
    public String getPictureLink();
    public String getUsername();

    public interface GetUserCallback {
        public void OnGetUserFinished(IUser user);
    }
}
