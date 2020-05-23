package com.fernandoaraujo.multinotes;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    TextView noteTitle;
    TextView noteContent;
    TextView noteDate;

    NoteViewHolder(View view){
        super(view);

        noteTitle = view.findViewById(R.id.noteTitle);
        noteContent = view.findViewById(R.id.noteContent);
        noteDate = view.findViewById(R.id.noteDate);
    }
}
