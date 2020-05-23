package com.fernandoaraujo.knowyourgovernment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder> {

    private static final String TAG = "StockAdapter";
    private List<Official> officialList;
    private MainActivity mainActivity;

    OfficialAdapter(List<Official> list, MainActivity m){
        this.officialList = list;
        this.mainActivity = m;
    }

    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG, "onCreateViewHolder: Creating View Holder and setting listeners");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.official_item, parent, false);
        v.setOnClickListener(mainActivity);

        return new OfficialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position) {
        Official s = officialList.get(position);

        holder.name.setText(s.getName() + " (" + s.getParty() + ")");
        holder.office.setText(s.getOffice());
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
