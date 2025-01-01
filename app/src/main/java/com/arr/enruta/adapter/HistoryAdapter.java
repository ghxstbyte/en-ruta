package com.arr.enruta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.arr.enruta.databinding.LayoutItemHistoryBinding;
import com.arr.enruta.models.History;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<History> mList;

    public HistoryAdapter(List<History> list) {
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutItemHistoryBinding binding =
                LayoutItemHistoryBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HistoryViewHolder view) {
            History model = mList.get(position);

            view.binding.textFecha.setText(model.fecha);
            view.binding.textEstado.setText(model.estado);
            if (!model.destino.isEmpty()) {
                view.binding.textDestino.setText(model.destino);
                view.binding.textDestino.setVisibility(View.VISIBLE);
            }
            if (!model.origen.isEmpty()) {
                view.binding.textOrigen.setText(model.origen);
                view.binding.textOrigen.setVisibility(View.VISIBLE);
            }
            if (!model.hora.isEmpty()) {
                view.binding.textHora.setText(model.hora);
                view.binding.textHora.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        private LayoutItemHistoryBinding binding;

        public HistoryViewHolder(LayoutItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
