package com.example.monitoreoacua.views.measurements;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Measurement;
import com.example.monitoreoacua.business.models.Sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying measurements in a RecyclerView
 * This adapter binds measurement data to the recycler_view_item_measurement layout
 */
public class MeasurementsAdapter extends RecyclerView.Adapter<MeasurementsAdapter.MeasurementViewHolder> {

    private List<Measurement> measurementList;
    private OnMeasurementClickListener listener;

    /**
     * Constructor for MeasurementAdapter
     * Initializes an empty list of measurements
     */
    public MeasurementsAdapter() {
        this.measurementList = new ArrayList<>();
    }

    /**
     * Set the measurement click listener
     *
     * @param listener The listener for measurement click events
     */
    public void setOnMeasurementClickListener(OnMeasurementClickListener listener) {
        this.listener = listener;
    }

    /**
     * Update the measurement list and refresh the adapter
     *
     * @param measurementList The new list of measurements to display
     */
    public void setMeasurementList(List<Measurement> measurementList) {
        this.measurementList = measurementList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MeasurementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_measurement, parent, false);
        return new MeasurementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MeasurementViewHolder holder, int position) {
        Measurement measurement = measurementList.get(position);
        Sensor sensor = measurement.getSensor();

        // Obtener el color basado en el valor y rango
        double measurementValue = measurement.getValue();

        // Verificar primero si el valor es mayor o igual a 35.00 (siempre en rojo)
        if (measurementValue >= 35.00) {
            // Valor mayor o igual a 35.00 - color rojo
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_light));
        }
        // Verificar si el valor es cero
        else if (measurementValue == 0.0) {
            // Valor en cero - color azul
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_blue_light));
        } 
        // Si no es cero ni mayor a 35, verificar si está dentro del rango
        else if (isValueInRange(measurementValue, sensor)) {
            // Valor dentro del rango - color verde
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_light));
        } else {
            // Valor fuera del rango - color rojo
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_light));
        }

        // Format the value with 2 decimal places
        String formattedValue = String.format("%.2f", measurement.getValue());

        // Set measurement title based on sensor name
        if (sensor != null && sensor.getName() != null && !sensor.getName().isEmpty()) {
            holder.tvMeasurementTitle.setText(sensor.getName());
        } else {
            holder.tvMeasurementTitle.setText("Medición");
        }

        // Set measurement type - extract from threshold if available
        String sensorType = getSensorTypeFromThresholds(sensor);
        if (!sensorType.isEmpty()) {
            String readableType = getReadableSensorType(sensorType);
            holder.tvMeasurementType.setText(readableType);
        } else {
            holder.tvMeasurementType.setText("Tipo de medida desconocido");
        }

        // Set measurement value with corresponding unit
        if (!sensorType.isEmpty()) {
            String unit = getSensorUnit(sensorType);
            holder.tvMeasurementValue.setText("Valor: " + formattedValue + " " + unit);
        } else {
            holder.tvMeasurementValue.setText("Valor: " + formattedValue);
        }

        // Set date
        if (measurement.getDate() != null && !measurement.getDate().isEmpty()) {
            holder.tvMeasurementDate.setText(measurement.getDate());
        } else {
            holder.tvMeasurementDate.setText("Fecha desconocida");
        }

        // Set time
        if (measurement.getTime() != null && !measurement.getTime().isEmpty()) {
            holder.tvMeasurementTime.setText(measurement.getTime());
        } else {
            holder.tvMeasurementTime.setText("Hora desconocida");
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMeasurementClick(measurement);
            }
        });
    }

    /**
     * Extracts sensor type from the threshold list if available
     * @param sensor The sensor object
     * @return The type of the sensor from thresholds, or empty string if not available
     */
    private String getSensorTypeFromThresholds(Sensor sensor) {
        if (sensor == null || sensor.getThresholds() == null || sensor.getThresholds().isEmpty()) {
            return "";
        }

        // Try to get the type from the first threshold
        for (Sensor.Threshold threshold : sensor.getThresholds()) {
            if (threshold != null && threshold.getType() != null && !threshold.getType().isEmpty()) {
                return threshold.getType();
            }
        }

        return "";
    }

    /**
     * Get a human-readable name for sensor type
     * @param type The sensor type
     * @return A human-readable name for the sensor type in Spanish
     */
    private String getReadableSensorType(String type) {
        if (type == null) return "Desconocido";

        switch (type.toLowerCase()) {
            case "temperature":
                return "Temperatura";
            case "ph":
                return "pH";
            case "oxygen":
                return "Oxígeno Disuelto";
            case "ec":
                return "Conductividad Eléctrica";
            case "humidity":
                return "Humedad";
            case "light":
                return "Luz";
            case "waterlevel":
                return "Nivel de Agua";
            default:
                return type;
        }
    }

    /**
     * Get the appropriate unit for a sensor type
     * @param type The sensor type
     * @return The unit for the given sensor type
     */
    private String getSensorUnit(String type) {
        if (type == null) return "";

        switch (type.toLowerCase()) {
            case "temperature":
                return "°C";
            case "ph":
                return "pH";
            case "oxygen":
                return "mg/L";
            case "ec":
                return "μS/cm";
            case "humidity":
                return "%";
            case "light":
                return "lux";
            case "waterlevel":
                return "cm";
            default:
                return "";
        }
    }

    /**
     * Verifica si un valor está dentro del rango definido por los thresholds del sensor
     * @param value El valor de la medición a verificar
     * @param sensor El sensor que tiene los thresholds
     * @return true si el valor está dentro del rango, false en caso contrario
     */
    private boolean isValueInRange(double value, Sensor sensor) {
        // Si no hay sensor o thresholds, asumimos que está dentro del rango
        if (sensor == null || sensor.getThresholds() == null || sensor.getThresholds().isEmpty()) {
            return true; 
        }

        double minThreshold = Double.MIN_VALUE;
        double maxThreshold = Double.MAX_VALUE;
        boolean hasMin = false;
        boolean hasMax = false;

        // Verificamos cada threshold y extraemos los valores min y max
        for (Sensor.Threshold threshold : sensor.getThresholds()) {
            if (threshold != null && threshold.getType() != null && threshold.getValue() != null) {
                try {
                    double thresholdValue = Double.parseDouble(threshold.getValue());
                    
                    if (threshold.getType().equalsIgnoreCase("min")) {
                        minThreshold = thresholdValue;
                        hasMin = true;
                    } else if (threshold.getType().equalsIgnoreCase("max")) {
                        maxThreshold = thresholdValue;
                        hasMax = true;
                    }
                } catch (NumberFormatException e) {
                    // Si no se puede parsear el valor, lo ignoramos
                    continue;
                }
            }
        }
        
        // Verificamos si el valor está dentro del rango
        if (hasMin && value < minThreshold) {
            return false; // Por debajo del mínimo
        }
        
        if (hasMax && value > maxThreshold) {
            return false; // Por encima del máximo
        }
        
        // Si pasó todas las verificaciones, está dentro del rango
        return true;
    }

    @Override
    public int getItemCount() {
        return measurementList.size();
    }

    /**
     * ViewHolder for measurement items
     * Binds to the recycler_view_item_measurement.xml layout
     */
    static class MeasurementViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMeasurementTitle;
        private TextView tvMeasurementType;
        private TextView tvMeasurementValue;
        private TextView tvMeasurementDate;
        private TextView tvMeasurementTime;
        private CardView cardView;

        MeasurementViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMeasurementTitle = itemView.findViewById(R.id.tvMeasurementTitle);
            tvMeasurementType = itemView.findViewById(R.id.tvMeasurementType);
            tvMeasurementValue = itemView.findViewById(R.id.tvMeasurementValue);
            tvMeasurementDate = itemView.findViewById(R.id.tvMeasurementDate);
            tvMeasurementTime = itemView.findViewById(R.id.tvMeasurementTime);
            cardView = itemView.findViewById(R.id.cardViewMeasurement);
        }
    }

    /**
     * Interface for handling measurement click events
     */
    public interface OnMeasurementClickListener {
        void onMeasurementClick(Measurement measurement);
    }
}