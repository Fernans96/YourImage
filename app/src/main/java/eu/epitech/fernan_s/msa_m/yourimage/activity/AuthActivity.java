package eu.epitech.fernan_s.msa_m.yourimage.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.FlickrAPI;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.IApi;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.ImgurAPI;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.PixivAPI;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener{
    private List<IApi> lapi = new ArrayList<>();
    private Context _ctx = this;
    private ColorMatrixColorFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lapi.add(new FlickrAPI(this));
        lapi.add(new ImgurAPI(this));
        lapi.add(new PixivAPI(this));
        setContentView(R.layout.activity_auth);


        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        filter = new ColorMatrixColorFilter(matrix);

        ImageView imageView = (ImageView) findViewById(R.id.flickr_auth);
        imageView.setTag(lapi.get(0).isConnected());
        if (!lapi.get(0).isConnected())
            imageView.setColorFilter(filter);
        imageView.setOnClickListener(this);

        imageView = (ImageView) findViewById(R.id.imgur_auth);
        imageView.setTag(lapi.get(1).isConnected());
        if (!lapi.get(1).isConnected())
            imageView.setColorFilter(filter);
        imageView.setOnClickListener(this);

        imageView = (ImageView) findViewById(R.id.pixiv_auth);
        imageView.setTag(lapi.get(2).isConnected());
        if (!lapi.get(2).isConnected())
            imageView.setColorFilter(filter);
        imageView.setOnClickListener(this);
    }

    public void CheckApi(final ImageView buttonView, boolean isChecked, final int api) {
        if (isChecked) {
            lapi.get(api).connect(this, new IApi.ConnectCallback() {
                @Override
                public void onConnectSuccess() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(_ctx, getString(R.string.authok), Toast.LENGTH_SHORT).show();
                            buttonView.setTag(true);
                            buttonView.setColorFilter(null);
                        }
                    });
                }

                @Override
                public void onConnectFailed() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(_ctx, getString(R.string.authfail), Toast.LENGTH_SHORT).show();
                            buttonView.setTag(false);
                            buttonView.setColorFilter(filter);
                        }
                    });
                }
            });
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.confirDis))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(_ctx, getString(R.string.disconnect), Toast.LENGTH_SHORT).show();
                            lapi.get(api).RemoveToken();
                            buttonView.setTag(false);
                            buttonView.setColorFilter(filter);
                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.flickr_auth:
                CheckApi((ImageView)view, !(boolean)view.getTag(), 0);
                break;

            case R.id.imgur_auth:
                CheckApi((ImageView)view, !(boolean)view.getTag(), 1);
                break;

            case R.id.pixiv_auth:
                CheckApi((ImageView)view, !(boolean)view.getTag(), 2);
                break;
        }
    }

}
