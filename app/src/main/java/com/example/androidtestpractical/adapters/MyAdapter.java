package com.example.androidtestpractical.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidtestpractical.MyViewCheckChangeListener;
import com.example.androidtestpractical.R;
import com.example.androidtestpractical.model.Item;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private MyViewCheckChangeListener myViewCheckChangeListener;

    private ArrayList<Item> myItems;

    public void setMyViewCheckChangeListener(MyViewCheckChangeListener myViewCheckChangeListener) {
        this.myViewCheckChangeListener = myViewCheckChangeListener;
    }

    public MyAdapter(ArrayList<Item> myItems) {
        this.myItems = myItems;
    }

    public void replaceAll(ArrayList<Item> myNewList) {
        myItems.clear();
        myItems.addAll(myNewList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_list_of_strings, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Item item = myItems.get(position);
        holder.myValue.setText(item.getName());
        holder.cbItem.setOnCheckedChangeListener(null);
        holder.cbItem.setChecked(item.isChecked());
        holder.cbItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                item.setChecked(b);
                myViewCheckChangeListener.checkChanged(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myItems.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.myValue)
        AppCompatTextView myValue;

        @BindView(R.id.cbItem)
        AppCompatCheckBox cbItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
