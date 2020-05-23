package com.fernandoaraujo.knowyourgovernment;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OfficialDownloader_AsyncTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "OfficialDownloader_Asyn";
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    private static final String apiBase = "https://www.googleapis.com/civicinfo/v2/representatives";
    private static final String quoteBody1 = "key";
    private static final String quoteBody2 = "address";
    private JSONObject result = new JSONObject();
    private String mode = "";


    OfficialDownloader_AsyncTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    protected String doInBackground(String... dataIn) {

        if(android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();

        String apiToken = dataIn[0];
        String address = dataIn[1];

        Log.d(TAG, "doInBackground: " + address);

        Uri.Builder buildURL = Uri.parse(apiBase ).buildUpon();
        buildURL.appendQueryParameter(quoteBody1, apiToken);
        buildURL.appendQueryParameter(quoteBody2, address);
        String urlToUse = buildURL.build().toString();

        while (urlToUse != null) {

            Log.d(TAG, "doInBackground: " + urlToUse);

            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(urlToUse);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                conn.setRequestMethod("GET");

                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }

                urlToUse = parseJSONResults(sb.toString());

            } catch (Exception e) {
                Log.e(TAG, "doInBackground: ", e);
                return null;
            }
        }

        return result.toString();
    }

    private String parseJSONResults(String s) {

        try {
            result = new JSONObject(s);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        List<Official> officialList = new ArrayList<Official>();
        Official parsedOfficial = null;

        try {

            JSONObject normalizedInput = result.getJSONObject("normalizedInput");
            JSONArray offices = result.getJSONArray("offices");
            JSONArray officials = result.getJSONArray("officials");

            String city = normalizedInput.getString("city").trim();
            String state = normalizedInput.getString("state").trim();
            String zip = normalizedInput.getString("zip").trim();


            ((TextView) mainActivity.findViewById(R.id.currentLocation)).setText(city + ", " + state + " " + zip);

            for(int i = 0; i < offices.length(); i++){
                JSONObject temp = offices.getJSONObject(i);
                JSONArray officialIndexes = temp.getJSONArray("officialIndices");
                String office = temp.getString("name").trim();

                for(int x = 0; x < officialIndexes.length(); x++){
                    int tempIndex = officialIndexes.getInt(x);
                    JSONObject tempOfficialData = officials.getJSONObject(tempIndex);

                    Official tempOfficial = new Official();
                    tempOfficial.setOffice(office);

                    String name = tempOfficialData.getString("name");
                    tempOfficial.setName(name);


                    List<String> addresses = new ArrayList<String>();

                    if(tempOfficialData.has("address")) {
                        JSONArray addressArray = tempOfficialData.getJSONArray("address");

                        for(int p = 0; p < addressArray.length(); p++) {

                            String address = "";
                            String line1 = "";
                            String line2 = "";
                            String line3 = "";
                            String subCity = "";
                            String subState = "";
                            String subZip = "";

                            JSONObject addressObject = addressArray.getJSONObject(p);

                            if (addressObject.has("line1")) {
                                line1 = addressObject.getString("line1").trim();
                            }

                            if (addressObject.has("line2")) {
                                line2 = addressObject.getString("line2").trim();
                            }

                            if (addressObject.has("line3")) {
                                line3 = addressObject.getString("line3").trim();
                            }
                            if (addressObject.has("city")) {
                                subCity = addressObject.getString("city").trim();
                            }
                            if (addressObject.has("state")) {
                                subState = addressObject.getString("state").trim();
                            }
                            if (addressObject.has("zip")) {
                                subZip = addressObject.getString("zip").trim();
                            }

                            if (!line1.equals("")) {
                                address = address + line1 + "\n";
                            }
                            if (!line2.equals("")) {
                                address = address + line2 + "\n";
                            }
                            if (!line3.equals("")) {
                                address = address + line3 + "\n";
                            }

                            if(p == addressArray.length()-1) {
                                address = address + subCity + ", " + subState + " " + subZip;
                            }else{
                                address = address.trim();
                            }

                            addresses.add(address);
                        }
                    }

                    tempOfficial.setAddresses(addresses);

                    String party = "";
                    if(tempOfficialData.has("party")) {
                        party = tempOfficialData.getString("party");
                        if (party.equals("")) {
                            party = "Unknow";
                        }
                    }
                    tempOfficial.setParty(party);

                    String phone = "";
                    if(tempOfficialData.has("phones")) {
                        JSONArray phoneArray = tempOfficialData.getJSONArray("phones");

                        if (phoneArray != null && phoneArray.length() > 0) {
                            phone = phoneArray.getString(0);
                        }
                    }
                    tempOfficial.setPhone(phone);

                    String websiteUrl = "";
                    if(tempOfficialData.has("urls")) {
                        JSONArray weburlArray = tempOfficialData.getJSONArray("urls");

                        if (weburlArray != null && weburlArray.length() > 0) {
                            websiteUrl = weburlArray.getString(0);
                        }
                    }
                    tempOfficial.setWebsiteURL(websiteUrl);

                    String email = "";
                    if(tempOfficialData.has("emails")) {
                        JSONArray emailArray = tempOfficialData.getJSONArray("emails");

                        if (emailArray != null && emailArray.length() > 0) {
                            email = emailArray.getString(0);
                        }
                    }
                    tempOfficial.setEmail(email);

                    String photoURL = "";
                    if(tempOfficialData.has("photoUrl")) {
                        photoURL = tempOfficialData.getString("photoUrl").trim();
                    }
                    tempOfficial.setPhotoURL(photoURL);


                    String googleID = "";
                    String facebookId = "";
                    String twitterId = "";
                    String youtubeId = "";

                    if(tempOfficialData.has("channels")) {
                        JSONArray channelArray = tempOfficialData.getJSONArray("channels");

                        for (int f = 0; f < channelArray.length(); f++) {
                            JSONObject tempChannel = channelArray.getJSONObject(f);

                            if(tempChannel.has("type") && tempChannel.has("id")) {
                                if (tempChannel.getString("type").equals("GooglePlus")) {
                                    googleID = tempChannel.getString("id");
                                }

                                if (tempChannel.getString("type").equals("Facebook")) {
                                    facebookId = tempChannel.getString("id");
                                }

                                if (tempChannel.getString("type").equals("Twitter")) {
                                    twitterId = tempChannel.getString("id");
                                }

                                if (tempChannel.getString("type").equals("YouTube")) {
                                    youtubeId = tempChannel.getString("id");
                                }
                            }
                        }
                    }

                    tempOfficial.setGooglePlusID(googleID);
                    tempOfficial.setFacebookID(facebookId);
                    tempOfficial.setTwitterID(twitterId);
                    tempOfficial.setYoutubeID(youtubeId);

                    officialList.add(tempOfficial);
                }

            }

            mainActivity.handleOfficialReturn(officialList);
            return;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mainActivity.handleOfficialReturn(officialList);
    }



}
