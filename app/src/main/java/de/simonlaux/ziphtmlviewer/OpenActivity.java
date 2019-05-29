package de.simonlaux.ziphtmlviewer;

import android.app.Activity;
import android.os.Bundle;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.IOException;
import android.net.Uri;
import android.widget.Toast;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import android.support.v7.app.AppCompatActivity;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.util.Base64;

public class OpenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        handleIntent();
    }

    private void handleIntent() {

        Uri uri = getIntent().getData();
        if (uri == null) {
            tellUserThatCouldntOpenFile();
            return;
        }

        String text = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            text = getStringFromInputStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (text == null) {
            tellUserThatCouldntOpenFile();
            return;
        }

        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true); 
        webSettings.setBuiltInZoomControls(true);
        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.loadDataWithBaseURL("file://index.html", text, "text/html", null, null);
    }

    private void tellUserThatCouldntOpenFile() {
        Toast.makeText(this, getString(R.string.could_not_open_file), Toast.LENGTH_SHORT).show();
    }

    public static String getStringFromInputStream(InputStream stream) throws IOException {
        ByteArrayOutputStream fout = new ByteArrayOutputStream();
        if (!unpackZip(stream, fout)) {
            throw new IOException();
        }
        return fout.toString();
    }

    private static boolean unpackZip(InputStream is, ByteArrayOutputStream fout) {
        ZipInputStream zis;
        try {
            String filename;
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;
            while ((ze = zis.getNextEntry()) != null) {
                if(ze.getName() != "index.html"){
                    continue;
                }
                if (ze.isDirectory()) {
                    continue;
                }
                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }
                zis.closeEntry();
            }
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}