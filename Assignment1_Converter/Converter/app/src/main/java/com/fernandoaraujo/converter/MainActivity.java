package com.fernandoaraujo.converter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView historyView;
    private EditText inputText;
    private TextView resultView;
    private RadioGroup radioButtons;
    private SharedPreferences myPrefs;
    private TextView conversionLabel;
    private TextView conversionResultLabel;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //first stage where we initialize most variables and get saved preferences

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        historyView = findViewById(R.id.text_historyList);
        radioButtons = findViewById(R.id.radioGroup);
        inputText = findViewById(R.id.text_degreeInput);
        resultView = findViewById(R.id.text_degreeOuput);
        conversionLabel = findViewById(R.id.text_conversionInput);
        conversionResultLabel = findViewById(R.id.text_conversionDegrees);

        myPrefs = getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
        int checkedId = myPrefs.getInt("CHECKED_ID", 0);

        if (checkedId == 0) {
            Log.d(TAG, "onCreate: " + "No checkedId saved");
        } else {
            Log.d(TAG, "onCreate: " + "Loaded checkedId successfully");
            radioButtons.check(checkedId);
        }

        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: " + "Bundle is Null");
        } else {
            Log.d(TAG, "onCreate: " + "Bundle is Not Null - Loading saved data for view mode change");
        }
    }

    @Override
    protected void onStart() {
        //this is after the UI actually gets drawn
        super.onStart();

        if(radioButtons.getCheckedRadioButtonId() == R.id.radio_toCE){
            conversionLabel.setText(R.string.f_to_c_label);
            conversionResultLabel.setText(R.string.celsuis_degrees);
        }else{
            conversionLabel.setText(R.string.c_to_f_label);
            conversionResultLabel.setText(R.string.fahrenheit_degrees);
        }
    }


    public void groupClick(View v) {
        //method called when any of the radio buttons is pressed
        SharedPreferences.Editor editor = myPrefs.edit();
        switch (v.getId()) {
            case R.id.radio_toCE:
                Log.d(TAG, "groupClick: " + R.id.radio_toCE);

                conversionLabel.setText(R.string.f_to_c_label);
                conversionResultLabel.setText(R.string.celsuis_degrees);
                editor.putInt("CHECKED_ID", R.id.radio_toCE);
                break;
            case R.id.radio_toFA:
                Log.d(TAG, "groupClick: " + R.id.radio_toFA);

                conversionLabel.setText(R.string.c_to_f_label);
                conversionResultLabel.setText(R.string.fahrenheit_degrees);
                editor.putInt("CHECKED_ID", R.id.radio_toFA);
                break;
            default:
                break;

        }
        editor.apply();
    }


    public void doConversion(View v) {
        //method called when the conversion button is pressed
        String name = ((Button) v).getText().toString();
        boolean toCelsius = true;

        if(radioButtons.getCheckedRadioButtonId() == R.id.radio_toCE){
            toCelsius = true;
        }else{
            toCelsius = false;
        }

        Log.d(TAG, "doConversion: " + name);

        Double inputValue = 0.0;
        Double resultValue = 0.0;
        String inputString = inputText.getText().toString();
        String historyString = historyView.getText().toString();
        String formattedResult = "";

        if (inputString != null) {
            inputValue = Double.parseDouble(inputString);
            inputValue = (double)Math.round(inputValue * 10) / 10;
        }

        if (toCelsius) {
            resultValue = (inputValue - 32.0) / 1.8;
            resultValue = (double)Math.round(resultValue * 10) / 10;
            formattedResult = inputValue + " F ==> " + resultValue + " C";

        } else {
            resultValue = (inputValue * 1.8) + 32;
            resultValue = (double)Math.round(resultValue * 10) / 10;
            formattedResult = inputValue + " C ==> " + resultValue + " F";

        }

        String resultString = resultValue.toString();
        resultView.setText(resultString);
        historyView.setText(formattedResult + "\n" + historyString);

    }

    public void doClearHistory(View v) {
        //method called when the clear button is pressed - clears all the history
        String name = ((Button) v).getText().toString();

        Log.d(TAG, "doClearHistory: " + name);
        TextView historyView = findViewById(R.id.text_historyList);
        historyView.setText("");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //method called before the view mode is changed between landscape and vertical view
        outState.putString("HISTORY", historyView.getText().toString());
        outState.putString("INPUT VALUE", inputText.getText().toString());
        outState.putString("RESULT VALUE", resultView.getText().toString());
        //Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //method called after create, it loads data from the saved bundle
        //Call super first
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey("HISTORY")) {
            historyView.setText(savedInstanceState.getString("HISTORY"));
        }
        if (savedInstanceState.containsKey("INPUT VALUE")) {
            inputText.setText(savedInstanceState.getString("INPUT VALUE"));
        }
        if (savedInstanceState.containsKey("RESULT VALUE")) {
            resultView.setText(savedInstanceState.getString("RESULT VALUE"));
        }

    }
}