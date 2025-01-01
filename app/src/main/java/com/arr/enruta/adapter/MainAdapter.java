package com.arr.enruta.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.arr.enruta.databinding.LayoutItemEmptyBinding;
import com.arr.enruta.databinding.LayoutItemsCodigosBinding;
import com.arr.enruta.models.Codes;
import com.arr.enruta.utils.storage.Code;
import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Code> mList;

    private final int VIEW_EMPTY = 0;
    private final int VIEW_ITEMS = 1;

    private OnClickItemListener mListener;

    public MainAdapter(ArrayList<Code> list, OnClickItemListener listner) {
        this.mList = list;
        this.mListener = listner;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mList == null || mList.isEmpty()) {
            LayoutItemEmptyBinding binding =
                    LayoutItemEmptyBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false);
            return new EmptyViewHolder(binding);
        }
        LayoutItemsCodigosBinding binding =
                LayoutItemsCodigosBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EmptyViewHolder) {
            return;
        }
        if (holder instanceof ItemsViewHolder view && !mList.isEmpty() && position < mList.size()) {
            Code model = mList.get(position);
            view.binding.textCode.setText(model.code);
            view.binding.textStatus.setText(getStatus(model.estado));

            view.binding.root.setOnClickListener(v -> mListener.onClickItems(position));
        }
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

    @Override
    public int getItemCount() {
        if (mList == null || mList.isEmpty()) {
            return 1;
        } else {
            return mList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mList == null || mList.isEmpty()) {
            return VIEW_EMPTY;
        } else {
            return VIEW_ITEMS;
        }
    }

    public interface OnClickItemListener {
        void onClickItems(int position);
    }

    class ItemsViewHolder extends RecyclerView.ViewHolder {

        private LayoutItemsCodigosBinding binding;

        public ItemsViewHolder(LayoutItemsCodigosBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {

        private LayoutItemEmptyBinding binding;

        public EmptyViewHolder(LayoutItemEmptyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
