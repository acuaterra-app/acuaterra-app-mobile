package com.example.monitoreoacua.views.farms.farm.modules;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Module;
import java.util.ArrayList;
import java.util.List;

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
        holder.tvModuleName.setText(module.getName() != null ? module.getName() : "No name");
        holder.tvModuleLocation.setText(module.getLocation() != null ? module.getLocation() : "No location");
        
        // Set new fields with null checks
        holder.tvModuleLatitude.setText(module.getLatitude() != null && !module.getLatitude().isEmpty() ? 
                module.getLatitude() : "N/A");
        
        holder.tvModuleLongitude.setText(module.getLongitude() != null && !module.getLongitude().isEmpty() ? 
                module.getLongitude() : "N/A");
        
        holder.tvModuleSpeciesFish.setText(module.getSpeciesFish() != null && !module.getSpeciesFish().isEmpty() ? 
                module.getSpeciesFish() : "Not specified");
        
        holder.tvModuleFishQuantity.setText(module.getFishQuantity() != null && !module.getFishQuantity().isEmpty() ? 
                module.getFishQuantity() : "Unknown");
        
        holder.tvModuleFishAge.setText(module.getFishAge() != null && !module.getFishAge().isEmpty() ? 
                module.getFishAge() : "Not specified");
        
        holder.tvModuleDimensions.setText(module.getDimensions() != null && !module.getDimensions().isEmpty() ? 
                module.getDimensions() : "Unknown dimensions");

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

    static class ModuleViewHolder extends RecyclerView.ViewHolder {
        private TextView tvModuleName, tvModuleLocation;
        private TextView tvModuleLatitude, tvModuleLongitude;
        private TextView tvModuleSpeciesFish, tvModuleFishQuantity;
        private TextView tvModuleFishAge, tvModuleDimensions;

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
        }
    }

    public interface OnModuleClickListener {
        void onModuleClick(Module module);
    }
}

