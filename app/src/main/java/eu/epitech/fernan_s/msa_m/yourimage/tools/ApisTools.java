package eu.epitech.fernan_s.msa_m.yourimage.tools;

import android.content.Context;

import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.DeviantArtApi;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.FlickrAPI;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.IApi;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.ImgurAPI;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.PX500API;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.PixivAPI;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;

/**
 * Created by quent on 16/02/2017.
 */

public class ApisTools {
    public static void MultipleGetThread(List<IApi> lapi, int page, String Query, IThread.GetThreadCallback callback) {
        for (IApi api : lapi) {
            if (Query.isEmpty()) {
                api.getThread(page, callback);
            } else {
                api.getThread(Query, page, callback);
            }
        }
    }

    public static IApi CreateInstance(int id, Context ctx) {
        switch (id) {
            case R.id.FlickrSwitch:
                return new FlickrAPI(ctx);
            case R.id.ImgurSwitch:
                return new ImgurAPI(ctx);
            case R.id.PixivSwitch:
                return new PixivAPI(ctx);
            case R.id.DeviantSwitch:
                return new DeviantArtApi(ctx);
            case R.id.px500Switch:
                return new PX500API(ctx);
        }
        return null;
    }

    public static boolean ApiLoad(List<IApi> lapi, String type) {
        for (IApi api : lapi) {
            if (api.getName().equals(type)) {
                return true;
            }
        }
        return false;
    }
}
