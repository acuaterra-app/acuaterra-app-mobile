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
    private boolean isMonitor;
    
    // Removidas las constantes de SharedPreferences - ahora dependemos completamente de la API

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
     * Los estados ahora vienen directamente de la API a través del @SerializedName("is_active")
     * 
     * @param moduleList The new list of modules to display
     */
    public void setModuleList(List<Module> moduleList) {
        this.moduleList = moduleList;
        // El estado isActive ya viene sincronizado desde la API
        notifyDataSetChanged();
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
        
        // El estado isActive ya viene sincronizado desde la API via @SerializedName("is_active")
        
        // Configurar el borde del CardView según el estado real (considerando deletedAt)
        MaterialCardView cardView = (MaterialCardView) holder.itemView;
        boolean moduleIsActive = module.isReallyActive();
        if (moduleIsActive) {
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
        
        // Set text color based on real active state (considering deletedAt)
        if (moduleIsActive) {
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
            // Set button text and click listener based on real active state
            if (moduleIsActive) {
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
        
        if (token == null) {
            Toast.makeText(context, "Error de autenticación. Por favor, inicie sesión nuevamente.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Determinar el nuevo estado basado en el estado real actual
        final boolean newState = !module.isReallyActive();
        
        // Actualizar estado localmente para feedback inmediato
        module.setActive(newState);
        moduleList.set(position, module);
        notifyItemChanged(position);

        // Optional: Notify changes to filtered list if needed
        // filteredModulesList.set(position, module);
        // notifyItemChanged(position);
        
        // Mostrar feedback inmediato
        Toast.makeText(context, 
            newState ? "Activando módulo..." : "Desactivando módulo...", 
            Toast.LENGTH_SHORT).show();
        
        // Llamar al endpoint apropiado según el nuevo estado
        if (newState) {
            // Activar módulo (reactivate)
            apiService.reactivateModule(token, module.getId()).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            if (response.isSuccessful() && response.body() != null) {
                                // Actualizar el estado correctamente para reactivación
                                module.setActive(true);
                                // Limpiar deletedAt ya que el módulo se reactivó
                                // Note: En caso ideal, la respuesta de la API debería incluir el objeto actualizado
                                moduleList.set(position, module);
                                notifyItemChanged(position);
                                Toast.makeText(context, "Módulo activado correctamente", Toast.LENGTH_SHORT).show();
                            } else {
                                // Revertir cambio local en caso de error
                                module.setActive(!newState);
                                moduleList.set(position, module);
                                notifyItemChanged(position);
                                String errorMsg = "Error al activar módulo: " + response.code();
                                if (response.code() == 401) {
                                    errorMsg = "Error de autenticación. Verifique sus credenciales.";
                                } else if (response.code() == 404) {
                                    errorMsg = "Módulo no encontrado.";
                                }
                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            // Revertir cambio local en caso de error
                            module.setActive(!newState);
                            moduleList.set(position, module);
                            notifyItemChanged(position);
                            Toast.makeText(context, "Error de conexión al activar módulo", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
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
                                // Revertir cambio local en caso de error
                                module.setActive(!newState);
                                moduleList.set(position, module);
                                notifyItemChanged(position);
                                String errorMsg = "Error al desactivar módulo: " + response.code();
                                if (response.code() == 401) {
                                    errorMsg = "Error de autenticación. Verifique sus credenciales.";
                                } else if (response.code() == 404) {
                                    errorMsg = "Módulo no encontrado.";
                                }
                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            // Revertir cambio local en caso de error
                            module.setActive(!newState);
                            moduleList.set(position, module);
                            notifyItemChanged(position);
                            Toast.makeText(context, "Error de conexión al desactivar módulo", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        }
    }
}
