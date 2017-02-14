package eu.epitech.fernan_s.msa_m.yourimage.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import eu.epitech.fernan_s.msa_m.yourimage.R;

public class FavActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

    }
}
