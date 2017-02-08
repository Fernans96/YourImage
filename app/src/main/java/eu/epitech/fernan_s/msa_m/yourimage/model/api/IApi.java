package eu.epitech.fernan_s.msa_m.yourimage.model.api;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.token.IToken;

/**
 * Created by quent on 30/01/2017.
 */

public interface IApi {
    public void getAuthlink(AuthLinkCallback callback);
    public void connect(Context ctx, ConnectCallback callback);
    public void auth(String query, ConnectCallback callback);
    public boolean isConnected();
    public IToken getToken();
    public void RemoveToken();
    public void getThread(int page, IThread.GetThreadCallback callback);
    public void getThread(String tags, int page, IThread.GetThreadCallback callback);
    public void SendPic(String Title, String Desc, List<Bitmap> images);
    public Bitmap getIcon();
    public void getFavs(int page, IThread.GetThreadCallback callback);
    public String getName();
    public interface AuthLinkCallback {
        public void onAuthLinkFinished(String authlink);
    }
    public interface ConnectCallback {
        public void onConnectSuccess();
        public void onConnectFailed();
    }
}
