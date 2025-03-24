package com.example.monitoreoacua.views.farms.farm.modules;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Module;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {

    private List<Module> moduleList;
    private OnModuleClickListener listener;

    public ModuleAdapter() {
        this.moduleList = new ArrayList<>();
    }

    public void setOnModuleClickListener(OnModuleClickListener listener) {
        this.listener = listener;
    }

    public void setModuleList(List<Module> moduleList) {
        this.moduleList = moduleList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_module, parent, false);
        return new ModuleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        Module module = moduleList.get(position);
        
        // Set name and location (existing fields)
        holder.tvModuleName.setText(getTextOrDefault(module.getName(), "No name"));
        holder.tvModuleLocation.setText(getTextOrDefault(module.getLocation(), "No location"));
        
        // Set new fields with null checks
        holder.tvModuleLatitude.setText(getTextOrDefault(module.getLatitude(), "N/A"));
        holder.tvModuleLongitude.setText(getTextOrDefault(module.getLongitude(), "N/A"));
        holder.tvModuleSpeciesFish.setText(getTextOrDefault(module.getSpeciesFish(), "Not specified"));
        holder.tvModuleFishQuantity.setText(getTextOrDefault(module.getFishQuantity(), "Unknown"));
        holder.tvModuleFishAge.setText(getTextOrDefault(module.getFishAge(), "Not specified"));
        
        // Handle dimensions string and parse it into separate components
        //String dimensionsText = getTextOrDefault(module.getDimensions(), "Unknown dimensions");
        //holder.tvModuleDimensions.setText(dimensionsText);
        
        // Parse dimensions and set values
        parseDimensions(module.getDimensions(), holder);
        
        // Set click listener for item
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onModuleClick(module);
            }
        });
    }
    
    /**
     * Helper method to get text value or default if null/empty
     */
    private String getTextOrDefault(String value, String defaultValue) {
        return value != null && !value.isEmpty() ? value : defaultValue;
    }
    
    /**
     * Format dimension value with specified unit and locale
     * @param value The dimension value to format
     * @param unit The unit to append (e.g., "m", "m³")
     * @param errorState Whether to show error formatting
     * @return Formatted string with proper locale and unit
     */
    private String formatDimension(double value, String unit, boolean errorState) {
        // Format with Spanish locale for proper decimal separator (comma)
        String formatted = String.format(Locale.forLanguageTag("es-ES"), "%.2f %s", value, unit);
        
        // Return proper formatted value
        return formatted;
    }
    
    /**
     * Set TextView text with error state highlighting if needed
     * @param textView The TextView to update
     * @param text The text to set
     * @param isError Whether this is an error state
     */
    private void setDimensionText(TextView textView, String text, boolean isError) {
        textView.setText(text);
        if (isError) {
            textView.setTextColor(Color.RED);
        } else {
            textView.setTextColor(Color.parseColor("#212121"));
        }
    }
    
    /**
     * Parse dimensions string and set values to respective TextViews
     * Expected format: "widthxlengthxdepth" (e.g., "10x5x2")
     * Displays formatted values with units and calculates volume automatically
     */
    private void parseDimensions(String dimensions, ModuleViewHolder holder) {
        // Default values
        double widthVal = 0.0;
        double lengthVal = 0.0;
        double depthVal = 0.0;
        double volumeVal = 0.0;
        
        // Error state flags for each dimension
        boolean widthError = false;
        boolean lengthError = false;
        boolean depthError = false;
        boolean formatError = false;
        
        String errorMessage = "";
        
        if (dimensions != null && !dimensions.isEmpty()) {
            try {
                // Split the dimensions by 'x' character
                String[] dimensionParts = dimensions.split("x");
                
                // If we have 3 parts (width, length, depth)
                if (dimensionParts.length == 3) {
                    // Parse each dimension with additional validation
                    try {
                        widthVal = Double.parseDouble(dimensionParts[0].trim());
                        if (widthVal <= 0) {
                            widthError = true;
                            errorMessage = "El ancho debe ser positivo";
                            Log.w("ModuleAdapter", "Width must be positive: " + widthVal);
                        }
                    } catch (NumberFormatException e) {
                        widthError = true;
                        errorMessage = "Formato de ancho inválido";
                        Log.e("ModuleAdapter", "Invalid width format: " + dimensionParts[0]);
                    }
                    
                    try {
                        lengthVal = Double.parseDouble(dimensionParts[1].trim());
                        if (lengthVal <= 0) {
                            lengthError = true;
                            errorMessage = "El largo debe ser positivo";
                            Log.w("ModuleAdapter", "Length must be positive: " + lengthVal);
                        }
                    } catch (NumberFormatException e) {
                        lengthError = true;
                        errorMessage = "Formato de largo inválido";
                        Log.e("ModuleAdapter", "Invalid length format: " + dimensionParts[1]);
                    }
                    
                    try {
                        depthVal = Double.parseDouble(dimensionParts[2].trim());
                        if (depthVal <= 0) {
                            depthError = true;
                            errorMessage = "La profundidad debe ser positiva";
                            Log.w("ModuleAdapter", "Depth must be positive: " + depthVal);
                        }
                    } catch (NumberFormatException e) {
                        depthError = true;
                        errorMessage = "Formato de profundidad inválido";
                        Log.e("ModuleAdapter", "Invalid depth format: " + dimensionParts[2]);
                    }
                    
                    // Only calculate volume if all dimensions are valid
                    if (!widthError && !lengthError && !depthError) {
                        volumeVal = widthVal * lengthVal * depthVal;
                        Log.d("ModuleAdapter", String.format(
                            "Dimensiones: %.2f x %.2f x %.2f = %.2f m³", 
                            widthVal, lengthVal, depthVal, volumeVal));
                    } else {
                        Log.e("ModuleAdapter", "No se pudo calcular el volumen debido a dimensiones inválidas");
                    }
                } else {
                    formatError = true;
                    errorMessage = "Formato incorrecto (debe ser AxBxC)";
                    Log.e("ModuleAdapter", "Formato de dimensiones inválido: " + dimensions + " (formato esperado: anchoxlargoxprofundidad)");
                }
            } catch (Exception e) {
                formatError = true;
                errorMessage = "Error al procesar dimensiones";
                Log.e("ModuleAdapter", "Error al analizar dimensiones '" + dimensions + "': " + e.getMessage());
            }
        } else {
            formatError = true;
            errorMessage = "No se proporcionaron dimensiones";
            Log.d("ModuleAdapter", "No se proporcionaron dimensiones");
        }
        
        // Format the values with units and proper locale
        String width = formatDimension(widthVal, "m", widthError);
        String length = formatDimension(lengthVal, "m", lengthError);
        String depth = formatDimension(depthVal, "m", depthError);
        String volume = formatDimension(volumeVal, "m³", widthError || lengthError || depthError);
        
        // Set the parsed values to the TextViews with possible error states
        setDimensionText(holder.tvModuleWidth, width, widthError);
        setDimensionText(holder.tvModuleLength, length, lengthError);
        setDimensionText(holder.tvModuleDepth, depth, depthError);
        setDimensionText(holder.tvModuleVolume, volume, widthError || lengthError || depthError);
        
        // Show error message if any invalid dimensions
        if (formatError || widthError || lengthError || depthError) {
            // If we have an error message holder, use it
            if (holder.tvDimensionError != null) {
                holder.tvDimensionError.setVisibility(View.VISIBLE);
                holder.tvDimensionError.setText(errorMessage);
            } else {
                // Log error but can't display to user without error TextView
                Log.e("ModuleAdapter", "Error dimensiones: " + errorMessage);
            }
        } else if (holder.tvDimensionError != null) {
            holder.tvDimensionError.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        return moduleList.size();
    }

    static class ModuleViewHolder extends RecyclerView.ViewHolder {
        private TextView tvModuleName, tvModuleLocation;
        private TextView tvModuleLatitude, tvModuleLongitude;
        private TextView tvModuleSpeciesFish, tvModuleFishQuantity;
        private TextView tvModuleFishAge, tvModuleDimensions;
        private TextView tvModuleWidth, tvModuleLength, tvModuleDepth, tvModuleVolume;
        private TextView tvDimensionError; // Added to display dimension errors

        ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvModuleName = itemView.findViewById(R.id.tvModuleName);
            tvModuleLocation = itemView.findViewById(R.id.tvModuleLocation);
            tvModuleLatitude = itemView.findViewById(R.id.tvModuleLatitude);
            tvModuleLongitude = itemView.findViewById(R.id.tvModuleLongitude);
            tvModuleSpeciesFish = itemView.findViewById(R.id.tvModuleSpeciesFish);
            tvModuleFishQuantity = itemView.findViewById(R.id.tvModuleFishQuantity);
            tvModuleFishAge = itemView.findViewById(R.id.tvModuleFishAge);

            
            // Initialize the new dimension TextViews
            tvModuleWidth = itemView.findViewById(R.id.tvModuleWidth);
            tvModuleLength = itemView.findViewById(R.id.tvModuleLength);
            tvModuleDepth = itemView.findViewById(R.id.tvModuleDepth);
            tvModuleVolume = itemView.findViewById(R.id.tvModuleVolume);
            
            // Try to find the dimension error TextView (may not exist in all layouts)
            try {
                tvDimensionError = itemView.findViewById(R.id.tvDimensionError);
            } catch (Resources.NotFoundException e) {
                // If not found, just continue without error display
                tvDimensionError = null;
            }
        }
    }

    public interface OnModuleClickListener {
        void onModuleClick(Module module);
    }
}

