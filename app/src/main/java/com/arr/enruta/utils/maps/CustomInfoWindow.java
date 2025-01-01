package com.arr.enruta.utils.maps;

import android.widget.TextView;
import org.osmdroid.views.MapView;
import com.arr.enruta.R;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class CustomInfoWindow extends InfoWindow {

    public CustomInfoWindow(MapView mapView) {
        super(R.layout.layout_view_title_map, mapView);
    }

    @Override
    public void onOpen(Object item) {
        Marker marker = (Marker) item;
        TextView title = mView.findViewById(R.id.title);
        title.setText(marker.getTitle());
    }

    @Override
    public void onClose() {}
}
