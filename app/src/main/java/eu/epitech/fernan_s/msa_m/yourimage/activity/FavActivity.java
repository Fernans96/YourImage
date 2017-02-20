package eu.epitech.fernan_s.msa_m.yourimage.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import shortbread.Shortcut;

@Shortcut(id = "fav", icon = R.drawable.ic_favorite_black_24dp, shortLabel = "Favorite")
public class FavActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private RecyclerView recyclerView;
    private List<IApi> _lapi = new ArrayList<>();
    private List<IThread> _lthread;
    private CardAdapter _adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        SugarContext.init(this);
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
        initSwipe();
        LoadFav();
    }

    private void initSwipe(){
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mSwipeRefreshLayout.setColorSchemeColors(getColor(R.color.colorPrimaryDark),getColor(R.color.colorPrimary),getColor(R.color.colorAccent));
        }
        else {
            mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark),getResources().getColor(R.color.colorPrimary),getResources().getColor(R.color.colorAccent));
        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                LoadFav();
            }
        });
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
        _adapter.ClearCache();
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
                            if (mSwipeRefreshLayout.isRefreshing())
                                mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            });
        }
    }
}
