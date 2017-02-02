package eu.epitech.fernan_s.msa_m.yourimage.model.api;

import android.content.Context;

import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.IToken;

/**
 * Created by quent on 30/01/2017.
 */

public interface IApi {
    public void getAuthlink(AuthLinkCallback callback);
    public void connect(Context ctx);
    public void auth(String query);
    public boolean isConnected();
    public IToken getToken();
    public void getThread(int page, IThread.GetThreadCallback callback);
    public void getThread(String tags, int page, IThread.GetThreadCallback callback);
    public void SendPic(String pic);
    public void Fav(int id);
    public void unFav(int id);
    public void getFavs(int page, IThread.GetThreadCallback callback);
    public interface AuthLinkCallback {
        public void onAuthLinkFinished(String authlink);
    }
}
