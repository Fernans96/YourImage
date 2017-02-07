package eu.epitech.fernan_s.msa_m.yourimage.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.FlickrAPI;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.IApi;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.ImgurAPI;

public class AuthActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private List<IApi> lapi = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lapi.add(new FlickrAPI(this));
        lapi.add(new ImgurAPI(this));
        setContentView(R.layout.activity_auth);
        CheckBox check = (CheckBox) findViewById(R.id.CheckFlickr);
        check.setChecked(lapi.get(0).isConnected());
        check.setOnCheckedChangeListener(this);
        check = (CheckBox) findViewById(R.id.CheckImgur);
        check.setChecked(lapi.get(1).isConnected());
        check.setOnCheckedChangeListener(this);
    }

    public void CheckApi(final CompoundButton buttonView, boolean isChecked, int api) {
        if (isChecked) {
            lapi.get(api).connect(this, new IApi.ConnectCallback() {
                @Override
                public void onConnectSuccess() {

                }

                @Override
                public void onConnectFailed() {
                    buttonView.setChecked(false);
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
        }
    }
}
