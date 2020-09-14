package de.simonlaux.ziphtmlviewer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.simonlaux.ziphtmlviewer.R;

public class OpenActivity extends Activity implements View.OnClickListener {
    private static final String MD_TEXT  = "MD_TEXT";
    private static final String FIRST_TIME_INIT = "FirstTimeInit";
    private static final String IS_SEARCHING = "Searching";
    private static final String TITLE_WEB = "TitleWeb";
    private boolean firstTimeInit = true;
    private boolean isSearching = false;
    private JsObject jsObject;
    private String titleWEB ="ZHV";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            firstTimeInit = savedInstanceState.getBoolean(FIRST_TIME_INIT, true);
            isSearching = savedInstanceState.getBoolean(IS_SEARCHING, false);
            titleWEB =savedInstanceState.getString(TITLE_WEB, titleWEB);
            String text=savedInstanceState.getString(MD_TEXT,"");
            if(text==""){
                jsObject=new JsObject("",false);
            }else{
                jsObject=new JsObject(text,true);
            }
        }
        /*Don't hide the bar for use it
        try {
            //this.getActionBar().hide();
        } catch (NullPointerException e) {
        }*/
        setContentView(R.layout.activity_reader);
        if (firstTimeInit) {
            handleIntent();
        } else {
            WebView myWebView = findViewById(R.id.webview);
            registerForContextMenu(myWebView);
            myWebView.restoreState(savedInstanceState);
            configureWebView(myWebView);
            this.setTitle(titleWEB);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void configureWebView(WebView web) {
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDomStorageEnabled(true);
        web.addJavascriptInterface(jsObject, "zhv");
        web.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                OpenActivity.this.titleWEB = title;
                OpenActivity.this.setTitle(title);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search_menu_item).getActionView();
        searchView.setIconifiedByDefault(true);
        //searchView.setIconified(true);
        searchView.setImeOptions(EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS | EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NAVIGATE_NEXT);
        searchView.setOnSearchClickListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                isSearching = false;
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                WebView myWebView = findViewById(R.id.webview);
                myWebView.findNext(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                WebView myWebView = findViewById(R.id.webview);
                myWebView.findAllAsync(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public void onBackPressed() {
        if (isSearching) {
            SearchView searchView = findViewById(R.id.search_menu_item);
            //if text closes it completly
            searchView.setIconified(true);
            searchView.setIconified(true);
            isSearching = false;
        } else {
            super.onBackPressed();
        }

    }

    private void handleIntent() {
        Uri uri = getIntent().getData();
        if (uri == null) {
            tellUserThatCouldNotOpenFile();
            return;
        }
        String text = null;
        boolean isMarkdown = false;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (uri.getPath() != null) {
                String path = uri.getPath();
                if (path.endsWith(".html.zip") || path.endsWith(".htmlzip")) {
                    // zip html mode
                    text = getStringFromZip(inputStream);
                } else {
                    // load file into memory (html or markdown)
                    ByteArrayOutputStream result = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        result.write(buffer, 0, length);
                    }
                    text = result.toString();
                    if (path.endsWith(".md")) {
                        // markdown mode
                        isMarkdown = true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (text == null) {
            tellUserThatCouldNotOpenFile();
            return;
        }

        WebView myWebView = findViewById(R.id.webview);
        jsObject = new JsObject(text, isMarkdown);
        configureWebView(myWebView);

        // myWebView.setWebContentsDebuggingEnabled(true);

        myWebView.loadData("", "text/html", null);
        if(isMarkdown){
            // markdown mode
            myWebView.loadUrl("file:///android_asset/markdown-reader.html");
        } else {
            // html mode
            myWebView.loadDataWithBaseURL("file://index.html", text, "text/html", null, null);
        }
        firstTimeInit = false;
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
                //Objects esta del api 19 en adelante
                if (!ze.getName().contentEquals("index.html"))
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        WebView myWebView = findViewById(R.id.webview);
        myWebView.saveState(outState);
        outState.putBoolean(IS_SEARCHING, isSearching);
        outState.putBoolean(FIRST_TIME_INIT, firstTimeInit);
        outState.putString(TITLE_WEB,titleWEB);
        outState.putString(MD_TEXT,jsObject.getMarkdown());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        isSearching = true;
    }


    class JsObject {
        String contentText="";
        boolean isMarkdownReader=false;
        public JsObject(String contentText,boolean isMarkdownReader){
            this.contentText=contentText;
            this.isMarkdownReader=isMarkdownReader;
        }

        @JavascriptInterface
        public int getVersion() { return 15; }
        @JavascriptInterface
        public String toString() { return "[ZippedHTMLViewer Object]"; }
        @JavascriptInterface
        public String getMarkdown() {
            if(isMarkdownReader){
                return contentText;
            } else {
                return "";
            }
        }
    }
}
