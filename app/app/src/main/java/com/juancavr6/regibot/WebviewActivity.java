package com.juancavr6.regibot;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.juancavr6.regibot.R;


public class WebviewActivity extends AppCompatActivity {

    private WebView myWebView;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        myWebView = findViewById(R.id.webview);

        // Habilitar JavaScript si es necesario
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Asegura que el contenido se abra dentro del WebView, no en el navegador
        myWebView.setWebViewClient(new WebViewClient());

        // Cargar una URL
        myWebView.loadUrl("https://github.com/Juancavr6/RegiBot/blob/main/README.md");

        // Configurar el botón de retroceso
        backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> {
            if (myWebView.canGoBack()) {
                myWebView.goBack();
            } else {
                finish();
            }
        });
    }
    @Override
    public void onDestroy() {
        if (myWebView != null) {
            myWebView.clearHistory();
            myWebView.clearCache(true);
            myWebView.removeAllViews();
            myWebView.destroy();
        }

        super.onDestroy();

    }

}