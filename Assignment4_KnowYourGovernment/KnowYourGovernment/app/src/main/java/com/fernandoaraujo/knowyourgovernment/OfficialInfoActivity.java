package com.fernandoaraujo.knowyourgovernment;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OfficialInfoActivity extends AppCompatActivity {

    private static final String TAG = "OfficialInfoActivity";

    private TextView locationLabel;
    private TextView addressLabel;
    private TextView phoneLabel;
    private TextView emailLabel;
    private TextView webLabel;

    private TextView infoOffice;
    private TextView infoName;
    private TextView infoParty;
    private TextView infoAddress;
    private TextView infoPhone;
    private TextView infoEmail;
    private TextView infoWebsite;

    private ImageView officerPhoto;
    private  ImageView partyIcon;
    private ImageView facebookIcon;
    private ImageView twitterIcon;
    private ImageView youtubeIcon;
    private ImageView googleIcon;

    Official tempOfficial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_info);

        locationLabel = findViewById(R.id.currentLocation2);
        addressLabel = findViewById(R.id.addressLabel);
        phoneLabel = findViewById(R.id.phoneLabel);
        emailLabel = findViewById(R.id.emailLabel);
        webLabel = findViewById(R.id.webLabel);

        infoOffice = findViewById(R.id.infoOffice);
        infoName = findViewById(R.id.infoName);
        infoParty = findViewById(R.id.infoParty);
        infoAddress = findViewById(R.id.infoAddress);
        infoPhone = findViewById(R.id.infoPhone);
        infoEmail = findViewById(R.id.infoEmail);
        infoWebsite = findViewById(R.id.infoWebsite);

        officerPhoto = findViewById(R.id.infoImage);
        partyIcon = findViewById(R.id.infoLogo);
        facebookIcon = findViewById(R.id.infoFacebook);
        googleIcon = findViewById(R.id.infoGoogle);
        twitterIcon = findViewById(R.id.infoTwitter);
        youtubeIcon = findViewById(R.id.infoYoutube);

        Intent intent = getIntent();

        if (intent.hasExtra("LOCATION")) {
            locationLabel.setText((String) intent.getSerializableExtra("LOCATION"));
        }

        if (intent.hasExtra("OFFICIAL")) {
            tempOfficial = (Official) intent.getSerializableExtra("OFFICIAL");
            infoOffice.setText(tempOfficial.getOffice());
            infoName.setText(tempOfficial.getName());
            infoParty.setText("(" + tempOfficial.getParty() + ")");

            if (!tempOfficial.getAddresses().isEmpty()) {
                addressLabel.setVisibility(View.VISIBLE);
                infoAddress.setVisibility(View.VISIBLE);

                String tempAddresses = "";
                int count = 0;
                for(String address : tempOfficial.getAddresses()){
                    if(count == tempOfficial.getAddresses().size() -1){
                        tempAddresses += address;
                    }else {
                        tempAddresses += address + "\n";
                    }
                    count++;
                }
                infoAddress.setText(tempAddresses);
                //Linkify.addLinks(infoAddress, Linkify.ALL);
                infoAddress.setPaintFlags(infoAddress.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            }else{
                addressLabel.setVisibility(View.GONE);
                infoAddress.setVisibility(View.GONE);
            }

            if (!tempOfficial.getPhone().isEmpty()) {
                phoneLabel.setVisibility(View.VISIBLE);
                infoPhone.setVisibility(View.VISIBLE);
                infoPhone.setText(tempOfficial.getPhone());
                Linkify.addLinks(infoPhone, Linkify.ALL);
            }else{
                emailLabel.setVisibility(View.GONE);
                infoEmail.setVisibility(View.GONE);
            }

            if (!tempOfficial.getEmail().isEmpty()) {
                emailLabel.setVisibility(View.VISIBLE);
                infoEmail.setVisibility(View.VISIBLE);
                infoEmail.setText(tempOfficial.getEmail());
                Linkify.addLinks(infoEmail, Linkify.ALL);
            }else{
                emailLabel.setVisibility(View.GONE);
                infoEmail.setVisibility(View.GONE);
            }

            if (!tempOfficial.getWebsiteURL().isEmpty()) {
                webLabel.setVisibility(View.VISIBLE);
                infoWebsite.setVisibility(View.VISIBLE);
                infoWebsite.setText(tempOfficial.getWebsiteURL());
                Linkify.addLinks(infoWebsite, Linkify.ALL);
            }else{
                webLabel.setVisibility(View.GONE);
                infoWebsite.setVisibility(View.GONE);
            }

            if (tempOfficial.getParty().contains("Democratic")) {
                partyIcon.setVisibility(View.VISIBLE);
                partyIcon.setImageResource(R.drawable.dem_logo);
                getWindow().getDecorView().setBackgroundColor(Color.BLUE);

                partyIcon.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Uri uri = Uri.parse("https://democrats.org");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });

            } else if (tempOfficial.getParty().contains("Republican")) {
                partyIcon.setVisibility(View.VISIBLE);
                partyIcon.setImageResource(R.drawable.rep_logo);
                getWindow().getDecorView().setBackgroundColor(Color.RED);

                partyIcon.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Uri uri = Uri.parse("https://gop.gov");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
            } else {
                getWindow().getDecorView().setBackgroundColor(Color.BLACK);
                partyIcon.setVisibility(View.GONE);
            }

            if (!tempOfficial.getFacebookID().isEmpty()) {
                facebookIcon.setVisibility(View.VISIBLE);
            }else{
                facebookIcon.setVisibility(View.GONE);
            }

            if (!tempOfficial.getTwitterID().isEmpty()) {
                twitterIcon.setVisibility(View.VISIBLE);
            }else{
                twitterIcon.setVisibility(View.GONE);
            }

            if (!tempOfficial.getGooglePlusID().isEmpty()) {
                googleIcon.setVisibility(View.VISIBLE);
            }else{
                googleIcon.setVisibility(View.GONE);
            }

            if (!tempOfficial.getYoutubeID().isEmpty()) {
                youtubeIcon.setVisibility(View.VISIBLE);
            }else{
                youtubeIcon.setVisibility(View.GONE);
            }

            final OfficialInfoActivity tempActivty = this;

            if(!tempOfficial.getPhotoURL().isEmpty()){

                loadOfficialPhoto(tempOfficial.getPhotoURL());

                officerPhoto.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(tempActivty, OfficialPhotoActivity.class);

                        intent.putExtra("OFFICIAL", tempOfficial);
                        intent.putExtra("LOCATION", ((TextView) findViewById(R.id.currentLocation2)).getText().toString());
                        startActivity(intent);
                    }
                });
            }else{
                officerPhoto.setImageResource(R.drawable.brokenimage);
            }


        } else {

        }
    }

    public void facebookClicked(View v) {

        String FACEBOOK_URL = "https://www.facebook.com/" + tempOfficial.getFacebookID();
        String urlToUse;

        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + tempOfficial.getFacebookID();
            }

        } catch (PackageManager.NameNotFoundException n) {
            urlToUse = FACEBOOK_URL;
        }
        Intent fbIntent = new Intent(Intent.ACTION_VIEW);
        fbIntent.setData(Uri.parse(urlToUse));
        startActivity(fbIntent);
    }

    public void twitterClicked(View v) {
        String twitterAppUrl = "twitter://user?screen_name=" + tempOfficial.getTwitterID();
        String twitterWebUrl = "https://twitter.com/" + tempOfficial.getTwitterID();

        Intent intent;
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterAppUrl));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterWebUrl));
        }
        startActivity(intent);
    }

    public void googlePlusClicked(View v) {
        Intent intent = null;

        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus", "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("cutomAppUri", tempOfficial.getGooglePlusID());
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + tempOfficial.getGooglePlusID())));
        }

    }

    public void youtubeClicked(View v) {
        Intent intent = null;

        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + tempOfficial.getYoutubeID()));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + tempOfficial.getYoutubeID())));
        }
    }

    private void loadOfficialPhoto(final String imageURL) {

        Log.d(TAG, "loadImage: " + imageURL);

        Picasso picasso = new Picasso.Builder(this).build();
        picasso.load(imageURL)
                .error(R.drawable.missing)
                .placeholder(R.drawable.placeholder)
                .into(officerPhoto);
    }


    //tried using Linkify instead, but it did not generate links properly. The entire address wasn't highlighted nor clickable
    public void addressClicked(View v) {
        String address = tempOfficial.getAddresses().get(tempOfficial.getAddresses().size()-1);
        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));

        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("No Map App Found");
            builder.setMessage("No Application found that handles ACTION_VIEW (geo) intents");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
