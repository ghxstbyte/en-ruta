package com.arr.enruta;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.arr.enruta.adapter.MainAdapter;
import com.arr.enruta.api.EnviosClient;
import com.arr.enruta.api.EnviosRetrofit;
import com.arr.enruta.api.model.Dato;
import com.arr.enruta.api.model.EnviosResponse;
import com.arr.enruta.databinding.ActivityMainBinding;
import com.arr.enruta.models.Codes;
import com.arr.enruta.utils.dialogs.CheckCodeDialog;
import com.arr.enruta.utils.dialogs.LoadingDialog;
import com.arr.enruta.utils.storage.Code;
import com.arr.enruta.utils.storage.CodeManager;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private EnviosClient api;

    private ArrayList<Code> list = new ArrayList<>();
    private MainAdapter adapter;

    private String mCode;
    private String mAnno;

    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");

        api = EnviosRetrofit.auth();
        dialog = new LoadingDialog(this);

        //  binding.content.btnDone.setOnClickListener(this::setRastrear);

        binding.btnDone.setOnClickListener(this::setCheckCode);

        // adapter
        adapter = new MainAdapter(list, p -> setOnClickItem(p));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        refreshRecyclerView();

        if (!list.isEmpty()) {
            getSupportActionBar().setTitle("Próximas entregas");
        }
    }

    public void refreshRecyclerView() {
        list.clear();
        list.addAll(CodeManager.loadCodes(this));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    private void setCheckCode(View view) {
        new CheckCodeDialog(this).show();
    }

    private void setOnClickItem(int position) {
        String codigo = CodeManager.getInfoJson(this, position, "code");
        String anno = CodeManager.getInfoJson(this, position, "anno");
        if (codigo != null && anno != null) {
            rastreo(codigo, anno);
            dialog.show();
            mCode = codigo;
            mAnno = anno;
        }
    }

    private void rastreo(String codigo, String anno) {
        Single<EnviosResponse> envios = api.getEnvios(codigo, anno, "Persona");
        envios.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handlerSuccess, this::handlerError);
    }

    private void handlerSuccess(EnviosResponse response) {
        if (response.error.equals("")) {
            dialog.dismiss();
            addOrUpdateCode(mCode, mAnno, response.estado);
            // Convierte `List<Dato>` a `ArrayList<Dato>`
            ArrayList<Dato> listaDatos = new ArrayList<>(response.datos);

            // Crea el Intent y añade la lista
            Intent intent = new Intent(this, TrackActivity.class);
            intent.putExtra("origen", response.origen);
            intent.putExtra("destino", response.destino);
            intent.putExtra("estado", response.estado);
            intent.putExtra("codigo", mCode);
            intent.putParcelableArrayListExtra("lista_datos", listaDatos);

            // Inicia el Activity
            startActivity(intent);
        } else {
            Toast.makeText(this, "" + response.error, Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    private void handlerError(Throwable error) {
        dialog.dismiss();
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
    }

    public void addOrUpdateCode(String newCode, String anno, String estado) {
        ArrayList<Code> codes = CodeManager.loadCodes(this);
        boolean found = false;

        for (Code code : codes) {
            if (code.code.equals(newCode)) {
                code.estado = estado; // Actualizar el estado si el código ya existe
                found = true;
                break;
            }
        }

        if (!found) {
            codes.add(new Code(newCode, anno, estado)); // Agregar nuevo código si no existe
        }

        CodeManager.saveCodes(codes, this); // Guardar la lista actualizada
    }
}
