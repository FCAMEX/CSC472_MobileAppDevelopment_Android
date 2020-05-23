package com.fernandoaraujo.multinotes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private static final int CODE_FOR_EDIT_ACTIVITY = 000;

    private ArrayList<Note> noteList = new ArrayList<Note>();
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    int pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler1);
        noteAdapter = new NoteAdapter(noteList, this);

        recyclerView.setAdapter(noteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager( this));

        readJSON();
        updateTitleCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menuInfo:

                intent = new Intent(this, AboutActivity.class);
               // EditText editText = (EditText) findViewById(R.id.editText);
               // String message = editText.getText().toString();
               // intent.putExtra(EXTRA_MESSAGE, none);
                startActivity(intent);
                return true;
            case R.id.menuAdd:

                intent = new Intent(this, EditActivity.class);
                startActivityForResult(intent, CODE_FOR_EDIT_ACTIVITY);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void populateList(){

        for(int i = 0; i < 15; i++){
            Note n = new Note("potato", "potato content", "today");
            noteList.add(n);
        }
    }

    @Override
    public void onClick(View view) {

        pos = recyclerView.getChildLayoutPosition(view);
        Note selection = noteList.get(pos);

       Intent intent = new Intent(this, EditActivity.class);
        // EditText editText = (EditText) findViewById(R.id.editText);
        // String message = editText.getText().toString();
        // intent.putExtra(EXTRA_MESSAGE, none);

        intent.putExtra("NOTE", selection);
        startActivityForResult(intent, CODE_FOR_EDIT_ACTIVITY);
    }

    @Override
    public boolean onLongClick(View view) {

        final int dPos = recyclerView.getChildLayoutPosition(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("Delete Note '" + noteList.get(dPos).getTitle()+ "'?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                noteList.remove(dPos);
                noteAdapter.notifyDataSetChanged();
                updateTitleCount();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });

        AlertDialog ad = builder.create();
        ad.show();

        return true;
    }

    private void updateTitleCount(){
        setTitle(getString(R.string.app_name) + " (" + noteList.size() + ")");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CODE_FOR_EDIT_ACTIVITY){
            if(resultCode == RESULT_OK){
                Log.d(TAG, "onActivityResult: Position = " + pos );

                Note tempNote  = (Note) data.getSerializableExtra("UPDATED NOTE");

                if (data.getStringExtra("STATUS").equals("edit")){
                    noteList.remove(pos);
                    noteList.add(0, tempNote);

                }else{
                    noteList.add(0, tempNote);
                }
                noteAdapter.notifyDataSetChanged();
                updateTitleCount();

            }else if(resultCode == RESULT_CANCELED){

                if(data != null) {
                    if (data.getStringExtra("STATUS").equals("empty")) {
                        Toast.makeText(this, "The untitled note was not saved!", Toast.LENGTH_LONG).show();
                    }
                }
            }

        }else{
            Log.d(TAG, "onActivityResult: ");
        }
        
        
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();

        writeJSON();
    }

    private void writeJSON(){

        Log.d(TAG, "writeJSON: Writing the JSON File");
        JSONArray jsonArray = new JSONArray();

        for(Note o : noteList){
            try{
                JSONObject noteJSON = new JSONObject();
                noteJSON.put("titleText", o.getTitle());
                noteJSON.put("contentText", o.getContent());
                noteJSON.put("dateText", o.getDate());
                jsonArray.put(noteJSON);

            }catch (JSONException e){
                e.printStackTrace();
            }

            String jsonString = jsonArray.toString();

            Log.d(TAG, "writeJSON: " + jsonString);

            try{
                OutputStreamWriter osw = new OutputStreamWriter( openFileOutput("JSON.txt", Context.MODE_PRIVATE));
                osw.write(jsonString);
                osw.close();

            }catch (IOException i){
                Log.d(TAG, "writeJSON: Writing failed!" + i.toString());
            }
        }
    }

    public void readJSON(){

        Log.d(TAG, "readJSON: Reading the JSON File");
        noteList.clear();
        try{
            InputStream inputStream = openFileInput("JSON.txt");

            if(inputStream != null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bf = new BufferedReader(inputStreamReader);

                String comingString = "";
                StringBuilder strBuilder = new StringBuilder();

                while ( (comingString = bf.readLine()) != null ) {
                    strBuilder.append(comingString);
                }

                inputStream.close();

                String jsonString = strBuilder.toString();

                try{
                    JSONArray jsonArray = new JSONArray(jsonString);

                    for(int i  = 0; i < jsonArray.length(); i++){
                        JSONObject jo = jsonArray.getJSONObject(i);
                        String tempTitle = jo.getString("titleText");
                        String tempContent = jo.getString("contentText");
                        String tempDate = jo.getString("dateText");

                        Note n = new Note(tempTitle, tempContent, tempDate);
                        noteList.add(n);
                    }

                }catch(JSONException j){
                    j.printStackTrace();
                }
            }
        }catch( FileNotFoundException f){
            Log.d(TAG, "readJSON: " + f.toString());
        }catch( IOException e){
            Log.d(TAG, "readJSON: " + e.toString());
        }
    }
}
