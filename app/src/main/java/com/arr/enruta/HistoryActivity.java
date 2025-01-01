package com.arr.enruta;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.arr.enruta.adapter.HistoryAdapter;
import com.arr.enruta.api.model.Dato;
import com.arr.enruta.databinding.ActivityHistoryBinding;
import com.arr.enruta.models.History;
import com.arr.enruta.utils.date.DateFormat;
import com.arr.enruta.utils.maps.CustomInfoWindow;
import com.arr.enruta.utils.maps.MapManager;
import com.arr.enruta.utils.storage.CodeManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;

public class HistoryActivity extends AppCompatActivity {

    private ActivityHistoryBinding binding;
    private List<History> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedP =
                getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        Configuration.getInstance().load(this, sharedP);
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        // interceptar los eventos del collapsing
        binding.appbar.setOnTouchListener(
                (v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_DOWN
                            || event.getAction() == MotionEvent.ACTION_MOVE) {
                        return true;
                    }
                    return false;
                });
        NestedScrollView nestedScrollView = findViewById(R.id.nested_scroll_view);
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) nestedScrollView.getLayoutParams();
        params.topMargin = -70;
        nestedScrollView.setLayoutParams(params);

        binding.map.setOnTouchListener(
                (v, event) -> {
                    // Bloquea el scroll del appbar mientras el usuario interactúa con el mapa
                    binding.nestedScrollView.requestDisallowInterceptTouchEvent(true);
                    return false; // Permite que el mapa procese los gestos normalmente
                });

        binding.collapsingToolbar.setOnTouchListener(
                (v, event) -> {
                    // Bloquea el scroll del appbar mientras el usuario interactúa con el mapa
                    binding.nestedScrollView.requestDisallowInterceptTouchEvent(true);
                    return false; // Permite que el mapa procese los gestos normalmente
                });

        // obtener valores
        String origen = getIntent().getStringExtra("origen");
        String estado = getIntent().getStringExtra("estado");
        String code = getIntent().getStringExtra("codigo");

        // api OSM

        /*
                MapView mapView = binding.map;
                mapView.setTileSource(TileSourceFactory.MAPNIK);
                mapView.setBuiltInZoomControls(false);
                mapView.setMultiTouchControls(true);

                // centrar el mapa en Cuba
                GeoPoint centerOfCuba = new GeoPoint(21.521757, -77.781167);
                mapView.getController().setCenter(centerOfCuba);
                mapView.getController().setZoom(7.6);

                // marcador de inicio
                GeoPoint startPoints = new GeoPoint(23.135305, -82.358963);
                Marker startMarker = new Marker(mapView);
                startMarker.setPosition(startPoints);
                startMarker.setIcon(getDrawable(R.drawable.ic_car_delivery));
                mapView.getOverlays().add(startMarker);

                // marcador de entrega
                GeoPoint endPoints = new GeoPoint(22.405905, -79.964575);
                Marker endMarker = new Marker(mapView);
                endMarker.setPosition(endPoints);
                endMarker.setIcon(getDrawable(R.drawable.ic_car_delivery));
                mapView.getOverlays().add(endMarker);

                // lineas
                Polyline line = new Polyline();
                line.setPoints(new ArrayList<>());
                line.setWidth(5f);
                line.setColor(Color.BLACK);
                line.addPoint(startPoints);
                line.addPoint(endPoints);
                mapView.getOverlayManager().add(line);

        */
        binding.textDestino.setText("Origen: " + convertirTexto(origen));
        binding.textEstado.setText(getStatus(estado));
        binding.iconEstado.setImageResource(getIconStatus(estado));

        HistoryAdapter adapter = new HistoryAdapter(list);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.setNestedScrollingEnabled(false);

        ArrayList<Dato> listaDatos = getIntent().getParcelableArrayListExtra("lista_datos");

        // Crear una nueva lista para almacenar los objetos `History`
        List<History> listaHistory = new ArrayList<>();

        // Recorrer `listaDatos` y convertir cada `Dato` en un `History`
        if (listaDatos != null) {
            for (Dato dato : listaDatos) {
                
            }
        }
        if (listaDatos != null && !listaDatos.isEmpty()) {
            String oficinaOrigen = listaDatos.get(0).oficinaOrigen.replace("CCP", "");
            String oficinaDestino = listaDatos.get(0).oficinaDestino.replace("CCP", "");

            // configurar mapa
         //   MapManager.setupMap(binding.map, oficinaOrigen, oficinaDestino);
        }
    }

    private int getIconStatus(String status) {
        switch (status) {
            case "ENTREGADO":
                return R.drawable.ic_entregado;
            case "RECEPCION":
                return R.drawable.ic_recepcionado;
            case "EN_CAMINO":
                return R.drawable.ic_en_camino;
            case "EN_ENTREGA":
                return R.drawable.ic_en_entrega;
        }
        return R.drawable.ic_package;
    }

    private String getStatus(String status) {
        switch (status) {
            case "ENTREGADO":
                return "Entregado";
            case "RECEPCION":
                return "Recepción";
            case "EN_CAMINO":
                return "En camino";
            case "EN_ENTREGA":
                return "En entrega";
        }
        return "Sin información";
    }

    private String convertirTexto(String texto) {
        String[] palabras = texto.toLowerCase().split(" ");
        StringBuilder resultado = new StringBuilder();

        for (String palabra : palabras) {
            if (palabra.length() > 0) {
                resultado
                        .append(Character.toUpperCase(palabra.charAt(0)))
                        .append(palabra.substring(1))
                        .append(" ");
            }
        }

        return resultado.toString().trim();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.map.onResume();
    }

    public void setupMap(String startLocationName, String endLocationName) {
        MapView mapView = binding.map;
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);

        // Centrar el mapa inicialmente en Cuba
        GeoPoint centerOfCuba = new GeoPoint(21.521757, -77.781167);
        mapView.getController().setCenter(centerOfCuba);
        mapView.getController().setZoom(10);

        // Geocodificar ambos lugares
        geocodePlace(
                startLocationName,
                (startPoint) -> {
                    if (startPoint != null) {
                        // Agregar marcador de inicio
                        Marker startMarker = new Marker(mapView);
                        startMarker.setPosition(startPoint);
                        startMarker.setIcon(getDrawable(R.drawable.ic_car_delivery));
                        mapView.getOverlays().add(startMarker);

                        // Mostrar título permanente
                        addCustomBubble(mapView, startPoint, startLocationName);

                        geocodePlace(
                                endLocationName,
                                (endPoint) -> {
                                    if (endPoint != null) {
                                        // Agregar marcador de entrega
                                        Marker endMarker = new Marker(mapView);
                                        endMarker.setPosition(endPoint);
                                        endMarker.setIcon(getDrawable(R.drawable.ic_car_delivery));
                                        mapView.getOverlays().add(endMarker);

                                        // Mostrar título permanente
                                        addCustomBubble(mapView, endPoint, endLocationName);

                                        // Dibujar línea entre los puntos
                                        Polyline line = new Polyline();
                                        line.setPoints(List.of(startPoint, endPoint));
                                        line.setWidth(5f);
                                        line.setColor(Color.BLACK);
                                        mapView.getOverlayManager().add(line);

                                        // Centrar el mapa entre los dos puntos
                                        //   centerMapBetweenPoints(mapView, startPoint, endPoint);
                                        testManualZoom(mapView, startPoint);
                                        mapView.invalidate();
                                    } else {
                                        runOnUiThread(
                                                () ->
                                                        Toast.makeText(
                                                                        this,
                                                                        "Lugar de entrega no encontrado",
                                                                        Toast.LENGTH_SHORT)
                                                                .show());
                                    }
                                });
                    } else {
                        runOnUiThread(
                                () ->
                                        Toast.makeText(
                                                        this,
                                                        "Lugar de inicio no encontrado",
                                                        Toast.LENGTH_SHORT)
                                                .show());
                    }
                });
    }

    // Método para agregar un título permanente
    private void addCustomBubble(MapView mapView, GeoPoint position, String title) {
        View bubbleView =
                LayoutInflater.from(this).inflate(R.layout.layout_view_title_map, mapView, false);

        // Configurar el contenido del Bubble
        //  ImageView icon = bubbleView.findViewById(R.id.icon);
        TextView titleView = bubbleView.findViewById(R.id.title);

        // icon.setImageResource(iconRes);
        titleView.setText(title);

        Overlay bubbleOverlay =
                new Overlay() {
                    @Override
                    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
                        if (shadow) return;

                        Point screenPoint = new Point();
                        mapView.getProjection().toPixels(position, screenPoint);

                        // Medir y posicionar la vista
                        bubbleView.measure(
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                        bubbleView.layout(
                                0,
                                0,
                                bubbleView.getMeasuredWidth(),
                                bubbleView.getMeasuredHeight());

                        // Dibujar la vista en el Canvas
                        canvas.save();
                        canvas.translate(
                                screenPoint.x
                                        - bubbleView.getMeasuredWidth()
                                                / 2.0f, // Centrar horizontalmente
                                screenPoint.y
                                        - bubbleView.getMeasuredHeight()); // Posicionar encima del
                        // marcador
                        bubbleView.draw(canvas);
                        canvas.restore();
                    }
                };

        mapView.getOverlays().add(bubbleOverlay);
        mapView.invalidate();
    }

    // Método para geocodificar un lugar por nombre
    private void geocodePlace(String placeName, GeocodeCallback callback) {
        AsyncTask.execute(
                () -> {
                    try {
                        // Construir la URL para la API de Nominatim
                        String urlString =
                                "https://nominatim.openstreetmap.org/search?q="
                                        + placeName.replace(" ", "%20")
                                        + ", Cuba&format=json&limit=1";
                        URL url = new URL(urlString);

                        // Realizar la solicitud HTTP GET
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                        // Leer la respuesta
                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        // Parsear la respuesta JSON
                        JSONArray jsonArray = new JSONArray(response.toString());
                        if (jsonArray.length() > 0) {
                            JSONObject location = jsonArray.getJSONObject(0);
                            double latitude = location.getDouble("lat");
                            double longitude = location.getDouble("lon");
                            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                            callback.onGeocodeSuccess(geoPoint);
                        } else {
                            callback.onGeocodeSuccess(null); // Lugar no encontrado
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onGeocodeSuccess(null); // Error en la solicitud
                    }
                });
    }

    // Método para centrar el mapa entre dos puntos
    private void centerMapBetweenPoints(MapView mapView, GeoPoint startPoint, GeoPoint endPoint) {
        // Verificar que el mapa tenga dimensiones válidas
        if (mapView.getHeight() == 0 || mapView.getWidth() == 0) {
            mapView.post(() -> centerMapBetweenPoints(mapView, startPoint, endPoint));
            return;
        }

        // Calcular los extremos del BoundingBox
        double north = Math.max(startPoint.getLatitude(), endPoint.getLatitude());
        double south = Math.min(startPoint.getLatitude(), endPoint.getLatitude());
        double east = Math.max(startPoint.getLongitude(), endPoint.getLongitude());
        double west = Math.min(startPoint.getLongitude(), endPoint.getLongitude());

        // Agregar un padding para que los puntos no queden al borde de la pantalla
        double padding = 0.05;
        north += padding;
        south -= padding;
        east += padding;
        west -= padding;

        // Calcular el BoundingBox
        BoundingBox boundingBox = new BoundingBox(north, east, south, west);

        // Aplicar zoom automáticamente para que ambos puntos queden visibles
        mapView.zoomToBoundingBox(boundingBox, true); // true para animación
        mapView.invalidate();
    }

    private void testManualZoom(MapView mapView, GeoPoint centerPoint) {
        // Centrar el mapa en un punto específico
        mapView.getController().animateTo(centerPoint);

        // Configurar un nivel de zoom manualmente
        mapView.getController().setZoom(100);
        mapView.invalidate();
    }

    // Método para calcular el nivel de zoom adecuado basado en el BoundingBox
    private int calculateZoomLevel(MapView mapView, BoundingBox boundingBox) {
        // Dimensiones del mapa en píxeles
        int mapWidth = mapView.getWidth();
        int mapHeight = mapView.getHeight();

        // Distancias en grados
        double latDifference = boundingBox.getLatNorth() - boundingBox.getLatSouth();
        double lonDifference = boundingBox.getLonEast() - boundingBox.getLonWest();

        // Relación de píxeles por grado al nivel de zoom base (zoom 0)
        double mapWidthInPixelsAtZoom0 = TileSystem.MapSize(0);
        double pixelsPerDegreeAtZoom0 = mapWidthInPixelsAtZoom0 / 360.0;

        // Relación de zoom basada en las dimensiones del mapa
        double zoomLat =
                Math.log(mapHeight / (latDifference * pixelsPerDegreeAtZoom0)) / Math.log(2);
        double zoomLon =
                Math.log(mapWidth / (lonDifference * pixelsPerDegreeAtZoom0)) / Math.log(2);

        // Seleccionar el menor nivel de zoom para que ambos puntos queden visibles
        int zoomLevel = (int) Math.min(zoomLat, zoomLon);

        // Asegurarse de que el nivel de zoom esté en el rango permitido por la biblioteca
        zoomLevel = Math.max(0, (int) Math.min(zoomLevel, mapView.getMaxZoomLevel()));

        return zoomLevel;
    }

    // Interfaz de callback para la geocodificación
    interface GeocodeCallback {
        void onGeocodeSuccess(GeoPoint geoPoint);
    }
}
/*

import android.os.AsyncTask;
import android.widget.Toast;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.List;
import java.util.Locale;

public void setupMap() {
    MapView mapView = binding.map;
    mapView.setTileSource(TileSourceFactory.MAPNIK);
    mapView.setBuiltInZoomControls(false);
    mapView.setMultiTouchControls(true);

    // Centrar el mapa inicialmente en Cuba
    GeoPoint centerOfCuba = new GeoPoint(21.521757, -77.781167);
    mapView.getController().setCenter(centerOfCuba);
    mapView.getController().setZoom(7.6);

    // Nombres de los lugares
    String startLocationName = "La Habana";
    String endLocationName = "Santa Clara";

    // Buscar y configurar puntos de inicio y fin
    geocodePlace(startLocationName, (startPoint) -> {
        if (startPoint != null) {
            Marker startMarker = new Marker(mapView);
            startMarker.setPosition(startPoint);
            startMarker.setIcon(getDrawable(R.drawable.ic_car_delivery));
            mapView.getOverlays().add(startMarker);

            geocodePlace(endLocationName, (endPoint) -> {
                if (endPoint != null) {
                    Marker endMarker = new Marker(mapView);
                    endMarker.setPosition(endPoint);
                    endMarker.setIcon(getDrawable(R.drawable.ic_car_delivery));
                    mapView.getOverlays().add(endMarker);

                    // Dibujar línea entre los puntos
                    Polyline line = new Polyline();
                    line.setPoints(List.of(startPoint, endPoint));
                    line.setWidth(5f);
                    line.setColor(Color.BLACK);
                    mapView.getOverlayManager().add(line);

                    // Centrar el mapa entre los puntos
                    centerMapBetweenPoints(mapView, startPoint, endPoint);
                }
            });
        }
    });
}

private void geocodePlace(String placeName, GeocodeCallback callback) {
    AsyncTask.execute(() -> {
        try {
            GeocoderNominatim geocoder = new GeocoderNominatim(Locale.getDefault());
            geocoder.setService("https://nominatim.openstreetmap.org/");
            List<org.osmdroid.bonuspack.location.Address> results = geocoder.getFromLocationName(placeName + ", Cuba", 1);
            if (results != null && !results.isEmpty()) {
                org.osmdroid.bonuspack.location.Address address = results.get(0);
                GeoPoint location = new GeoPoint(address.getLatitude(), address.getLongitude());
                callback.onGeocodeSuccess(location);
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Lugar no encontrado: " + placeName, Toast.LENGTH_SHORT).show());
                callback.onGeocodeSuccess(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "Error al buscar el lugar: " + placeName, Toast.LENGTH_SHORT).show());
            callback.onGeocodeSuccess(null);
        }
    });
}

private void centerMapBetweenPoints(MapView mapView, GeoPoint startPoint, GeoPoint endPoint) {
    double north = Math.max(startPoint.getLatitude(), endPoint.getLatitude());
    double south = Math.min(startPoint.getLatitude(), endPoint.getLatitude());
    double east = Math.max(startPoint.getLongitude(), endPoint.getLongitude());
    double west = Math.min(startPoint.getLongitude(), endPoint.getLongitude());

    double padding = 0.05;
    BoundingBox boundingBox = new BoundingBox(
            north + padding,
            east + padding,
            south - padding,
            west - padding
    );

    mapView.zoomToBoundingBox(boundingBox, true);
    mapView.invalidate();
}

interface GeocodeCallback {
    void onGeocodeSuccess(GeoPoint geoPoint);
}


*/
