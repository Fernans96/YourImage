package eu.epitech.fernan_s.msa_m.yourimage.activity;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ResourceBundle;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.adapter.ScreenSlidePagerAdapter;
import eu.epitech.fernan_s.msa_m.yourimage.model.image.IImage;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.FlickrThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.ImgurThread;

public class ImageActivity extends AppCompatActivity {
    private ViewPager mPager;
    private Context _ctx;
    private ScreenSlidePagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        String Jthread = getIntent().getStringExtra("thread");
        IThread thread = null;
        try {
            String type = new JSONObject(Jthread).getString("_Type");
            if (type.equals("Flickr")) {
                thread = new Gson().fromJson(Jthread, FlickrThread.class);
            } else if (type.equals("Imgur")) {
                thread = new Gson().fromJson(Jthread, ImgurThread.class);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        final ImageView imageView = (ImageView) findViewById(R.id.tmp_image);
        _ctx = this;
        TextView textView = (TextView) findViewById(R.id.title_tread_image);
        if(thread.getTitle() != null)
            textView.setText(thread.getTitle());

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.viewPager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

        final ImageView imageView = (ImageView) mPager.findViewById(R.id.tmp_image);
        //mPagerAdapter.setLThread(thread);
        thread.getImages(new IImage.getImageCallback() {
            @Override
            public void onGetImageFinished(List<IImage> lThread) {
//                String uri = lThread.get(0).getLink();
                Log.d("FRAG", "title of 0: " + lThread.get(0).getTitle());
                mPagerAdapter.setLThread(lThread);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPager.setAdapter(mPagerAdapter);
                    }
                });

            }
        });

        // Attach the page change listener inside the activity
/*
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                Toast.makeText(ImageActivity.this,
                        "Selected page position: " + position, Toast.LENGTH_SHORT).show();
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });
*/

/*        thread.getImages(new IImage.getImageCallback() {
            @Override
            public void onGetImageFinished(final List<IImage> lThread) {
                String uri = lThread.get(0).getLink();
                final String extension = uri.substring(uri.lastIndexOf("."));
                Log.d("Object", "url: " + lThread.get(0).getLink() + " | ext: " + extension);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (extension.equals(".gif"))
                        Glide
                                .with(_ctx)
                                .load(lThread.get(0).getLink())
                                .asGif()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .skipMemoryCache(true)
                                .placeholder(R.drawable.interrogation_karai)
                                .into(imageView);
                        else {
                            Glide
                                    .with(_ctx)
                                    .load(lThread.get(0).getLink())
                                    .placeholder(R.drawable.interrogation_karai)
                                    .into(imageView);
                        }
                    }
                });
            }
        });*/
    }
}
