package eu.epitech.fernan_s.msa_m.yourimage.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.ScreenSlidePageFragment;
import eu.epitech.fernan_s.msa_m.yourimage.model.image.IImage;

/**
 * Created by matheo msa on 06/02/2017.
 */

public class ScreenSlidePagerAdapter  extends FragmentStatePagerAdapter {
    int NUM_PAGES = 1;
     List<IImage> _lThread = null;


    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setLThread(List<IImage> lThread){
        _lThread = lThread;
        NUM_PAGES = lThread.size();
    }


    @Override
    public Fragment getItem(int position) {
        Log.d("FRAG", "lol: " + _lThread.get(position).getTitle());
        if (_lThread != null)
            return ScreenSlidePageFragment.newInstance(position, _lThread.get(position).getTitle(), _lThread.get(position).getLink(), _lThread.get(position).getDesc());
        else
            return new ScreenSlidePageFragment();
    }



    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
