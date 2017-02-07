package eu.epitech.fernan_s.msa_m.yourimage.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

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
import eu.epitech.fernan_s.msa_m.yourimage.model.thread.IThread;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MaterialSearchView searchView;
    private RecyclerView recyclerView;
    private EndlessRecyclerViewScrollListener scrollListener;
    private List<IThread> treads;
    private CardAdapter cardAdapter;
    private List<IApi> _lapi = null;
    private Context _ctx = this;
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

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        treads = new ArrayList<>();
        cardAdapter = new CardAdapter(treads);
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

    public void init_search() {
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

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
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View hView = navigationView.getHeaderView(0);

        SwitchCompat lol = (SwitchCompat) hView.findViewById(R.id.ImgurSwitch);
        lol.setOnCheckedChangeListener(list);
        lol = (SwitchCompat) hView.findViewById(R.id.FlickrSwitch);
        lol.setOnCheckedChangeListener(list);
        MultiImageView multiImageView = (MultiImageView) hView.findViewById(R.id.iv);
        multiImageView.setShape(MultiImageView.Shape.CIRCLE);
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void RemoveApi(String api) {
        for (int i = 0; i < _lapi.size(); i++) {
            if (_lapi.get(i).getName().equals(api)) {
                _lapi.remove(i);
            }
        }

    }

    public void UpdateAdapter() {
        treads.clear();
        cardAdapter.notifyDataSetChanged();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        MultiImageView multiImageView = (MultiImageView) hView.findViewById(R.id.iv);
        multiImageView.clear();
        for (IApi api : _lapi) {
            if (api.isConnected()) {
                multiImageView.addImage(api.getIcon());
                api.getThread(1, new IThread.GetThreadCallback() {
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
                api.connect(this);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //UpdateAdapter();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void loadNextDataFromApi(int page) {
        for (IApi api : _lapi) {
            api.getThread(page + 1, new IThread.GetThreadCallback() {
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
