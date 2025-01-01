package com.arr.enruta.utils.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import com.arr.enruta.R;
import android.view.WindowManager;
import com.arr.enruta.databinding.LayoutViewDialogProgressBinding;

public class LoadingDialog extends Dialog {

    private LayoutViewDialogProgressBinding binding;

    public LoadingDialog(Context context) {
        super(context);
        binding = LayoutViewDialogProgressBinding.inflate(getLayoutInflater());

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(params);
        getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.bg_dialog));
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        setContentView(binding.getRoot());
    }
}
