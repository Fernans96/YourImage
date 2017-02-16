package eu.epitech.fernan_s.msa_m.yourimage.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.gson.Gson;
import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.adapter.CardAdapter;
import eu.epitech.fernan_s.msa_m.yourimage.listener.HidingScrollListener;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.FlickrAPI;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.IApi;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.ImgurAPI;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.PixivAPI;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;

public class FavActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private RecyclerView recyclerView;
    private List<IApi> _lapi = new ArrayList<>();
    private List<IThread> _lthread;
    private CardAdapter _adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        SugarContext.init(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_fav);
        _lthread = new ArrayList<>();
        _adapter = new CardAdapter(_lthread);
        recyclerView.setAdapter(_adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // THIS IS IMPORTANT
        recyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {

            }
            @Override
            public void onShow() {

            }
        });
        initFav();
        LoadFav();
    }

    private void initFav() {
        Gson gson = new Gson();
        ArrayList<String> names = gson.fromJson(preferences.getString("apis", ""), ArrayList.class);
        for (String api : names) {
            if (api.equals("Flickr")) {
                _lapi.add(new FlickrAPI(this));
            } else if (api.equals("Pixiv")) {
                _lapi.add(new PixivAPI(this));
            } else if (api.equals("Imgur")) {
                _lapi.add(new ImgurAPI(this));
            }
        }
    }

    private void LoadFav() {
        _lthread.clear();
        _adapter.notifyDataSetChanged();
        for (IApi api : _lapi) {
            api.getFavs(0, new IThread.GetThreadCallback() {
                @Override
                public void onGetThreadComplete(final List<IThread> lThread) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int old = _lthread.size();
                            _lthread.addAll(lThread);
                            _adapter.notifyItemRangeInserted(old, lThread.size());
                        }
                    });
                }
            });
        }
    }
}
