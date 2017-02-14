package eu.epitech.fernan_s.msa_m.yourimage.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.FlickrAPI;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.IApi;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.ImgurAPI;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.PixivAPI;

public class AuthActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private List<IApi> lapi = new ArrayList<>();
    Context _ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lapi.add(new FlickrAPI(this));
        lapi.add(new ImgurAPI(this));
        lapi.add(new PixivAPI(this));
        setContentView(R.layout.activity_auth);
        CheckBox check = (CheckBox) findViewById(R.id.CheckFlickr);
        check.setChecked(lapi.get(0).isConnected());
        check.setOnCheckedChangeListener(this);
        check = (CheckBox) findViewById(R.id.CheckImgur);
        check.setChecked(lapi.get(1).isConnected());
        check.setOnCheckedChangeListener(this);
        check = (CheckBox) findViewById(R.id.CheckPixiv);
        check.setChecked(lapi.get(2).isConnected());
        check.setOnCheckedChangeListener(this);
    }

    public void CheckApi(final CompoundButton buttonView, boolean isChecked, int api) {
        if (isChecked) {
            lapi.get(api).connect(this, new IApi.ConnectCallback() {
                @Override
                public void onConnectSuccess() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(_ctx, "Authentication Success", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onConnectFailed() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(_ctx, "Authentication Failed", Toast.LENGTH_LONG).show();
                            buttonView.setChecked(false);
                        }
                    });
                }
            });
        } else {
            lapi.get(api).RemoveToken();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.CheckFlickr:
                CheckApi(buttonView, isChecked,0);
                break;
            case R.id.CheckImgur:
                CheckApi(buttonView, isChecked,1);
                break;
            case R.id.CheckPixiv:
                CheckApi(buttonView, isChecked,2);
                break;
        }
    }
}
