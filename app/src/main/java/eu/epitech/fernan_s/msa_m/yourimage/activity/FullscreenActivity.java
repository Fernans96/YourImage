package eu.epitech.fernan_s.msa_m.yourimage.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.tools.ImagesTools;
import eu.epitech.fernan_s.msa_m.yourimage.tools.TouchImageView;

public class FullscreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_fullscreen);
/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
/*
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
*/

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        setContentView(R.layout.activity_fullscreen);

        ImageView imgDisplay = (TouchImageView) findViewById(R.id.touchImageView);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        LazyHeaders.Builder head = new LazyHeaders.Builder();
        if (url.contains("pixiv")) {
            head = head.addHeader("Referer", "http://www.pixiv.net/");
        }
        GlideUrl gurl = new GlideUrl(url, head.build());
        ImagesTools.LoadPictures(url.substring(url.lastIndexOf(".")), gurl, this, imgDisplay);
    }

}
