package com.fernandoaraujo.stockwatch;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class StockDownloader_AsyncTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "StockDownloader_AsyncTa";
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    private static final String apiBase = "https://cloud.iexapis.com/stable/stock/";
    private static final String quoteBody = "quote?token=";
    private JSONObject result = new JSONObject();
    private String mode = "";


    StockDownloader_AsyncTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    protected String doInBackground(String... dataIn) {

        String apiToken = dataIn[0];
        String stockSymbol = dataIn[1];
        mode = dataIn[2];
        Uri.Builder buildURL = Uri.parse(apiBase).buildUpon();
        buildURL.appendEncodedPath(stockSymbol);
        buildURL.appendEncodedPath(quoteBody + apiToken);
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

        Stock parsedStock = null;

        try {

                String symbol = result.getString("symbol").trim();
                String name = result.getString("companyName").trim();
                String price = result.getString("latestPrice").trim();
                String change = result.getString("change").trim();
                String changePercent = result.getString("changePercent").trim();

                parsedStock = new Stock(symbol, name, price, change, changePercent);

            mainActivity.handleStockReturn(mode, parsedStock);
            return;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mainActivity.handleStockReturn(mode, parsedStock);
    }



}
