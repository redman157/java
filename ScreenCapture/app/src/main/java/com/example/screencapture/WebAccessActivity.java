package com.example.screencapture;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class WebAccessActivity extends AppCompatActivity implements View.OnClickListener
{
    private View viewLoading;
    private WebView webView;
    private Button btnBack;
    private ImageButton ibScreen;
    private FrameLayout flWeb;
    private boolean isLoad = false;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView.enableSlowWholeDocumentDraw();
        setContentView(R.layout.activity_web_access);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        ibScreen = findViewById(R.id.ibScreen);
        btnBack = findViewById(R.id.btnBack);
        viewLoading = findViewById(R.id.layout_loading);
        webView = findViewById(R.id.wvHome);
        initData(url);
        flWeb = findViewById(R.id.flWeb);





        ibScreen.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void initData(String url) {
        webView.setWebViewClient(webViewClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.loadUrl(url);

    }

    public Bitmap screenshot(WebView webView) {
        try {
            float scale = webView.getScale();
            int height = (int) (webView.getContentHeight() * scale + 0.5);
            Bitmap bitmap = Bitmap.createBitmap(webView.getWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            webView.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void storeScreenshot(Bitmap bitmap, Date filename) throws IOException {
        File file = new File(Environment.getExternalStorageDirectory() +File.separator +"ScreenCapture");
        if (!file.exists()){
            file.mkdir();
        }
        String path = Environment.getExternalStorageDirectory().toString() + "/"+"ScreenCapture"+File.separator + filename+".jpg";
        FileOutputStream out = null;
        File imageFile = new File(path);

        try {
            out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } finally {

            try {
                if (out != null) {
                    out.close();
                }

            } catch (Exception exc) {
                exc.printStackTrace();
            }

        }
    }

    private WebViewClient webViewClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            viewLoading.setVisibility(View.VISIBLE);
            isLoad = false;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            viewLoading.setVisibility(View.GONE);
            isLoad = true;
            Log.d("KKK", "" + CookieManager.getInstance().getCookie(url));
        }
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack:
                finish();
                break;
            case R.id.ibScreen:
                if (isLoad == true) {
//                    takeScreenshot();
                    Log.d("BBB","Chụp ảnh : Enter");
                    Date now = new Date();
                    android.text.format.DateFormat.format("hh:mm:ss", now);
                    Log.d("BBB","Chụp ảnh tên : Enter");
                    try {
                        storeScreenshot(screenshot(webView), now);
                    } catch (IOException e) {

                    }
                    flWeb.setVisibility(View.VISIBLE);

                    AlphaAnimation fade = new AlphaAnimation(1, 0);
                    fade.setDuration(400);
                    fade.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            flWeb.setVisibility(View.GONE);
                            Log.d("BBB","dang chụp : Enter");
                        }

                        @Override
                        public void onAnimationEnd(Animation anim) {
                            flWeb.setVisibility(View.VISIBLE);
                            Log.d("BBB","Chụp xong : Enter");
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    flWeb.startAnimation(fade);

                }
                Log.d("BBB","kết thúc ảnh : Enter");

                break;
        }
    }
}
