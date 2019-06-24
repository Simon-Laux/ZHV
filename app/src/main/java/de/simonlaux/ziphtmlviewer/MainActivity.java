package de.simonlaux.ziphtmlviewer;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.os.Bundle;

public class MainActivity extends Activity {

    TextView trouble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.trouble = findViewById(R.id.troubleshooting);
        this.trouble.setVisibility(View.INVISIBLE);
        TextView version_code = findViewById(R.id.version_code);
        version_code.setText(String.valueOf(BuildConfig.VERSION_CODE));
    }

    public void toggleTroubleshooting(View view) {
        if (this.trouble.getVisibility() == View.VISIBLE)
            this.trouble.setVisibility(View.INVISIBLE);
        else
            this.trouble.setVisibility(View.VISIBLE);
    }
    
}

