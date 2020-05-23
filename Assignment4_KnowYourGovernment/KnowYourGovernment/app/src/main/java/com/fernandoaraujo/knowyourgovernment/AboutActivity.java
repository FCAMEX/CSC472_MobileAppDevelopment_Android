package com.fernandoaraujo.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    TextView apiLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        apiLink = findViewById(R.id.apiText);
        apiLink.setPaintFlags(apiLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

    }

    public void apiClicked(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://developers.google.com/civic-information/"));
        startActivity(i);

    }

}
