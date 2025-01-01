package com.arr.enruta.utils.maps;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONObject;
import com.arr.enruta.R;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;

import java.net.HttpURLConnection;
import java.net.URL;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

public class MapManager {

    @SuppressWarnings("deprecation")
    public static void setupMap(
            Context context,
            MapView mapView,
            String startLocation,
            String endLocation,
            boolean isDarkMode) {
        mapView.setTileSource(getTileSource(isDarkMode));
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);

        // centrar la vista en Cuba
        GeoPoint centerOfCuba = new GeoPoint(22.274053, -79.988086);
        mapView.getController().setCenter(centerOfCuba);
        mapView.getController().setZoom(7.5);

        // gelocalización de inicio
        if (startLocation != null && endLocation != null) {
            geocodePlace(startLocation)
                    .filter(startPoint -> startPoint != null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            startPoint -> {
                                if (startPoint != null) {
                                    if (mapView != null) {
                                        // agregar el marcador del punto de inicio.
                                        addCustomMarker(
                                                context, mapView, startPoint, startLocation);

                                        // geolocalización de destino
                                        geocodePlace(endLocation)
                                                .filter(endPoint -> endPoint != null)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(
                                                        endPoint -> {
                                                            // agregar el marcador del punto de
                                                            // destino.
                                                            addCustomMarker(
                                                                    context,
                                                                    mapView,
                                                                    endPoint,
                                                                    endLocation);

                                                            addLineBetweenPoints(
                                                                    mapView,
                                                                    startPoint,
                                                                    endPoint,
                                                                    isDarkMode);

                                                            addCenterToMarket(
                                                                    mapView, startPoint, endPoint);
                                                        },
                                                        throwable -> {
                                                            Log.e(
                                                                    "EndError: ",
                                                                    throwable.getMessage());
                                                            throwable.printStackTrace();
                                                        });
                                    }
                                }
                            },
                            throwable -> {
                                Log.e("StartError: ", throwable.getMessage());
                                throwable.printStackTrace();
                            });
        }
    }

    private static Observable<GeoPoint> geocodePlace(String placeName) {
        return Observable.fromCallable(
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
                            return new GeoPoint(latitude, longitude);
                        } else {
                            return null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                });
    }

    private static void addCustomMarker(
            Context context, MapView mapView, GeoPoint geoPoint, String title) {
        View view =
                LayoutInflater.from(context)
                        .inflate(R.layout.layout_view_title_map, mapView, false);
        TextView titleView = view.findViewById(R.id.title);
        titleView.setText(title);

        mapView.getOverlays().add(new BubbleOverlay(geoPoint, view));
        mapView.invalidate();
    }

    private static class BubbleOverlay extends Overlay {
        private final GeoPoint position;
        private final View view;

        public BubbleOverlay(GeoPoint position, View bubbleView) {
            this.position = position;
            this.view = bubbleView;
        }

        @Override
        public void draw(Canvas canvas, MapView mapView, boolean shadow) {
            if (shadow) return;

            Point point = new Point();
            mapView.getProjection().toPixels(position, point);

            // Medir y posicionar la vista
            view.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

            // Dibujar la vista en el Canvas
            canvas.save();
            canvas.translate(
                    point.x - view.getMeasuredWidth() / 2.0f, point.y - view.getMeasuredHeight());
            view.draw(canvas);
            canvas.restore();
        }
    }

    private static void addLineBetweenPoints(
            MapView mapView, GeoPoint startPoint, GeoPoint endPoint, boolean isDarkMode) {
        mapView.getOverlays().removeIf(overlay -> overlay instanceof CircleOverlay);
        int color = Color.BLACK;
        if (isDarkMode) {
            color = Color.WHITE;
        }

        // Ajustar el número de puntos según el nivel de zoom
        int steps = Math.max(5, 20); // Más puntos con mayor zoom

        // Calcular los puntos intermedios entre startPoint y endPoint
        double latDiff = (endPoint.getLatitude() - startPoint.getLatitude()) / steps;
        double lonDiff = (endPoint.getLongitude() - startPoint.getLongitude()) / steps;

        for (int i = 0; i <= steps; i++) {
            double lat = startPoint.getLatitude() + (latDiff * i);
            double lon = startPoint.getLongitude() + (lonDiff * i);

            GeoPoint point = new GeoPoint(lat, lon);
            mapView.getOverlays().add(new CircleOverlay(point, 10, color));
        }

        // Redibujar el mapa
        mapView.invalidate();
    }

    private static class CircleOverlay extends Overlay {
        private final GeoPoint position;
        private final int radius;
        private final int color;

        public CircleOverlay(GeoPoint position, int radius, int color) {
            this.position = position;
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void draw(Canvas canvas, MapView mapView, boolean shadow) {
            if (shadow) return;

            Point screenPoint = new Point();
            mapView.getProjection().toPixels(position, screenPoint);

            Paint paint = new Paint();
            paint.setColor(color);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);

            canvas.drawCircle(screenPoint.x, screenPoint.y, radius, paint);
        }
    }

    private static void addCenterToMarket(MapView mapView, GeoPoint from, GeoPoint to) {
        double north = Math.max(from.getLatitude(), to.getLatitude());
        double south = Math.min(from.getLatitude(), to.getLatitude());
        double east = Math.max(from.getLongitude(), to.getLongitude());
        double west = Math.min(from.getLongitude(), to.getLongitude());

        BoundingBox box = new BoundingBox(north + 0.4, east + 0.4, south - 0.4, west - 0.4);
        mapView.zoomToBoundingBox(box, true);
        mapView.invalidate();
    }

    private static OnlineTileSourceBase getTileSource(boolean isDarkMode) {
        return new OnlineTileSourceBase(
                isDarkMode ? "DarkMode" : "LightMode",
                0,
                19,
                256,
                "",
                new String[] {
                    isDarkMode
                            ? "https://a.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}.png"
                            : "https://a.basemaps.cartocdn.com/light_all/{z}/{x}/{y}.png"
                }) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return getBaseUrl()
                        .replace("{z}", String.valueOf(MapTileIndex.getZoom(pMapTileIndex)))
                        .replace("{x}", String.valueOf(MapTileIndex.getX(pMapTileIndex)))
                        .replace("{y}", String.valueOf(MapTileIndex.getY(pMapTileIndex)));
            }
        };
    }
}
