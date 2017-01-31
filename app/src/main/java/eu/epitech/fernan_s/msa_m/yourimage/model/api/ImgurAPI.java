package eu.epitech.fernan_s.msa_m.yourimage.model.api;

import android.content.Context;
import android.support.test.espresso.core.deps.guava.base.Splitter;
import android.util.Log;

import java.util.Map;

import eu.epitech.fernan_s.msa_m.yourimage.dialog.AuthDialog;

/**
 * Created by quent on 30/01/2017.
 */

public class ImgurAPI implements IApi {
    private static String _client_id = "47bb2cf28f17756";
    private static String _secret = "aef3b44d9985f67134e0ab82bbaddb2e7d95f7f8";


    @Override
    public String getAuthlink() {
        String ret = "https://api.imgur.com/oauth2/authorize?"
                + "client_id=" + _client_id
                + "&response_type=" + "token"
                + "&state=" + "";
        return ret;
    }

    @Override
    public void connect(Context ctx) {
        AuthDialog diag = new AuthDialog(ctx, this);
        diag.show();
    }

    @Override
    public void auth(String query) {
        String[] ret = query.split("#");
        Map<String, String> map = Splitter.on('&').trimResults().withKeyValueSeparator("=").split(ret[ret.length - 1]);

        Log.d("Access", map.get("access_token"));
    }

    @Override
    public String getThread(int page) {
        return null;
    }

    @Override
    public String getThread(String tags, int page) {
        return null;
    }

    @Override
    public void SendPic(String pic) {

    }

    @Override
    public void Fav(int id) {

    }

    @Override
    public void unFav(int id) {

    }

    @Override
    public String getFavs(int page) {
        return null;
    }
}
