package com.fernandoaraujo.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static int LOCATION_REQUEST_CODE_ID = 111;
    private LocationManager locationManager;
    private Criteria criteria;
    private String currentZip;
    private String API_KEY = "XXXXX";
    private List<Official> officialList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OfficialAdapter oAdapter;
    private int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        oAdapter = new OfficialAdapter(officialList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(oAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();

        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    LOCATION_REQUEST_CODE_ID);
        } else {
            getLocation();
        }
    }

    @Override
    public void onClick(View view) {

        pos = recyclerView.getChildLayoutPosition(view);
        Official selection = officialList.get(pos);

        Intent intent = new Intent(this, OfficialInfoActivity.class);
        intent.putExtra("OFFICIAL", selection);
        intent.putExtra("LOCATION", ((TextView) findViewById(R.id.currentLocation)).getText().toString());
        startActivity(intent);

    }

    @SuppressLint("MissingPermission")
    private void getLocation() {

        boolean connected = isConnected();

        String bestProvider = locationManager.getBestProvider(criteria, true);

        Location currentLocation = locationManager.getLastKnownLocation(bestProvider);
        if (currentLocation != null && connected) {

            Geocoder geoc = new Geocoder(this, Locale.getDefault());

            List<Address> addressList;

            try {
                addressList = geoc.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 5);

                setAddress(addressList);

            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            ((TextView) findViewById(R.id.currentLocation)).setText(R.string.no_loc);
            triggerNoNetworkAlert();
            //just for testing
            //new OfficialDownloader_AsyncTask(this).execute(API_KEY, "60181");

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuSearch:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enter a City, State or Zip Code:");

                final EditText et = new EditText(this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setGravity(Gravity.CENTER_HORIZONTAL);
                builder.setView(et);
                final MainActivity tempActivty = this;

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {

                        officialList.clear();

                        if(isConnected()) {
                            new OfficialDownloader_AsyncTask(tempActivty).execute(API_KEY, et.getText().toString());
                        }else{
                            ((TextView) findViewById(R.id.currentLocation)).setText(R.string.no_loc);
                            triggerNoNetworkAlert();
                        }
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });

                AlertDialog ad = builder.create();
                ad.show();

                return true;

            case R.id.menuAbout:

                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setAddress(List<Address> addresses) {

        String city = "";
        String state = "";
        String zip = "";

        if (addresses.size() == 0) {
            //((TextView) findViewById(R.id.textView)).setText(R.string.nothing_found);

            return;
        }

        for (Address ad : addresses) {

            if (zip.equals("")) {
                zip = (ad.getPostalCode() == null ? "" : ad.getPostalCode());
            }

            if (city.equals("")) {
                city = (ad.getLocality() == null ? "" : ad.getLocality());
            }

            if (state.equals("")) {
                state = (ad.getAdminArea() == null ? "" : ad.getAdminArea());
            }

        }

        String addressInput = zip;

        if (addressInput.equals("")) {
            addressInput = city;
        }

        if (addressInput.equals("")) {
            addressInput = state;
        }

        new OfficialDownloader_AsyncTask(this).execute(API_KEY, addressInput);

    }


    public void handleOfficialReturn(List<Official> officials) {

        officialList.addAll(officials);
        oAdapter.notifyDataSetChanged();

    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private void triggerNoNetworkAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Data cannot be accessed/loaded without an internet connection.");
        AlertDialog ad = builder.create();
        ad.show();
    }
}
