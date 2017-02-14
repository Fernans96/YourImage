package eu.epitech.fernan_s.msa_m.yourimage.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.stfalcon.multiimageview.MultiImageView;

import java.util.ArrayList;
import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.EndlessRecyclerViewScrollListener;
import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.adapter.CardAdapter;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.FlickrAPI;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.IApi;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.ImgurAPI;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.PixivAPI;
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private MaterialSearchView searchView;
    private RecyclerView recyclerView;
    private EndlessRecyclerViewScrollListener scrollListener;
    private List<IThread> treads;

    private SwitchCompat switchCompatFlickr, switchCompatImgur, switchCompatPixiv;
    private LinearLayout favbutton;
    private NavigationView navigationView;
    private MultiImageView multiImageView;
    private View hView;
    private Gson gson;

    private CardAdapter cardAdapter;
    private List<IApi> _lapi = null;
    private Context _ctx = this;
    private String _query = "";

    CompoundButton.OnCheckedChangeListener list = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            switch (compoundButton.getId()) {
                case R.id.ImgurSwitch:
                    if (b)
                        _lapi.add(new ImgurAPI(_ctx));
                    else
                        RemoveApi("Imgur");
                    break;
                case R.id.FlickrSwitch:
                    if (b)
                        _lapi.add(new FlickrAPI(_ctx));
                    else
                        RemoveApi("Flickr");
                    break;
                case R.id.PixivSwitch:
                    if (b)
                        _lapi.add(new PixivAPI(_ctx));
                    else
                        RemoveApi("Pixiv");
                    break;
                default:
                    break;
            }
            UpdateAdapter();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = preferences.edit();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        treads = new ArrayList<>();
        gson = new Gson();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        hView = navigationView.getHeaderView(0);
        switchCompatImgur = (SwitchCompat) hView.findViewById(R.id.ImgurSwitch);
        switchCompatFlickr = (SwitchCompat) hView.findViewById(R.id.FlickrSwitch);
        switchCompatPixiv = (SwitchCompat) hView.findViewById(R.id.PixivSwitch);
        multiImageView = (MultiImageView) hView.findViewById(R.id.iv);
        favbutton = (LinearLayout) hView.findViewById(R.id.fav_button);

        cardAdapter = new CardAdapter(treads);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(_ctx, PostActivity.class);
                startActivity(intent);
            }
        });

        favbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(_ctx, FavActivity.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cardAdapter);
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);
        setSupportActionBar(toolbar);
        init_search();
        init_api();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RefreshApi();
    }

    public void init_search() {
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("Tags", "onQueryTextSubmit: " + newText);
                _query = newText;
                UpdateAdapter();
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }
        });

    }

    public void init_api() {
        _lapi = new ArrayList<>();
        ArrayList<String> names = gson.fromJson(preferences.getString("apis", ""), ArrayList.class);
        if (names == null)
            names = new ArrayList<>();

        switchCompatImgur.setOnCheckedChangeListener(list);
        switchCompatImgur.setEnabled(new ImgurAPI(this).isConnected());
        switchCompatFlickr.setEnabled(new FlickrAPI(this).isConnected());
        switchCompatFlickr.setOnCheckedChangeListener(list);
        switchCompatPixiv.setEnabled(new PixivAPI(this).isConnected());
        switchCompatPixiv.setOnCheckedChangeListener(list);

        for (String s : names){
            if (s.equals("Imgur")){
                switchCompatImgur.setChecked(true);
            }
            else if (s.equals("Flickr")){
                switchCompatFlickr.setChecked(true);
            }
            else if (s.equals("Pixiv")){
                switchCompatPixiv.setChecked(true);
            }
        }
        multiImageView.setShape(MultiImageView.Shape.CIRCLE);
    }

    public void RefreshApi() {
        boolean connected = new ImgurAPI(this).isConnected();
        switchCompatImgur.setEnabled(connected);
        switchCompatImgur.setChecked(switchCompatImgur.isChecked() && connected);
        connected = new FlickrAPI(this).isConnected();
        switchCompatFlickr.setEnabled(connected);
        switchCompatFlickr.setChecked(switchCompatFlickr.isChecked() && connected);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_reload:
                UpdateAdapter();
                break;
            case R.id.action_authenticate:
                Intent intent = new Intent(_ctx, AuthActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.action_gotop:
                recyclerView.scrollToPosition(0);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void setApis(){
        ArrayList<String> names = gson.fromJson(preferences.getString("apis", ""), ArrayList.class);
        boolean add;
        if (names == null)
            names = new ArrayList<>();
        for (int i = 0; i <_lapi.size(); i++){
            add = true;
            for (String s : names){
                if (s.equals(_lapi.get(i).getName()))
                    add = false;
            }
            if (add)
                names.add(_lapi.get(i).getName());
        }

        String s = gson.toJson(names);
        editor.putString("apis", s);
        editor.apply();
    }


    public void RemoveApi(String api) {
        ArrayList<String> names = gson.fromJson(preferences.getString("apis", ""), ArrayList.class);
        if (names == null)
             names = new ArrayList<>();

        for (int i = 0; i < _lapi.size(); i++) {
            if (_lapi.get(i).getName().equals(api)) {
                if (names.size()>=i)
                    names.remove(i);
                _lapi.remove(i);
            }
        }
        String s = gson.toJson(names);
        editor.putString("apis", s);
        editor.apply();

    }

    public void UpdateAdapter() {
        treads.clear();

        cardAdapter.notifyDataSetChanged();
        multiImageView.clear();
        final String current_str = _query;
        for (IApi api : _lapi) {
            multiImageView.addImage(api.getIcon());
            if (_query.isEmpty()) {
                api.getThread(0, new IThread.GetThreadCallback() {
                    @Override
                    public void onGetThreadComplete(final List<IThread> lThread) {
                        setApis();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!current_str.equals(_query))
                                    return;
                                int old = treads.size();
                                treads.addAll(lThread);
                                cardAdapter.notifyItemRangeInserted(old, lThread.size());
                            }
                        });
                    }
                });
            } else {
                api.getThread(_query, 0, new IThread.GetThreadCallback() {
                    @Override
                    public void onGetThreadComplete(final List<IThread> lThread) {
                        setApis();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!current_str.equals(_query))
                                    return;
                                int old = treads.size();
                                treads.addAll(lThread);
                                cardAdapter.notifyItemRangeInserted(old, lThread.size());
                            }
                        });
                    }
                });
            }
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadNextDataFromApi(int page) {
        for (IApi api : _lapi) {
            if (_query.isEmpty()) {
                api.getThread(page, new IThread.GetThreadCallback() {
                    @Override
                    public void onGetThreadComplete(final List<IThread> lThread) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int old = treads.size();
                                treads.addAll(lThread);
                                cardAdapter.notifyItemRangeInserted(old, lThread.size());
                            }
                        });
                    }
                });
            } else {
                api.getThread(_query, page, new IThread.GetThreadCallback() {
                    @Override
                    public void onGetThreadComplete(final List<IThread> lThread) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int old = treads.size();
                                treads.addAll(lThread);
                                cardAdapter.notifyItemRangeInserted(old, lThread.size());
                            }
                        });
                    }
                });
            }
        }

    }
}
