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
        holder.tvModuleName.setText(module.getName());
        holder.tvModuleLocation.setText(module.getLocation());

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

        ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvModuleName = itemView.findViewById(R.id.tvModuleName);
            tvModuleLocation = itemView.findViewById(R.id.tvModuleLocation);
        }
    }

    public interface OnModuleClickListener {
        void onModuleClick(Module module);
    }
}

