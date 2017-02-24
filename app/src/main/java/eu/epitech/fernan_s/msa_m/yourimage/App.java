package eu.epitech.fernan_s.msa_m.yourimage;

import android.app.Application;

import com.orm.SugarContext;

import shortbread.Shortbread;

/**
 * Created by quent on 18/02/2017.
 */

public class App extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        Shortbread.create(this);
        SugarContext.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}