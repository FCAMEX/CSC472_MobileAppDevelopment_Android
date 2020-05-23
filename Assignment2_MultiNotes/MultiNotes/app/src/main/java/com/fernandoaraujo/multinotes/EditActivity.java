package com.fernandoaraujo.multinotes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {

    public static SimpleDateFormat formatter =  new SimpleDateFormat("EEE MMM d, hh:mm aaa", Locale.US);

    EditText noteTitle;
    EditText noteContent;
    Note tempNote;
    Boolean isNew = false;
    Boolean isEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        noteTitle = findViewById(R.id.editTitle);
        noteContent = findViewById(R.id.editBody);

        Intent intent = getIntent();
        if(intent.hasExtra("NOTE")){
            tempNote = (Note) intent.getSerializableExtra("NOTE");
            noteTitle.setText(tempNote.getTitle());
            noteContent.setText(tempNote.getContent());
        }else{
            tempNote = new Note("","","");
            isNew = true;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuSave:

                updateNote();

                if(isEdited || isNew) {

                    Intent data = new Intent();

                    if(tempNote.getTitle().trim().isEmpty()) {

                        data.putExtra("STATUS", "empty");
                        setResult(RESULT_CANCELED, data );
                    }else {
                        String status;

                        if (isNew) {
                            status = "add";
                        } else {
                            status = "edit";
                        }


                        data.putExtra("UPDATED NOTE", tempNote);
                        data.putExtra("STATUS", status);
                        setResult(RESULT_OK, data);
                    }
                }else{

                }

                finish();
                return true;
            default:
               return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("Your note is not saved!");
        builder.setMessage("Save note '" + noteTitle.getText() +"'?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                updateNote();

                if(isEdited || isNew) {

                    Intent data = new Intent();

                    if(tempNote.getTitle().trim().isEmpty()) {
                        data.putExtra("STATUS", "empty");
                        setResult(RESULT_CANCELED, data );
                    }else {

                        data.putExtra("UPDATED NOTE", tempNote);

                        String status;

                        if (isNew) {
                            status = "add";
                        } else {
                            status = "edit";
                        }
                        data.putExtra("STATUS", status);
                        setResult(RESULT_OK, data);
                    }
                }
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog ad = builder.create();
        ad.show();

      // super.onBackPressed(); //always end with this
    }

    private void updateNote(){
        String inputTitle = noteTitle.getText().toString();
        String inputBody = noteContent.getText().toString();

        if(inputTitle.equals(tempNote.getTitle()) && inputBody.equals(tempNote.getContent())){
            isEdited = false;
        } else{
            isEdited = true;
            tempNote.setTitle(noteTitle.getText().toString());
            tempNote.setContent(noteContent.getText().toString());
            tempNote.setDate(formatter.format( new Date()));
        }
    }
}
