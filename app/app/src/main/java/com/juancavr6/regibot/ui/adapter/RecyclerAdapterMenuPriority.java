package com.juancavr6.regibot.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.juancavr6.regibot.R;
import com.juancavr6.regibot.controller.SettingsController;
import com.juancavr6.regibot.ui.UIActionElement;

import java.util.Collections;
import java.util.List;

public class RecyclerAdapterMenuPriority extends RecyclerView.Adapter<RecyclerAdapterMenuPriority.MyViewHolder> {

    private final List<UIActionElement> elements;
    private final Context context;
    public RecyclerAdapterMenuPriority(Context context) {
        elements = SettingsController.getInstance(context).getUIPriorityList();
        this.context = context;
        Log.v("Test112", "Adapter with " + elements.size() + " elements.");

    }

    @NonNull
    @Override
    public RecyclerAdapterMenuPriority.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_menu_priority,parent,false);
        RecyclerAdapterMenuPriority.MyViewHolder myViewHolder = new RecyclerAdapterMenuPriority.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterMenuPriority.MyViewHolder holder, int position) {

        // Get the element at the current position
        UIActionElement element = elements.get(holder.getAdapterPosition());
        // Set the data for the current element
        holder.image_element.setImageResource(context.getResources().getIdentifier(
                element.getIconDrawableName(), "drawable", context.getPackageName()));
        holder.text_nombre.setText(context.getString(element.getNameSource()));
        holder.switch_element.setChecked(element.isEnabled());


        // Set the arrow buttons functionality
        holder.image_arrowDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = elements.indexOf(element);
                if (pos < elements.size() - 1) {
                    Collections.swap(elements, pos, pos + 1);
                    notifyItemMoved(pos, pos + 1);
                    SettingsController.getInstance(context).setUIPriorityList(elements);

                }
            }
        });
        holder.image_arrowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = elements.indexOf(element);
                if (pos > 0) {
                    Collections.swap(elements, pos, pos - 1);
                    notifyItemMoved(pos, pos - 1);
                    SettingsController.getInstance(context).setUIPriorityList(elements);
                }
            }
        });
        // Set the switch functionality
        holder.switch_element.setOnCheckedChangeListener((buttonView, isChecked) -> {
            element.setEnabled(isChecked);
            SettingsController.getInstance(context).setUIPriorityList(elements);
        });

    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardView_main;
        ImageView image_element,image_arrowUp,image_arrowDown;
        TextView text_nombre;
        SwitchCompat switch_element;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView_main = itemView.findViewById(R.id.cardView_main);
            image_element = itemView.findViewById(R.id.imageElement);
            text_nombre = itemView.findViewById(R.id.textElement);
            image_arrowUp = itemView.findViewById(R.id.imageArrowUp);
            image_arrowDown = itemView.findViewById(R.id.imageArrowDown);
            switch_element = itemView.findViewById(R.id.switchElement);


        }
    }
}
