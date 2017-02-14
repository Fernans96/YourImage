package eu.epitech.fernan_s.msa_m.yourimage.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.activity.FullscreenActivity;


public class ScreenSlidePageFragment extends Fragment {

    // Store instance variables
    private int page;
    private String url;
    private Context context;

    // newInstance constructor for creating fragment with arguments
    public static ScreenSlidePageFragment newInstance(int page, String url, int num_page) {
        ScreenSlidePageFragment fragmentFirst = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        args.putString("url", url);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("page", 0);
        url = getArguments().getString("url");
        Log.d("URL", url);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);


        ImageView imageView = (ImageView) view.findViewById(R.id.tmp_image);
        final String extension = url.substring(url.lastIndexOf("."));
        context = getContext();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullscreenActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
        Log.d("URL", url);
        LazyHeaders.Builder head = new LazyHeaders.Builder();
        if (url.contains("pixiv")) {
            head = head.addHeader("Referer", "http://www.pixiv.net/");
        }
        GlideUrl gurl = new GlideUrl(url, head.build());
        if (extension.equals(".gif") || extension.equals(".gifv"))
            Glide
                    .with(this)
                    .load(gurl)
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.interrogation_karai)
                    .into(imageView);
        else {
            Glide
                    .with(this)
                    .load(gurl)
                    .placeholder(R.drawable.interrogation_karai)
                    .into(imageView);
        }
        return view;
    }
}
