package eu.epitech.fernan_s.msa_m.yourimage.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.IApi;

/**
 * Created by quent on 30/01/2017.
 */

public class AuthDialog extends Dialog {
    WebView _wv = null;
    Dialog _diag = this;

    public AuthDialog(final Context context, final IApi api) {
        super(context);
        this.setContentView(R.layout.authdialog);
        _wv = (WebView)this.findViewById(R.id.authView);
        _wv.getSettings().setJavaScriptEnabled(true);
        _wv.loadUrl(api.getAuthlink());
        _wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("code=") || url.contains("access_token=")) {
                    api.auth(url);
                    Log.d("Bondour", url);
                    _diag.dismiss();
                    Toast.makeText(context, "Auth Done", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
