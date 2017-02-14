package eu.epitech.fernan_s.msa_m.yourimage.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.fragment.ScreenSlidePageFragment;
import eu.epitech.fernan_s.msa_m.yourimage.model.image.IImage;

/**
 * Created by matheo msa on 06/02/2017.
 */

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    int NUM_PAGES = 1;
    List<Fragment> lfrag = null;


    public ScreenSlidePagerAdapter(FragmentManager fm, List<IImage> lThread) {
        super(fm);
        NUM_PAGES = lThread.size();
        lfrag = new ArrayList<>();
        for (int i = 0; i < NUM_PAGES; i++) {
            lfrag.add(ScreenSlidePageFragment.newInstance(i, lThread.get(i).getLink().toStringUrl(), NUM_PAGES));
        }
    }


    @Override
    public Fragment getItem(int position) {
        return lfrag.get(position);
    }


    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
