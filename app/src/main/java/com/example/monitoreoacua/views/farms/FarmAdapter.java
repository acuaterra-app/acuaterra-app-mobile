package com.example.monitoreoacua.views.farms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Farm;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying Farm objects in a RecyclerView
 */
public class FarmAdapter extends RecyclerView.Adapter<FarmAdapter.FarmViewHolder> {

    private List<Farm> farmList;
    private OnFarmClickListener listener;

    public interface OnFarmClickListener {
        void onFarmClick(Farm farm);
    }

    public FarmAdapter() {
        this.farmList = new ArrayList<>();
    }

    public void setOnFarmClickListener(OnFarmClickListener listener) {
        this.listener = listener;
    }

    public void setFarmList(List<Farm> farmList) {
        this.farmList = farmList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_farm, parent, false);
        return new FarmViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FarmViewHolder holder, int position) {
        Farm farm = farmList.get(position);
        holder.tvFarmName.setText(farm.getName());
        holder.tvFarmAddress.setText(farm.getAddress());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFarmClick(farm);
            }
        });
    }

    @Override
    public int getItemCount() {
        return farmList.size();
    }

    class FarmViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFarmName, tvFarmAddress, tvFarmCoordinates;

        FarmViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFarmName = itemView.findViewById(R.id.tvFarmName);
            tvFarmAddress = itemView.findViewById(R.id.tvFarmAddress);
            tvFarmCoordinates = itemView.findViewById(R.id.tvFarmCoordinates);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onFarmClick(farmList.get(position));
                }
            });
        }

        void bind(Farm farm) {
            tvFarmName.setText(farm.getName());
            tvFarmAddress.setText(farm.getAddress());
            tvFarmCoordinates.setText(farm.getLatitude() + ", " + farm.getLongitude());
        }
    }
}

