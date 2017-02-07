package eu.epitech.fernan_s.msa_m.yourimage;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


public class ScreenSlidePageFragment extends Fragment {

/*    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);

        return rootView;
    }*/
// Store instance variables
    private String title;
    private int page;
    private String url;

    // newInstance constructor for creating fragment with arguments
    public static ScreenSlidePageFragment newInstance(int page, String title, String url) {
        ScreenSlidePageFragment fragmentFirst = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        args.putString("title", title);
        args.putString("url", url);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("page", 0);
        title = getArguments().getString("Title", "no title");
        url = getArguments().getString("url");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
//        TextView tvLabel = (TextView) view.findViewById(R.id.tvLabel);

        ImageView imageView =  (ImageView) view.findViewById(R.id.tmp_image);
        final String extension = url.substring(url.lastIndexOf("."));
        if (extension.equals(".gif"))
            Glide
                    .with(this)
                    .load(url)
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .skipMemoryCache(true)
                    .placeholder(R.drawable.interrogation_karai)
                    .into(imageView);
        else {
            Glide
                    .with(this)
                    .load(url)
                    .placeholder(R.drawable.interrogation_karai)
                    .into(imageView);
        }
       return view;
    }
}
