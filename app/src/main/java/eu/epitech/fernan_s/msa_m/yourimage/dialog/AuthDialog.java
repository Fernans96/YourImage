package eu.epitech.fernan_s.msa_m.yourimage.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import eu.epitech.fernan_s.msa_m.yourimage.R;
import eu.epitech.fernan_s.msa_m.yourimage.model.api.IApi;

/**
 * Created by quent on 30/01/2017.
 */

public class AuthDialog extends Dialog {
    private WebView _wv = null;
    private Dialog _diag = this;
    private ProgressBar progress;
    private boolean _done = false;

    public AuthDialog(final Context context, final IApi api, final IApi.ConnectCallback callback) {
        super(context);
        this.setContentView(R.layout.authdialog);

        progress = (ProgressBar) findViewById(R.id.progressBar);

        AuthDialog.this.progress.setProgress(0);
        _wv = (WebView)this.findViewById(R.id.authView);
        _wv.getSettings().setJavaScriptEnabled(true);
        _wv.getSettings().setUseWideViewPort(true);
        _wv.getSettings().setLoadWithOverviewMode(true);
        _wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        api.getAuthlink(new IApi.AuthLinkCallback() {
            @Override
            public void onAuthLinkFinished(final String authlink) {
                Handler handler = new Handler(context.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        _wv.loadUrl(authlink);
                    }
                });
            }
        });

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!_done) {
                    callback.onConnectFailed();
                }
            }
        });


        _wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                view.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progress.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
                if (url.contains("code=") || url.contains("oauth_verifier=") || url.contains("access_token=")) {
                    api.auth(url, callback);
                    _done = true;
                    _diag.dismiss();
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }
        });
    }
}
