package com.arr.enruta.utils.dialogs;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.arr.enruta.HistoryActivity;
import com.arr.enruta.TrackActivity;
import com.arr.enruta.api.EnviosClient;
import com.arr.enruta.api.EnviosRetrofit;
import com.arr.enruta.api.model.Dato;
import com.arr.enruta.api.model.EnviosResponse;
import com.arr.enruta.databinding.LayoutViewDialogCodeBinding;
import com.arr.enruta.utils.storage.Code;
import com.arr.enruta.utils.storage.CodeManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CheckCodeDialog extends BottomSheetDialog {

    private LayoutViewDialogCodeBinding binding;
    private EnviosClient api;
    private String mCode;
    private String mAnno;

    public CheckCodeDialog(Context context) {
        super(context);
        binding = LayoutViewDialogCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        // api correos
        api = EnviosRetrofit.auth();

        binding.btnDone.setEnabled(false);
        binding.editCodigo.requestFocus();
        InputMethodManager imm =
                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.editCodigo, InputMethodManager.SHOW_IMPLICIT);

        binding.editCodigo.addTextChangedListener(new ValidateCode());

        // ultimos tres años
        ArrayList<String> year = getLastThreeYears();
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        context, android.R.layout.simple_dropdown_item_1line, year);
        binding.anno.setAdapter(adapter);

        binding.anno.setAdapter(adapter);

        // Seleccionar el primer año (el más reciente) por defecto sin filtrar
        if (!year.isEmpty()) {
            binding.anno.setText(year.get(0), false); // Usa false para evitar el filtro
        }

        // Configurar el listener para manejar la selección
        binding.anno.setOnItemClickListener(
                (parent, view, position, id) -> {
                    String selectedYear = year.get(position);
                    binding.anno.setText(selectedYear, false); // Actualiza el texto sin filtrar
                });

        // launch to HistoryActivity
        binding.btnDone.setOnClickListener(this::setLaunchHistory);

        // clipboard
        checkClipboardAndSetText(context, binding.btnPortapapeles);
        binding.btnPortapapeles.setOnClickListener(
                v -> {
                    String code = binding.btnPortapapeles.getText().toString();
                    binding.editCodigo.setText(code);
                });
    }

    private void setLaunchHistory(View view) {
        String code = binding.editCodigo.getText().toString();
        String anno = binding.anno.getText().toString();
        if (!TextUtils.isEmpty(code)) {
            getStatusDelivery(code, anno);
            mCode = code;
            mAnno = anno;
            if (binding.btnPortapapeles.getVisibility() == View.VISIBLE) {
                binding.btnPortapapeles.setVisibility(View.GONE);
            }
            binding.progress.setVisibility(View.VISIBLE);
        }
    }

    private void getStatusDelivery(String codigo, String anno) {
        Single<EnviosResponse> envios = api.getEnvios(codigo, anno, "Persona");
        envios.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handlerSuccess, this::handlerError);
    }

    private void handlerSuccess(EnviosResponse response) {
        if (response.error.equals("")) {
            binding.progress.setVisibility(View.GONE);

            addOrUpdateCode(mCode, mAnno, response.estado);

            // Convierte `List<Dato>` a `ArrayList<Dato>`
            ArrayList<Dato> listaDatos = new ArrayList<>(response.datos);

            // Crea el Intent y añade la lista
            Intent intent = new Intent(getContext(), TrackActivity.class);
            intent.putExtra("origen", response.origen);
            intent.putExtra("destino", response.destino);
            intent.putExtra("estado", response.estado);
            intent.putExtra("codigo", mCode);
            intent.putParcelableArrayListExtra("lista_datos", listaDatos);

            // Inicia el Activity
            getContext().startActivity(intent);
            dismiss();
        } else {
            binding.progress.setVisibility(View.GONE);
            Toast.makeText(getContext(), "" + response.error, Toast.LENGTH_LONG).show();
        }
    }

    private void handlerError(Throwable error) {
        binding.progress.setVisibility(View.GONE);
        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
    }

    public void addOrUpdateCode(String newCode, String anno, String estado) {
        ArrayList<Code> codes = CodeManager.loadCodes(getContext());
        for (Code code : codes) {
            if (code.code.equals(newCode)) {
                break;
            }
        }

        codes.add(new Code(newCode, anno, estado)); // Agregar nuevo código si no existe
        CodeManager.saveCodes(codes, getContext()); // Guardar la lista actualizada
    }

    private ArrayList<String> getLastThreeYears() {
        ArrayList<String> yearList = new ArrayList<>();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        // Generar los últimos tres años
        for (int i = 0; i < 3; i++) {
            yearList.add(yearFormat.format(calendar.getTime()));
            calendar.add(Calendar.YEAR, -1);
        }

        return yearList;
    }

    public class ValidateCode implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void afterTextChanged(Editable e) {
            String code = e.toString();
            if (code.length() > 3) {
                binding.btnDone.setEnabled(true);
            } else {
                binding.btnDone.setEnabled(false);
            }
        }
    }

    private void checkClipboardAndSetText(Context context, MaterialButton buttom) {
        ClipboardManager clipboard =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        // Verificar si hay datos en el portapapeles
        if (clipboard != null && clipboard.hasPrimaryClip()) {
            ClipData clipData = clipboard.getPrimaryClip();

            if (clipData != null && clipData.getItemCount() > 0) {
                // Obtener el texto copiado
                CharSequence copiedText = clipData.getItemAt(0).getText();

                if (!TextUtils.isEmpty(copiedText)) {
                    String text = copiedText.toString();

                    // Verificar si el texto cumple con los requisitos
                    if (text.equals(text.toUpperCase()) && text.length() > 8) {
                        buttom.setVisibility(View.VISIBLE);
                        buttom.setText(text);
                    }
                }
            }
        }
    }
}
