package eu.epitech.fernan_s.msa_m.yourimage.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.fragment.IntroFragment;

public class IntroActivity extends AppIntro2 {
    SharedPreferences preference;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        preference = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = preference.edit();
        addSlide(IntroFragment.newInstance(getString(R.string.tuto_title_1), getString(R.string.tuto_desc_1), R.drawable.tutorial_1));
        addSlide(IntroFragment.newInstance(getString(R.string.tuto_title_2), getString(R.string.tuto_desc_2), R.drawable.tutorial_2));
        addSlide(IntroFragment.newInstance(getString(R.string.tuto_title_3), getString(R.string.tuto_desc_3), R.drawable.tutorial_3));
        addSlide(IntroFragment.newInstance(getString(R.string.tuto_title_4), getString(R.string.tuto_desc_4), R.drawable.tutorial_5));

    }

    private void launchActivity(){
        editor.putBoolean("firstLaunch", false);
        editor.apply();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("startauth", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        launchActivity();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        launchActivity();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
