package eu.epitech.fernan_s.msa_m.yourimage.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import eu.epitech.fernan_s.msa_m.yourimage.R;


public class IntroFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String mTitle;
    private String mDesc;
    private int mImageId;

    public IntroFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static IntroFragment newInstance(String title, String desc, int image_id) {
        IntroFragment fragment = new IntroFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, title);
        args.putString(ARG_PARAM2, desc);
        args.putInt(ARG_PARAM3, image_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_PARAM1);
            mDesc = getArguments().getString(ARG_PARAM2);
            mImageId = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_intro, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.tuto_image);
        Glide
                .with(getContext())
                .load(mImageId)
                .into(imageView);

        TextView textView = (TextView) view.findViewById(R.id.tuto_title);
        textView.setText(mTitle);
        textView = (TextView) view.findViewById(R.id.tuto_desc);
        textView.setText(mDesc);

        return view;
    }

}
