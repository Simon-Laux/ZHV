package de.simonlaux.ziphtmlviewer;

import android.app.Activity;
import java.util.Objects;
import android.os.Bundle;
import java.io.InputStream;
import java.io.IOException;
import android.net.Uri;
import android.widget.Toast;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

public class OpenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getActionBar().hide();
        } catch (NullPointerException e) {
        }

        setContentView(R.layout.activity_reader);

        handleIntent();
    }

    private void handleIntent() {

        Uri uri = getIntent().getData();
        if (uri == null) {
            tellUserThatCouldNotOpenFile();
            return;
        }

        String text = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (!uri.getPath().contains("zip")) {
                // html mode
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }   
                text = result.toString();
            } else {
                // zip html mode
                text = getStringFromZip(inputStream);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (text == null) {
            tellUserThatCouldNotOpenFile();
            return;
        }

        WebView myWebView = findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.loadDataWithBaseURL("file://index.html", text, "text/html", null, null);
    }

    private void tellUserThatCouldNotOpenFile() {
        Toast.makeText(this, getString(R.string.could_not_open_file), Toast.LENGTH_SHORT).show();
    }

    public static String getStringFromZip(InputStream stream) throws IOException {
        ByteArrayOutputStream fout = new ByteArrayOutputStream();
        if (!unpackZip(stream, fout)) {
            throw new IOException();
        }
        return fout.toString();
    }

    private static boolean unpackZip(InputStream is, ByteArrayOutputStream fout) {
        ZipInputStream zis;
        try {
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;
            while ((ze = zis.getNextEntry()) != null) {
                if (ze.isDirectory())
                    continue;
                if (!Objects.equals(ze.getName(), "index.html"))
                    continue;

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