package eu.epitech.fernan_s.msa_m.yourimage.activity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.orm.SugarContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.adapter.ScreenSlidePagerAdapter;
import eu.epitech.fernan_s.msa_m.yourimage.model.image.IImage;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.FlickrThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.ImgurThread;

public class ImageActivity extends AppCompatActivity {
    private ViewPager mPager;
    private TextView page_tv;
    private Context _ctx;
    private int nb_page;
    private boolean isFav;
    private IThread thread;
    private List<IImage> lImage;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4242;


    @Override
    protected void onStop() {
        super.onStop();
        SugarContext.terminate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        SugarContext.init(this);
        String Jthread = getIntent().getStringExtra("thread");
        thread = null;
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

        isFav = thread.isFav();
        Log.d("WUT", "apres que le thread soit instance");

//        final ImageView imageView = (ImageView) findViewById(R.id.tmp_image);
        _ctx = this;
        TextView textView = (TextView) findViewById(R.id.title_tread_image);
        page_tv = (TextView) findViewById(R.id.page_text);
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
                nb_page = lThread.size();
                lImage = lThread;
                mPagerAdapter.setLThread(lThread);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (nb_page > 1){
                            String pages = "1/" + nb_page;
                            page_tv.setText(pages);
                        }
                        mPager.setAdapter(mPagerAdapter);
                    }
                });

            }
        });

        // Attach the page change listener inside the activity
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
            if (nb_page >= 1){
                String pages = (position + 1) + "/" + nb_page;
                page_tv.setText(pages);
            }
/*
                Toast.makeText(ImageActivity.this,
                        "Selected page position: " + position, Toast.LENGTH_SHORT).show();
*/
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

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.getItem(1);

        Log.d("WUT", "quand j'ai besoin que le thread soit instance");
        if (isFav){
            menuItem.setChecked(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                menuItem.setIcon(getDrawable(R.drawable.ic_favorite_white_24dp));
            }
            else{
                menuItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
            }
        }
        else{
            menuItem.setChecked(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                menuItem.setIcon(getDrawable(R.drawable.ic_favorite_border_white_24dp));
            }
            else{
                menuItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
            }
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.action_fav:
                if(menuItem.isChecked()){
                    menuItem.setChecked(false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        menuItem.setIcon(getDrawable(R.drawable.ic_favorite_border_white_24dp));
                    }
                    else{
                        menuItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
                    }
                   thread.unfav();
                }
                else {
                    menuItem.setChecked(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        menuItem.setIcon(getDrawable(R.drawable.ic_favorite_white_24dp));
                    }
                    else{
                        menuItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
                    }
                    thread.fav();
                }
                break;
            case R.id.action_save:
                String link = lImage.get(mPager.getCurrentItem()).getLink();
                String [] picid = link.split("/");
                String name = picid[picid.length -1];
                Log.d("SAVE", "name: " + name);
                file_download(link, name);
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }
    public void file_download(String uRl, String name) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            File direct = new File(Environment.getExternalStorageDirectory()
                    + "/YourImage");
            if (!direct.exists()) {
                direct.mkdirs();
            }

            DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(uRl);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false).setTitle("Demo")
                    .setDescription("Something useful. No, really.")
                    .setDestinationInExternalPublicDir("/YourImage", name);

            mgr.enqueue(request);
        }
    }

}
