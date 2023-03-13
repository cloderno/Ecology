package com.example.ecologyproject.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecologyproject.R;
import com.example.ecologyproject.model.Region;
import com.example.ecologyproject.model.Station;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;

import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvStations;
    private StationAdapter adapter;
    private StationViewModel viewModel;
    private RegionViewModel regionViewModel;

    private MapView mapView;
    private MapObjectCollection mapObjects;
    private final String MAPKIT_API_KEY = "23f328ba-362d-4610-9409-17833540a05e";
    private final Point TARGET_LOCATION = new Point(49.956987, 82.592091);

    private Spinner spRegion;

    private ArrayAdapter<String> regionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMapView();
        initSpinner();
        initRecyclerView();
        initViewModels();
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

        regionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<String>());
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
                changeSheetState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
        viewModel.loadStations();
    }

    // Инициализация bottomSheet
    private void changeSheetState() {
        FrameLayout frameLayout = findViewById(R.id.bottomSheet);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
        bottomSheetBehavior.setPeekHeight(150);
        bottomSheetBehavior.setState(bottomSheetBehavior.STATE_EXPANDED);
    }

    private void initRecyclerView() {
        rvStations = findViewById(R.id.rv_stations);
        rvStations.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StationAdapter(new ArrayList<>(), mapView);
        rvStations.setAdapter(adapter);
    }

    private void initViewModels() {
        viewModel = new ViewModelProvider(this).get(StationViewModel.class);
        viewModel.getStations().observe(this, new Observer<List<Station>>() {
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
            List<Station> filteredStations = stations.stream()
                    .filter(s -> Objects.equals(s.getCity(), selectedCity))
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
}