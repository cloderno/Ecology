package com.example.ecologyproject.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecologyproject.R;
import com.example.ecologyproject.model.Indice;
import com.example.ecologyproject.model.Region;
import com.example.ecologyproject.model.Station;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;

import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements StationAdapter.ClickItem {
    private RecyclerView rvStations;
    private StationAdapter adapter;
    private StationViewModel stationViewModel;
    private List<Station> filteredStations;
    private RegionViewModel regionViewModel;

    private MapView mapView;
    private MapObjectCollection mapObjects;
    private final String MAPKIT_API_KEY = "23f328ba-362d-4610-9409-17833540a05e";
    private final Point TARGET_LOCATION = new Point(49.956987, 82.592091);

    private Spinner spRegion;

    private Button btnClose;
    private GridView gridView;

    private ArrayAdapter<String> regionAdapter;

    private BottomSheetBehavior<View> bottomSheetBehavior1;
    private BottomSheetBehavior<View> bottomSheetBehavior2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMapView();
        initSpinner();
        initSheet();
        initRecyclerView();
        initViewModels();

        btnClose = (Button) findViewById(R.id.selected_close_btn);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    // Инициализация карт от яндекса
    private void initMapView() {
        mapView = findViewById(R.id.mapview);

        mapView.getMap().move(new CameraPosition(TARGET_LOCATION, 14.0f, 0.0f, 0.0f), new Animation(Animation.Type.SMOOTH, 2), null);

        mapObjects = mapView.getMap().getMapObjects().addCollection();
    }

    // Инизиализация компонента спиннер, получаем данные из Api, RegionService и заполняем спиннер
    private void initSpinner() {
        spRegion = findViewById(R.id.spinner_regions);

        //change spinner text style
        regionAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, new ArrayList<String>()){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextSize(12);
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextSize(12);
                textView.setPadding(30,30,30,30);
                return view;
            }
        };
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRegion.setAdapter(regionAdapter);

        regionViewModel = new ViewModelProvider(this).get(RegionViewModel.class);
        regionViewModel.getRegions().observe(this, new Observer<List<Region>>() {
            @Override
            public void onChanged(List<Region> regions) {
                updateSpinner(regions);
            }
        });

        regionViewModel.loadRegions();


        spRegion.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadStations();

                bottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                bottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                bottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    private void updateSpinner(List<Region> regions) {
        List<String> regionNames = new ArrayList<>();
        for (Region region : regions) {
            regionNames.add(region.getName());
        }

        regionAdapter.clear();
        regionAdapter.addAll(regionNames);
    }

    private void loadStations() {
        stationViewModel.loadStations();
    }

    // Инициализация bottomSheet
    private void initSheet() {
        FrameLayout frameLayout1 = findViewById(R.id.bottom_sheet_container_1);
        FrameLayout frameLayout2 = findViewById(R.id.bottom_sheet_container_2);

        bottomSheetBehavior1 = BottomSheetBehavior.from(frameLayout1);
        bottomSheetBehavior2 = BottomSheetBehavior.from(frameLayout2);

        bottomSheetBehavior1.setPeekHeight(150);
        bottomSheetBehavior2.setPeekHeight(150);

        bottomSheetBehavior1.setHideable(true);
        bottomSheetBehavior2.setHideable(true);

        bottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void initRecyclerView() {
        rvStations = findViewById(R.id.rv_stations);
        rvStations.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StationAdapter(new ArrayList<>(), mapView, this::ClickStation);
        rvStations.setAdapter(adapter);
    }

    private void initViewModels() {
        stationViewModel = new ViewModelProvider(this).get(StationViewModel.class);
        stationViewModel.getStations().observe(this, new Observer<List<Station>>() {
            @Override
            public void onChanged(List<Station> stations) {
                updateStations(stations);
            }
        });
    }

    // следим за обновлением stations
    private void updateStations(List<Station> stations) {
        String selectedCity = spRegion.getSelectedItem().toString();

        if (selectedCity != null && !selectedCity.isEmpty() && stations != null && !stations.isEmpty()) {
            if (selectedCity.equals("Шемонаихинский")){
                selectedCity = "Шемонаиха";
            }
            else if (selectedCity.equals("Глубоковский")) {
                selectedCity = "п.Глубокое";
            }

            String finalSelectedCity = selectedCity;
            filteredStations = stations.stream()
                    .filter(s -> Objects.equals(s.getCity(), finalSelectedCity))
                    .collect(Collectors.toList());

            adapter.setStations(filteredStations);

            updateMapMarkers(filteredStations);
        }
    }

    // Обновляем маркеры в зависимости от выбранного города
    private void updateMapMarkers(List<Station> stations) {
        mapObjects.clear();

        for (Station station : stations) {
            Point point = new Point(station.getLat(), station.getLon());
            MapObject mapObject = mapObjects.addPlacemark(
                    point,
                    ImageProvider.fromResource(this, R.drawable.marker)
            );

            // Вызвать функцию для показа данных выбранной марки
            mapObject.addTapListener(new MapObjectTapListener() {
                @Override
                public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                    String stationName = (String) mapObject.getUserData();

                    Station filteredStation = stations.stream()
                            .filter(s -> Objects.equals(s.getName(), stationName))
                            .findFirst()
                            .orElse(null);

                    if (filteredStation != null) {
                        ClickStation(filteredStation);
                    }

                    return true;
                }
            });
            mapObject.setUserData(station.getName());
        }
    }

    @Override
    protected void onStop() {
        // Вызов onStop нужно передавать инстансам MapView и MapKit.
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        // Вызов onStart нужно передавать инстансам MapView и MapKit.
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    // Выбор адреса
    @Override
    public void ClickStation(Station station) {
        Point point = new Point(station.getLat(), station.getLon());
        mapView.getMap().move(new CameraPosition(point, 14.0f, 0.0f, 0.0f), new Animation(Animation.Type.SMOOTH, 2), null);

        TextView name = findViewById(R.id.tv_selected_name);
        TextView city = findViewById(R.id.tv_selected_city);
        TextView address = findViewById(R.id.tv_selected_address);
        View colorBlock = findViewById(R.id.selected_color_block);
        TextView time = findViewById(R.id.selected_time);
        gridView = findViewById(R.id.grid_view);

        name.setText(station.getName());
        city.setText(station.getCity());
        address.setText(station.getAddress());
        colorBlock.setBackgroundColor(Color.parseColor(station.getColor()));
        time.setText("Время обновления данных: " + station.getDate());


        List<Indice> indices = station.getIndices();
        GridAdapter gridAdapter = new GridAdapter(MainActivity.this, indices);
        gridView.setAdapter(gridAdapter);
        gridView.setNumColumns(3);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Indice currentIndice = (Indice) parent.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, IndiceActivity.class);
                intent.putExtra("indice", currentIndice);
                intent.putExtra("station", station);
                startActivity(intent);
            }
        });

        bottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}