package com.arr.enruta;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arr.enruta.adapter.HistoryAdapter;
import com.arr.enruta.api.model.Dato;
import com.arr.enruta.databinding.ActivityTrackOrderBinding;
import com.arr.enruta.models.History;
import com.arr.enruta.utils.Theme;
import com.arr.enruta.utils.date.DateFormat;
import com.arr.enruta.utils.maps.MapManager;

import org.osmdroid.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class TrackActivity extends AppCompatActivity {

    private ActivityTrackOrderBinding binding;
    private List<History> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        Configuration.getInstance().load(this, sp);
        super.onCreate(savedInstanceState);
        binding = ActivityTrackOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        supportActionBar();
        getBackPressed();

        String origen = getIntent().getStringExtra("origen");
        String destino = getIntent().getStringExtra("destino");
        String estado = getIntent().getStringExtra("estado");
        String code = getIntent().getStringExtra("codigo");

        binding.textOrigen.setText(!origen.trim().isEmpty() ? "Origen: " + setMinText(origen) : "");
        binding.textDestino.setText(
                !destino.trim().isEmpty() ? "Destino: " + setMinText(destino) : "");
        binding.textCodigo.setText(!code.trim().isEmpty() ? code : "");
        binding.textEstado.setText(!estado.trim().isEmpty() ? getStatus(estado) : "");
        binding.iconEstado.setImageResource(getIconStatus(estado));

        // configuracion del MapView
        boolean isDarkTheme = Theme.isDarkModeEnabled(getResources());

        ArrayList<Dato> datos = getIntent().getParcelableArrayListExtra("lista_datos", Dato.class);
        if (datos != null && !datos.isEmpty()) {
            String startLocation = datos.get(0).oficinaOrigen.replace("CCP", "");
            String endLocation = datos.get(0).oficinaDestino.replace("CCP", "");

            if (startLocation.equals("Oficina Cambio Internacional")
                    || startLocation.equals("Paquetería Internacional")) {
                startLocation = "La Habana";
            }
            if (endLocation.trim().equalsIgnoreCase("Villa Clara")) {
                endLocation = "Santa Clara";
            }
            Toast.makeText(this, endLocation, Toast.LENGTH_LONG).show();
            MapManager.setupMap(this, binding.mapView, startLocation, endLocation, isDarkTheme);
        } else {
            MapManager.setupMap(this, binding.mapView, null, null, isDarkTheme);
        }

        HistoryAdapter adapter = new HistoryAdapter(list);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.setNestedScrollingEnabled(false);
        getListHistory(datos);

        // copiar código al portapapeles
        binding.copyCode.setOnClickListener(
                v -> {
                    String mCodigo = binding.textCodigo.getText().toString();
                    copyToClipboard(this, mCodigo);
                });
    }

    private void getListHistory(ArrayList<Dato> datos) {
        if (datos != null) {
            for (Dato d : datos) {
                String fecha = DateFormat.getFechaFormat(d.fecha);
                String hora = DateFormat.getHoraFormat(d.fecha);
                String from = !d.oficinaOrigen.trim().isEmpty() ? "De: " + d.oficinaOrigen : "";
                String to = !d.oficinaDestino.trim().isEmpty() ? "Hacia: " + d.oficinaDestino : "";
                list.add(new History(fecha, from, to, d.estado, hora));
            }
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

    private String setMinText(String texto) {
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

    private void getBackPressed() {
        getOnBackPressedDispatcher()
                .addCallback(
                        this,
                        new OnBackPressedCallback(true) {
                            @Override
                            public void handleOnBackPressed() {
                                navigateToMain();
                            }
                        });
    }

    private void supportActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);

        // control scroll collapsing
        View.OnTouchListener touchListener =
                (view, event) -> {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_MOVE:
                            // Deshabilitar el intercepto de eventos mientras se interactúa con el
                            // view
                            binding.nestedScrollView.requestDisallowInterceptTouchEvent(true);
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            // Permitir que el NestedScrollView vuelva a interceptar eventos después
                            // de soltar
                            binding.nestedScrollView.requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                    return false;
                };

        // Asignar el mismo listener a todas las vistas
        binding.mapView.setOnTouchListener(touchListener);
        binding.contentMap.setOnTouchListener(touchListener);
        binding.collapsing.setOnTouchListener(touchListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navigateToMain();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);

        // Mostrar Toast solo en versiones anteriores a Android 13
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Toast.makeText(context, "Código copiado al portapapeles", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.mapView.onDetach();
    }
}
