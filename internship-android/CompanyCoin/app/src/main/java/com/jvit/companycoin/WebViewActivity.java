package com.jvit.companycoin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView imgBack;
    WebView myWebView;
    ImageView imgBackWeb, imgNextWeb, imgStopWeb, imgReloadWeb ;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);


        initView();

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        showWebView(url);

        imgBack.setOnClickListener(this);
        imgBackWeb.setOnClickListener(this);
        imgNextWeb.setOnClickListener(this);
        imgStopWeb.setOnClickListener(this);
        imgReloadWeb.setOnClickListener(this);
    }


    private void showWebView(String url){
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClientDemo());
        myWebView.setWebChromeClient(new WebChromeClientDemo());
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(false);
        myWebView.loadUrl(url);
    }


    private void initView(){
        progressBar = findViewById(R.id.progressBarLoadWeb);
        progressBar.setMax(100);
        imgBack = findViewById(R.id.img_backWebViewActivity);
        myWebView = findViewById(R.id.webView);
        imgBackWeb = findViewById(R.id.imgBackWebView);
        imgStopWeb = findViewById(R.id.imgStopWebView);
        imgReloadWeb = findViewById(R.id.imgReloadWebView);
        imgNextWeb = findViewById(R.id.imgNextWebView);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBackWebView:

                imgBackWeb.setImageResource(R.drawable.left_gray);
                myWebView.goBack();
                break;
            case R.id.imgNextWebView:

                myWebView.goForward();
                imgNextWeb.setImageResource(R.drawable.right_gray);
                break;
            case R.id.imgStopWebView:
                imgStopWeb.setImageResource(R.drawable.cancel_orange);
                myWebView.stopLoading();
                break;
            case R.id.imgReloadWebView:
                imgReloadWeb.setImageResource(R.drawable.reload_orange);
                myWebView.reload();
                break;
            case R.id.img_backWebViewActivity:
                finish();
                break;
        }
    }
    private class WebViewClientDemo extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            progressBar.setProgress(100);
            imgStopWeb.setImageResource(R.drawable.cancel_gray);
            imgReloadWeb.setImageResource(R.drawable.reload_gray);

            if (myWebView.canGoForward()){
                myWebView.goForward();
                imgNextWeb.setImageResource(R.drawable.right_orange);
            }else {
                imgNextWeb.setImageResource(R.drawable.right_gray);
            }

            if (myWebView.canGoBack()) {
                imgBackWeb.setImageResource(R.drawable.left_orange);
                myWebView.goBack();
            } else{
                imgBackWeb.setImageResource(R.drawable.left_gray);
            }

        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);

        }
    }
    private class WebChromeClientDemo extends WebChromeClient {
        public void onProgressChanged(WebView view, int progress) {
            progressBar.setProgress(progress);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        else {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
