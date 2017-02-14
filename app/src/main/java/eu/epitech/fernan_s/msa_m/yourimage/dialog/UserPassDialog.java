package eu.epitech.fernan_s.msa_m.yourimage.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.IApi;

/**
 * Created by quent on 13/02/2017.
 */

public class UserPassDialog extends AlertDialog.Builder  {
    private View _Dialog_Layout;
    private IApi _api;
    private EditText _UserName;
    private EditText _Password;
    private AlertDialog _Dialog;
    private IApi.ConnectCallback _callback;
    private boolean done = false;

    public UserPassDialog(Context context, IApi api, IApi.ConnectCallback callback) {
        super(context);
        _api = api;
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        _Dialog_Layout = layoutInflaterAndroid.inflate(R.layout.userpassdialog, null);
        _UserName = (EditText) _Dialog_Layout.findViewById(R.id.UserName);
        _Password = (EditText) _Dialog_Layout.findViewById(R.id.Password);
        setView(_Dialog_Layout);
        InitDialog();
        _Dialog = create();
        _callback = callback;
    }

    private void InitDialog() {
        this.setCancelable(true);
        this.setPositiveButton(getContext().getResources().getString(R.string.Auth), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        this.setNegativeButton(getContext().getResources().getString(R.string.cancelbtn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public void Show() {
        _Dialog.show();
        InitAuthBtn();
        _Dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!done) {
                    _callback.onConnectFailed();
                }
            }
        });
    }

    private void InitAuthBtn() {
        _Dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("username", _UserName.getText().toString());
                    obj.put("password", _Password.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                _api.auth(obj.toString(), _callback);
                done = true;
                _Dialog.dismiss();
            }
        });
    }
}
