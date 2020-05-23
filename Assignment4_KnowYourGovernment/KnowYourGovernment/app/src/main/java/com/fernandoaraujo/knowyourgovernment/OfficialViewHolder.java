package com.fernandoaraujo.knowyourgovernment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class OfficialViewHolder extends RecyclerView.ViewHolder {

    TextView office;
    TextView name;

    OfficialViewHolder(View view){
        super(view);
        office = view.findViewById(R.id.officialOffice);
        name = view.findViewById(R.id.infoName);

    }

}
