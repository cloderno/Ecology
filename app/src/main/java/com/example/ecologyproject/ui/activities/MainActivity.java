package com.example.ecologyproject.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ecologyproject.R;
import com.example.ecologyproject.model.Indice;
import com.example.ecologyproject.model.Region;
import com.example.ecologyproject.model.Station;
import com.example.ecologyproject.ui.adapters.GridAdapter;
import com.example.ecologyproject.ui.adapters.StationAdapter;
import com.example.ecologyproject.ui.viewmodels.RegionViewModel;
import com.example.ecologyproject.ui.viewmodels.StationViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;

import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements StationAdapter.ClickItem {
    private final String MAPKIT_API_KEY = "23f328ba-362d-4610-9409-17833540a05e";
    private final Point TARGET_LOCATION = new Point(49.956987, 82.592091);

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button buttonGetLocation;
    private RecyclerView rvStations;
    private StationAdapter adapter;
    private StationViewModel stationViewModel;
    private List<Station> filteredStations;
    private RegionViewModel regionViewModel;
    private MapView mapView;
    private MapObjectCollection mapObjects;
    private Spinner spRegion;
    private Button btnClose;
    private GridView gridView;
    private ArrayAdapter<String> regionAdapter;
    private BottomSheetBehavior<View> bottomSheetBehavior1, bottomSheetBehavior2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация компонентов
        initUI();

        btnClose = findViewById(R.id.selected_close_btn);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    private void initUI() {
        // Задаем цвет шапки
        setBarColor();

        // Инициализация карт от яндекса
        initMapView();

        // Инизиализация компонента спиннер, получаем данные из Api, RegionService
        initSpinner();

        // Инициализация bottomSheet
        initSheet();

        // Инициализация RecyclerView
        initRecyclerView();

        // Инициализация StationViewModels
        initViewModels();
    }

    private void setBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue_header));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_header)));
    }

    private void initMapView() {
        mapView = findViewById(R.id.mapview);

        mapView.getMap().move(new CameraPosition(TARGET_LOCATION, 14.0f, 0.0f, 0.0f), new Animation(Animation.Type.SMOOTH, 0.2F), null);

        mapObjects = mapView.getMap().getMapObjects().addCollection();
    }


    private void initSpinner() {
        spRegion = findViewById(R.id.spinner_regions);
        regionAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, new ArrayList<>());
        regionAdapter.setDropDownViewResource(R.layout.dropdown_layout);
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
                stationViewModel.loadStations();

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

    // Получаем данные из API и вставляем их в spinner
    private void updateSpinner(List<Region> regions) {
        List<String> regionNames = new ArrayList<>();

        for (Region region : regions) {
            regionNames.add(region.getName());
        }

        regionAdapter.clear();
        regionAdapter.addAll(regionNames);
    }

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

        // отключение скрола в bottomsheet
        RecyclerView rv = findViewById(R.id.rv_stations);
        GridView gv = findViewById(R.id.grid_view);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    bottomSheetBehavior1.setDraggable(false);
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    bottomSheetBehavior1.setDraggable(true);
                }
            }
        });

        gv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bottomSheetBehavior2.setDraggable(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        bottomSheetBehavior2.setDraggable(true);
                        break;
                }
                return false;
            }
        });

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

    // Меняем выдаваемые станции при смене города через Spinner
    // API выдаёт ответ в видео Шемонаихинский и Глубоковский, в другом url где данные о станциях города, мы имеем города Шемонаиха и Глубоковский
    private void updateStations(List<Station> stations) {
        String selectedCity = spRegion.getSelectedItem() != null ? spRegion.getSelectedItem().toString() : "Усть-Каменогорск";

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

        List<PointF> pointList = new ArrayList<>();
        pointList.add(new PointF(0.4f, 0.4f));
        pointList.add(new PointF(1.5f, 1.5f));

        for (Station station : stations) {
            Point point = new Point(station.getLat(), station.getLon());
            MapObject mapObject = mapObjects.addPlacemark(point);


            // Меняем цвет марки на карте в зависимости от цвета
            // yellow -#FFA800 green-45B500 orange-FF5C00 red-FF0909
            Drawable iconDrawable = null;
            switch (station.getColor()){
                case "#45B500":
                    iconDrawable = getResources().getDrawable(R.drawable.placemark_green);
                    break;
                case "#FFA800":
                    iconDrawable = getResources().getDrawable(R.drawable.placemark_yellow);
                    break;
                case "#FF5C00":
                    iconDrawable = getResources().getDrawable(R.drawable.placemark_orange);
                    break;
                case "#FF0909":
                    iconDrawable = getResources().getDrawable(R.drawable.placemark_red);
                    break;
                default:
                    iconDrawable = getResources().getDrawable(R.drawable.baseline_place_24);
                    break;
            }

            Bitmap iconBitmap = getBitmapFromDrawable(iconDrawable);
            ((PlacemarkMapObject) mapObject).setIcon(ImageProvider.fromBitmap(iconBitmap));
            ((PlacemarkMapObject) mapObject).setScaleFunction(pointList);
            mapObject.setZIndex(1.4f);
            mapObject.setUserData(station.getName());

            // Вызвать функцию для показа данных при клике на марку
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

                    mapObject.removeTapListener(this);
                    mapObject.addTapListener(this);

                    return true;
                }
            });
        }
    }

    // Получаем битмап чтобы можно было менять цвет
    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }

        return bitmap;
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

    // Функция которая открывает новое активити и показывает данные выбранной категории
    @Override
    public void ClickStation(Station station) {
        Point point = new Point(station.getLat(), station.getLon());
        mapView.getMap().move(new CameraPosition(point, 14.0f, 0.0f, 0.0f), new Animation(Animation.Type.SMOOTH, 0.2F), null);

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