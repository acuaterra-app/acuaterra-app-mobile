package com.example.monitoreoacua.views.farms.farm.modules;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Module;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.widget.Toast;
import android.graphics.drawable.GradientDrawable;
import android.content.SharedPreferences;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiModulesService;
import com.example.monitoreoacua.service.request.BaseRequest;
import com.example.monitoreoacua.service.response.ApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.material.card.MaterialCardView;

/**
 * Adapter for displaying modules in a RecyclerView
 */
public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {

    private List<Module> moduleList;
    private OnModuleClickListener listener;
    private Context context;
    private RecyclerView recyclerViewModules;
    
    // Constantes para SharedPreferences
    private static final String PREF_NAME = "module_states";
    private static final String STATE_PREFIX = "module_state_";

    /**
     * Constructor for ModuleAdapter
     */
    public ModuleAdapter() {
        this.moduleList = new ArrayList<>();
    }

    public ModuleAdapter(Context context) {
        this.moduleList = new ArrayList<>();
        this.context = context;
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
        
        // Aplicar estados almacenados a los módulos
        for (int i = 0; i < moduleList.size(); i++) {
            Module module = moduleList.get(i);
            Boolean storedState = getStoredModuleState(module.getId());
            if (storedState != null) {
                module.setActive(storedState);
                moduleList.set(i, module);
            }
        }
        
        notifyDataSetChanged();
    }
    
    /**
     * Guarda el estado de un módulo en SharedPreferences
     */
    private void saveModuleState(int moduleId, boolean state) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().putBoolean(STATE_PREFIX + moduleId, state).apply();
        }
    }
    
    /**
     * Recupera el estado almacenado de un módulo
     * @return El estado almacenado o null si no existe
     */
    private Boolean getStoredModuleState(int moduleId) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            if (prefs.contains(STATE_PREFIX + moduleId)) {
                return prefs.getBoolean(STATE_PREFIX + moduleId, false);
            }
        }
        return null;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_view_item_module, parent, false);
        if (context == null) {
            context = parent.getContext();
        }
        return new ModuleViewHolder(itemView);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerViewModules = recyclerView;
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        Module module = moduleList.get(position);
        
        // Recuperar estado almacenado (ya debería estar aplicado en setModuleList, 
        // pero verificamos por si acaso)
        Boolean storedState = getStoredModuleState(module.getId());
        if (storedState != null) {
            module.setActive(storedState);
        }
        
        // Configurar el borde del CardView según el estado
        MaterialCardView cardView = (MaterialCardView) holder.itemView;
        if (module.isActive()) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
            cardView.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
            cardView.setStrokeWidth(4);
        } else {
            cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
            cardView.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark));
            cardView.setStrokeWidth(4);
        }
        
        // Set name and location (existing fields)
        holder.tvModuleName.setText(module.getName() != null ? module.getName() : "Sin nombre");
        
        // Set text color based on active state
        if (module.isActive()) {
            holder.tvModuleName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black));
        } else {
            holder.tvModuleName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark));
        }
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

        // Set button text and click listener
        if (module.isActive()) {
            holder.btnToggleModule.setText("Desactivar");
            holder.btnToggleModule.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
        } else {
            holder.btnToggleModule.setText("Activar");
            holder.btnToggleModule.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark));
        }

        holder.btnToggleModule.setOnClickListener(v -> {
            // Update module status through API
            updateModuleStatus(module, position);
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
        private Button btnToggleModule;

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
            btnToggleModule = itemView.findViewById(R.id.btnToggleModule);
        }
    }

    /**
     * Interface for handling module click events
     */
    public interface OnModuleClickListener {
        void onModuleClick(Module module);
    }
    
    /**
     * Actualiza el estado del módulo a través de la API
     */
    private void updateModuleStatus(Module module, int position) {
        ApiModulesService apiService = ApiClient.getClient().create(ApiModulesService.class);
        String token = new BaseRequest().getAuthToken();
        
        // Cambiar el estado
        final boolean newState = !module.isActive();
        module.setActive(newState);
        
        // Guardar el estado en SharedPreferences
        saveModuleState(module.getId(), newState);
        
        // Actualizar la lista y la vista
        moduleList.set(position, module);
        notifyItemChanged(position);
        
        // Mostrar feedback
        Toast.makeText(context, 
            newState ? "Módulo activado" : "Módulo desactivado", 
            Toast.LENGTH_SHORT).show();
        
        // Notificar al servidor del cambio
        apiService.toggleModuleStatus(token, module.getId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(context, "Error al sincronizar con el servidor", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        Toast.makeText(context, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}
