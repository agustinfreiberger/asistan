package ar.edu.unicen.isistan.asistan.views.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import ar.edu.unicen.isistan.asistan.R;

public class PrivacyPolicyActivity extends Activity {

    private Button acceptButton;
    private WebView consentText;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_privacy_policy);

        this.acceptButton = this.findViewById(R.id.acceptButton);
        this.consentText = this.findViewById(R.id.privacyPolicy);
        this.loading = this.findViewById(R.id.loading);

        this.acceptButton.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);
        });

        this.consentText.clearCache(false);
        this.consentText.setWebViewClient(new PrivacyWebClient());
        this.consentText.loadUrl("https://docs.google.com/document/d/1cirV8E6ygpb2HLIwQ0iZC9dNS4B7WScWpc8mcuV8mBA/edit?usp=sharing");

        this.onStart();
    }

    private class PrivacyWebClient extends WebViewClient {

        private boolean redirect;
        private boolean loadingFinished;

        public PrivacyWebClient() {
            this.redirect = false;
            this.loadingFinished = false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
            if (!this.loadingFinished)
                redirect = true;
            view.loadUrl(urlNewString);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view,url,favicon);
            this.loadingFinished = false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view,url);
            if (!redirect) {
                PrivacyPolicyActivity.this.acceptButton.setVisibility(View.VISIBLE);
                PrivacyPolicyActivity.this.consentText.setVisibility(View.VISIBLE);
                PrivacyPolicyActivity.this.loading.setVisibility(View.GONE);
            } else {
                redirect = false;
            }
        }

    }
}
