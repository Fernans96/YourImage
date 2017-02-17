package eu.epitech.fernan_s.msa_m.yourimage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.widget.ImageView;

import com.orm.SugarContext;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.internal.MethodSorter;
import org.junit.runner.RunWith;

import java.net.URI;
import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.model.api.IApi;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.PixivAPI;
import eu.epitech.fernan_s.msa_m.yourimage.model.image.IImage;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.PixivThread;
import eu.epitech.fernan_s.msa_m.yourimage.tools.ImagesTools;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private String TAG = "TEST";


    @Before
    public void ConnectToApiPixiv() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        SugarContext.init(appContext);
        PixivAPI api = new PixivAPI(appContext);
        api.RemoveToken();
        if (!api.isConnected()) {
            api.SyncAuth("quentin.fernandez9@me.com", "test1234");
        }
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("eu.epitech.fernan_s.msa_m.yourimage", appContext.getPackageName());
    }

    @Test
    public void ConnectionTestPixiv() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        IApi api = new PixivAPI(appContext);
        if (!api.isConnected())
            throw new Exception("Token doesn't exist");
    }

    @Test
    public void getThreadTestPixiv() throws Exception {
        List<PixivThread> lpix = new PixivAPI(InstrumentationRegistry.getTargetContext()).Sync_GetThread(0);
        if (lpix.size() < 1)
            throw new Exception("Thread list empty");
    }

    @Test
    public void getPictureTestPixiv() throws Exception {
        List<PixivThread> lpix = new PixivAPI(InstrumentationRegistry.getTargetContext()).Sync_GetThread(0);
        if (lpix.size() < 1)
            throw new Exception("Thread list empty");
        List<IImage> lim = lpix.get(0).sync_getImages();
        if (lim.size() < 1)
            throw new Exception("Image list empty");
    }

    @Test
    public void ThreadFav() throws Exception {
        List<PixivThread> lpix = new PixivAPI(InstrumentationRegistry.getTargetContext()).Sync_GetThread(0);
        if (lpix.size() < 1)
            throw new Exception("Thread list empty");
        for (PixivThread pix : lpix) {
            if (!pix.isFav())
                pix.fav();
        }
    }

    @After
    public void afterfonction() throws Exception {
        SugarContext.terminate();
    }
}
