package com.example.monitoreoacua.views.farms.farm.modules;

import android.util.Log;
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
import com.example.monitoreoacua.business.utils.RolePermissionHelper;
import com.example.monitoreoacua.utils.SessionManager;
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
import android.os.Handler;
import android.os.Looper;

/**
 * Adapter for displaying modules in a RecyclerView
 */
public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {

    private List<Module> moduleList;
    private OnModuleClickListener listener;
    private Context context;
    private RecyclerView recyclerViewModules;
    private boolean isMonitor;
    
    // Constantes para SharedPreferences
    private static final String PREF_NAME = "module_states";
    private static final String STATE_PREFIX = "module_state_";
    
    // Constantes para reintentos
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;

    /**
     * Constructor for ModuleAdapter
     */
    public ModuleAdapter() {
        this.moduleList = new ArrayList<>();
        this.isMonitor = false;  // Default to false for safety
    }

    public ModuleAdapter(Context context) {
        this.moduleList = new ArrayList<>();
        this.context = context;
        if (context != null) {
            SessionManager sessionManager = SessionManager.getInstance(context);
            this.isMonitor = RolePermissionHelper.isMonitor(sessionManager.getUser());
            Log.d("ModuleAdapter", "Is monitor user: " + this.isMonitor);
        }
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
        // Check role again to be absolutely sure
        if (context != null) {
            SessionManager sessionManager = SessionManager.getInstance(context);
            isMonitor = RolePermissionHelper.isMonitor(sessionManager.getUser());
        }
        
        int layoutResId = isMonitor ? 
            R.layout.recycle_view_item_module_monitor : 
            R.layout.recycle_view_item_module;
        
        Log.d("ModuleAdapter", "Using layout: " + (isMonitor ? "monitor layout" : "owner layout"));
            
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(layoutResId, parent, false);
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

        // Only setup toggle button for non-monitor users
        if (!isMonitor && holder.btnToggleModule != null) {
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
        }

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
        
        // Validar token
        if (token == null) {
            Toast.makeText(context, "Error de autenticación. Por favor, inicie sesión nuevamente.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Determinar el nuevo estado
        final boolean newState = !module.isActive();
        
        // Actualizar estado localmente primero
        module.setActive(newState);
        
        // Guardar el estado en SharedPreferences
        saveModuleState(module.getId(), newState);
        
        // Actualizar la lista y la vista
        moduleList.set(position, module);
        notifyItemChanged(position);
        
        // Mostrar feedback inmediato
        Toast.makeText(context,
                newState ? "Activando módulo..." : "Desactivando módulo...",
                Toast.LENGTH_SHORT).show();
        
        // Llamar al endpoint apropiado según el nuevo estado
        if (newState) {
            // Activar módulo (reactivate) con reintentos automáticos
            attemptModuleActivation(apiService, token, module, position, newState, 1);
        } else {
            // Desactivar módulo (deactivate)
            apiService.deactivateModule(token, module.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            if (response.isSuccessful()) {
                                Toast.makeText(context, "Módulo desactivado correctamente", Toast.LENGTH_SHORT).show();
                            } else {
                                // Para desactivación, revertir solo en caso de errores críticos
                                if (response.code() == 401) {
                                    // Error de autenticación: revertir y requerir nueva autenticación
                                    revertModuleState(module, position, newState);
                                    Toast.makeText(context, "Error de autenticación. Verifique sus credenciales.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Otros errores: mantener estado y permitir reintentos
                                    String errorMsg;
                                    if (response.code() == 404) {
                                        errorMsg = "Módulo no encontrado. Puede intentar nuevamente.";
                                    } else {
                                        errorMsg = "Error al desactivar módulo: " + response.code() + ". Puede intentar nuevamente.";
                                    }
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            // Error de conexión: mantener estado y permitir reintentos
                            Toast.makeText(context, "Error de conexión al desactivar módulo. Puede intentar nuevamente.", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        }
    }
    
    /**
     * Restaura el estado del botón después de un error
     */
    private void restoreButton(int position, boolean isActive) {
        if (recyclerViewModules != null) {
            RecyclerView.ViewHolder viewHolder = recyclerViewModules.findViewHolderForAdapterPosition(position);
            if (viewHolder instanceof ModuleViewHolder) {
                ModuleViewHolder moduleHolder = (ModuleViewHolder) viewHolder;
                if (moduleHolder.btnToggleModule != null) {
                    moduleHolder.btnToggleModule.setEnabled(true);
                    if (isActive) {
                        moduleHolder.btnToggleModule.setText("Desactivar");
                        moduleHolder.btnToggleModule.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
                    } else {
                        moduleHolder.btnToggleModule.setText("Activar");
                        moduleHolder.btnToggleModule.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
                    }
                }
            }
        }
    }
    
    /**
     * Intenta activar un módulo con reintentos automáticos en caso de error 404
     */
    private void attemptModuleActivation(ApiModulesService apiService, String token, Module module, 
                                       int position, boolean newState, int attemptNumber) {
        apiService.reactivateModule(token, module.getId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            // Éxito: mostrar mensaje solo si es el primer intento o después de reintentos
                            Toast.makeText(context, "Módulo activado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            // Manejar errores según el código de respuesta
                            if (response.code() == 404 && attemptNumber < MAX_RETRIES) {
                                // Error 404: reintentar silenciosamente después de un delay
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    attemptModuleActivation(apiService, token, module, position, newState, attemptNumber + 1);
                                }, RETRY_DELAY_MS);
                            } else {
                                // Error definitivo: mostrar mensaje pero MANTENER el estado visual actualizado
                                // No revertir para permitir que el usuario pueda intentar nuevamente
                                String errorMsg;
                                if (response.code() == 401) {
                                    errorMsg = "Error de autenticación. Verifique sus credenciales.";
                                } else if (response.code() == 404) {
                                    errorMsg = ":)";
                                } else {
                                    errorMsg = "Error al activar módulo: " + response.code() + ". Puede intentar nuevamente.";
                                }
                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        if (attemptNumber < MAX_RETRIES) {
                            // Reintentar silenciosamente en caso de falla de conexión
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                attemptModuleActivation(apiService, token, module, position, newState, attemptNumber + 1);
                            }, RETRY_DELAY_MS);
                        } else {
                            // Error definitivo: mostrar mensaje pero MANTENER estado para permitir reintentos
                            Toast.makeText(context, "Error de conexión al activar módulo. Puede intentar nuevamente.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    
    /**
     * Revierte el estado del módulo en caso de error definitivo
     */
    private void revertModuleState(Module module, int position, boolean newState) {
        module.setActive(!newState);
        saveModuleState(module.getId(), !newState);
        moduleList.set(position, module);
        notifyItemChanged(position);
    }
    
}
