package com.fernandoaraujo.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OfficialPhotoActivity extends AppCompatActivity {

    private static final String TAG = "OfficialPhotoActivity";

    private TextView locationLabel;
    private TextView photoOffice;
    private TextView photoName;
    private ImageView photoImage;
    private ImageView photoLogo;

    private Official tempOfficial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_photo);

        photoOffice = findViewById(R.id.photoOffice);
        photoName = findViewById(R.id.photoName);
        photoImage = findViewById(R.id.photoImage);
        photoLogo = findViewById(R.id.photoLogo);
        locationLabel = findViewById(R.id.currentLocation3);

        Intent intent = getIntent();

        if (intent.hasExtra("LOCATION")) {
            locationLabel.setText((String) intent.getSerializableExtra("LOCATION"));
        }

        if (intent.hasExtra("OFFICIAL")) {
            tempOfficial = (Official) intent.getSerializableExtra("OFFICIAL");

            photoOffice.setText(tempOfficial.getOffice());
            photoName.setText(tempOfficial.getName());

            if (tempOfficial.getParty().contains("Democratic")) {
                photoLogo.setVisibility(View.VISIBLE);
                photoLogo.setImageResource(R.drawable.dem_logo);
                getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                photoLogo.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Uri uri = Uri.parse("https://democrats.org");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
            }else if(tempOfficial.getParty().contains("Republican")){
                photoLogo.setVisibility(View.VISIBLE);
                photoLogo.setImageResource(R.drawable.rep_logo);
                getWindow().getDecorView().setBackgroundColor(Color.RED);
                photoLogo.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Uri uri = Uri.parse("https://gop.gov");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
            }else{
                photoLogo.setVisibility(View.GONE);
                getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            }

            loadOfficialPhoto(tempOfficial.getPhotoURL());

        }
    }


    private void loadOfficialPhoto(final String imageURL) {

        Log.d(TAG, "loadImage: " + imageURL);

        Picasso picasso = new Picasso.Builder(this).build();
        picasso.load(imageURL)
                .error(R.drawable.missing)
                .placeholder(R.drawable.placeholder)
                .into(photoImage);
    }
}
