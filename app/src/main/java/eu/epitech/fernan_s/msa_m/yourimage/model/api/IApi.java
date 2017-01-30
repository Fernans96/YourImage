package eu.epitech.fernan_s.msa_m.yourimage.model.api;

import android.content.Context;

/**
 * Created by quent on 30/01/2017.
 */

public interface IApi {
    public String getAuthlink();
    public void connect(Context ctx);
    public void auth(String query);
    public String getThread(int page);
    public String getThread(String tags, int page);
    public void SendPic(String pic);
    public void Fav(int id);
    public void unFav(int id);
    public String getFavs(int page);
}
