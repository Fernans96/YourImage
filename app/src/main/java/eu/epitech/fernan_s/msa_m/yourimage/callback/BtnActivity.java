package eu.epitech.fernan_s.msa_m.yourimage.callback;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import eu.epitech.fernan_s.msa_m.yourimage.activity.PostActivity;

/**
 * Created by quent on 16/02/2017.
 */

public class BtnActivity implements View.OnClickListener {
    Class _c;
    Context _ctx;

    public BtnActivity(Class c, Context ctx) {
        _c = c;
        _ctx = ctx;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(_ctx, _c);
        _ctx.startActivity(intent);
    }
}
