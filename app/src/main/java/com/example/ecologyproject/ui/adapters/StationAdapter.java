package com.example.ecologyproject.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecologyproject.R;
import com.example.ecologyproject.model.Station;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.mapview.MapView;

import java.util.List;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder> {
    private final MapObjectCollection mapObjects;
    private List<Station> stations;
    private ClickItem clickItem;
    private final MapView mapView;

    public StationAdapter(List<Station> stations, MapView mapView) {
        this.stations = stations;
        this.mapView = mapView;
        this.mapObjects = mapView.getMap().getMapObjects().addCollection();
    }

    public StationAdapter(List<Station> stations, MapView mapView, ClickItem clickItem) {
        this.stations = stations;
        this.mapView = mapView;
        this.mapObjects = mapView.getMap().getMapObjects().addCollection();
        this.clickItem = clickItem;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_station, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Station station = stations.get(position);

        holder.tvName.setText(station.getName());
        holder.tvCity.setText(station.getCity());
        holder.tvAddress.setText(station.getAddress());
        holder.colorBlock.setBackgroundColor(Color.parseColor(station.getColor()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickItem.ClickStation(station);
            }
        });
    }

    public interface ClickItem {
        void ClickStation(Station station);
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvCity;

        TextView tvAddress;
        View colorBlock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCity = itemView.findViewById(R.id.tv_city);
            tvAddress = itemView.findViewById(R.id.tv_address);
            colorBlock = itemView.findViewById(R.id.colorBlock);
        }
    }
}
