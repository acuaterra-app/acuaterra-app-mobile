package com.example.monitoreoacua.views.farms.farm.modules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Sensor;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {

    private final List<Sensor> sensors;
    private final LayoutInflater inflater;
    private final OnSensorInteractionListener listener;

    public SensorAdapter(Context context, List<Sensor> sensors, OnSensorInteractionListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.sensors = sensors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycle_view_item_sensor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sensor sensor = sensors.get(position);
        holder.bindData(sensor);
    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtSensorName;
        private final TextView txtMinThreshold;
        private final TextView txtMaxThreshold;
        private final Button btnViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSensorName = itemView.findViewById(R.id.sensor_name);
            txtMinThreshold = itemView.findViewById(R.id.min_threshold_value);
            txtMaxThreshold = itemView.findViewById(R.id.max_threshold_value);
            btnViewDetails = itemView.findViewById(R.id.view_sensor_details_button);
        }

        public void bindData(final Sensor sensor) {
            txtSensorName.setText(sensor.getName());
            
            // Find min and max thresholds
            String minThreshold = "Min: N/A";
            String maxThreshold = "Max: N/A";
            
            if (sensor.getThresholds() != null) {
                for (Sensor.Threshold threshold : sensor.getThresholds()) {
                    if (threshold.getType().equals("min")) {
                        minThreshold = "Min: " + threshold.getValue();
                    } else if (threshold.getType().equals("max")) {
                        maxThreshold = "Max: " + threshold.getValue();
                    }
                }
            }
            
            txtMinThreshold.setText(minThreshold);
            txtMaxThreshold.setText(maxThreshold);
            
            btnViewDetails.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewSensorDetails(sensor);
                }
            });
            
            // Make the whole item clickable
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSensorSelected(sensor);
                }
            });
        }
    }

    // Interface for handling sensor selection
    public interface OnSensorInteractionListener {
        void onSensorSelected(Sensor sensor);
        void onViewSensorDetails(Sensor sensor);
    }

    // Method to update data
    public void updateData(List<Sensor> newSensors) {
        this.sensors.clear();
        this.sensors.addAll(newSensors);
        notifyDataSetChanged();
    }
}

