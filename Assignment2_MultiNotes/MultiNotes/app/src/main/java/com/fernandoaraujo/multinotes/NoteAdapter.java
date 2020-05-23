package com.fernandoaraujo.multinotes;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private static final String TAG = "NoteAdapter";
    private ArrayList<Note> myList;
    private MainActivity mainActivity;

    NoteAdapter(ArrayList<Note> list, MainActivity activity){
        myList = list;
        mainActivity = activity;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Creating New items");
        View noteView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);

       noteView.setOnClickListener(mainActivity);
       noteView.setOnLongClickListener(mainActivity);

        return new NoteViewHolder(noteView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {

        Note n = myList.get(position);

        String tempTitle = n.getTitle();
        String tempContent = n.getContent();

        if(tempTitle.length() > 80){
            tempTitle = tempTitle.substring(0,81) + "...";
        }
        if(tempContent.length() > 80){
            tempContent = tempContent.substring(0,81) + "...";
        }

        holder.noteTitle.setText(tempTitle);
        holder.noteContent.setText(tempContent);
        holder.noteDate.setText(n.getDate());
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }
}
