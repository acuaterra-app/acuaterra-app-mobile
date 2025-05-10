package com.example.monitoreoacua.views.farms.farm.modules;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Module;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying modules in a RecyclerView
 */
public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {

    private List<Module> moduleList;
    private OnModuleClickListener listener;


    /**
     * Constructor for ModuleAdapter
     */
    public ModuleAdapter() {
        this.moduleList = new ArrayList<>();
    }

    /**
     * Set the module click listener
     * 
     * @param listener The listener for module click events
     */
    public void setOnModuleClickListener(OnModuleClickListener listener) {
        this.listener = listener;
    }

    /**
     * Update the module list and refresh the adapter
     * 
     * @param moduleList The new list of modules to display
     */
    public void setModuleList(List<Module> moduleList) {
        this.moduleList = moduleList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_view_item_module, parent, false);
        return new ModuleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        Module module = moduleList.get(position);
        
        // Set name and location (existing fields)
        holder.tvModuleName.setText(module.getName() != null ? module.getName() : "Sin nombre");
        holder.tvModuleLocation.setText("Ubicación: " + (module.getLocation() != null ? module.getLocation() : "No especificada"));
        
        // Set new fields with null checks
        holder.tvModuleLatitude.setText("Latitud: " + (module.getLatitude() != null && !module.getLatitude().isEmpty() ? 
                module.getLatitude() : "N/A"));
        
        holder.tvModuleLongitude.setText("Longitud: " + (module.getLongitude() != null && !module.getLongitude().isEmpty() ? 
                module.getLongitude() : "N/A"));
        
        holder.tvModuleSpeciesFish.setText("Especie: " + (module.getSpeciesFish() != null && !module.getSpeciesFish().isEmpty() ? 
                module.getSpeciesFish() : "No especificada"));
        
        holder.tvModuleFishQuantity.setText("Cantidad: " + (module.getFishQuantity() != null && !module.getFishQuantity().isEmpty() ? 
                module.getFishQuantity() : "0"));
        
        holder.tvModuleFishAge.setText("Edad: " + (module.getFishAge() != null && !module.getFishAge().isEmpty() ? 
                module.getFishAge() : "0 días"));
        
        holder.tvModuleDimensions.setText("Dimensiones: " + (module.getDimensions() != null && !module.getDimensions().isEmpty() ? 
                module.getDimensions() : "No especificadas"));

        holder.btnEditModule.setOnClickListener(v -> {
            if (listener != null) {
                // Requiere implementar onEditModuleClick en el listener
                listener.onEditModuleClick(module);
            }
        });

        holder.btnDesactiveModule.setOnClickListener(v -> {
            boolean isDeactivated = module.isDeactivated(); // Debes tener este campo en tu modelo

            // Cambiar el estado
            module.setDeactivated(!isDeactivated);

            // Cambiar texto y color del botón
            if (module.isDeactivated()) {
                holder.btnDesactiveModule.setText("Activar");
                holder.btnDesactiveModule.setBackgroundColor(Color.GRAY);
            } else {
                holder.btnDesactiveModule.setText("Desactivar");
                holder.btnDesactiveModule.setBackgroundColor(Color.RED);
            }

            // Notificar si necesitas manejar el evento externamente
            if (listener != null) {
                listener.onToggleModuleState(module);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onModuleClick(module);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }

    /**
     * ViewHolder for module items
     */
    static class ModuleViewHolder extends RecyclerView.ViewHolder {
        private TextView tvModuleName, tvModuleLocation;
        private TextView tvModuleLatitude, tvModuleLongitude;
        private TextView tvModuleSpeciesFish, tvModuleFishQuantity;
        private TextView tvModuleFishAge, tvModuleDimensions;
        Button btnEditModule, btnDesactiveModule;


        ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvModuleName = itemView.findViewById(R.id.tvModuleName);
            tvModuleLocation = itemView.findViewById(R.id.tvModuleLocation);
            tvModuleLatitude = itemView.findViewById(R.id.tvModuleLatitude);
            tvModuleLongitude = itemView.findViewById(R.id.tvModuleLongitude);
            tvModuleSpeciesFish = itemView.findViewById(R.id.tvModuleSpeciesFish);
            tvModuleFishQuantity = itemView.findViewById(R.id.tvModuleFishQuantity);
            tvModuleFishAge = itemView.findViewById(R.id.tvModuleFishAge);
            tvModuleDimensions = itemView.findViewById(R.id.tvModuleDimensions);
            btnEditModule = itemView.findViewById(R.id.btnEditModule);
            btnDesactiveModule = itemView.findViewById(R.id.btnDesactiveModule);
        }
    }

    /**
     * Interface for handling module click events
     */
    public interface OnModuleClickListener {
        void onModuleClick(Module module);
        void onEditModuleClick(Module module);
        void onToggleModuleState(Module module);
    }
}
