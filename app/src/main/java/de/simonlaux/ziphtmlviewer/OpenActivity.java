package de.simonlaux.ziphtmlviewer;

import android.app.Activity;
import android.os.Bundle;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.IOException;
import android.net.Uri;
import android.widget.Toast;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import java.io.FileNotFoundException;

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

        TextView textView = findViewById(R.id.tv_content);
        textView.setText(text);
    }

    private void tellUserThatCouldntOpenFile() {
        Toast.makeText(this, getString(R.string.could_not_open_file), Toast.LENGTH_SHORT).show();
    }

    public static String getStringFromInputStream(InputStream stream) throws IOException {
        int n = 0;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(stream, "UTF8");
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer)))
            writer.write(buffer, 0, n);
        return writer.toString();
    }

}