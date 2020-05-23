package com.fernandoaraujo.stockwatch;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class StockViewHolder extends RecyclerView.ViewHolder {

    public TextView symbol;
    TextView price;
    TextView priceChange;
    TextView name;

    StockViewHolder(View view){
        super(view);
        symbol = view.findViewById(R.id.symbol);
        price = view.findViewById(R.id.price);
        priceChange = view.findViewById(R.id.priceChange);
        name = view.findViewById(R.id.name);

    }

}
