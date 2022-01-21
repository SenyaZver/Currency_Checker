package com.example.currencychecker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    Context context;
    String[] names;
    String[] values;

    public RecycleViewAdapter(Context context, String[] names, String[] values) {
        this.context = context;
        this.names = names;
        this.values = values;
    }

    public void swapItems(@NonNull String[] newNames, @NonNull String[] newValues){
        this.names = newNames;
        this.values = newValues;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView value;
        ImageView circle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            value = itemView.findViewById(R.id.value);
            circle = itemView.findViewById(R.id.circle);
        }
    }


    @NonNull
    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position) {
        holder.name.setText(names[position]);
        holder.value.setText(values[position]);
        holder.circle.setImageResource(R.drawable.circle2);
    }

    @Override
    public int getItemCount() {
        return this.names.length;
    }



}
