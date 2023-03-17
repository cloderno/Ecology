package com.example.ecologyproject.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ecologyproject.R;
import com.example.ecologyproject.model.Indice;
import com.example.ecologyproject.model.Station;

import java.util.List;

public class GridAdapter extends ArrayAdapter<Indice> {
    private final List<Indice> indices;

    public GridAdapter(Context context, List<Indice> indices) {
        super(context, 0, indices);
        this.indices = indices;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflate the grid_item layout for each item
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);
        }

        // Get the TextViews in the layout and set their values
        TextView name = convertView.findViewById(R.id.grid_name);
        TextView code = convertView.findViewById(R.id.grid_code);
        TextView value = convertView.findViewById(R.id.grid_value);
        LinearLayout gridColor = convertView.findViewById(R.id.grid_item_color);

        Indice indice = indices.get(position);

        name.setText(indice.getElement());
        code.setText(Html.fromHtml(indice.getCode()));
        value.setText(indice.getValue().toString());
        gridColor.setBackgroundColor(Color.parseColor(indice.getColor()));


        return convertView;
    }
}

