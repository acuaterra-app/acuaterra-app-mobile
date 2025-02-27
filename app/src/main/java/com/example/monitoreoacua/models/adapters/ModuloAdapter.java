package com.example.monitoreoacua.models.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.monitoreoacua.R;
import java.util.List;


import com.example.monitoreoacua.R;
import com.example.monitoreoacua.models.objects.Modulo;

public class ModuloAdapter extends RecyclerView.Adapter<ModuloAdapter.ViewHolder> {
    private List<Modulo> modules;

    public ModuloAdapter(List<Modulo> modules) {
        this.modules = modules;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_modulo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Modulo modulo = modules.get(position);
        holder.tvName.setText(modulo.getNombre());
        holder.tvUbicacion.setText("Ubicación: " + modulo.getUbicacion());
        holder.tvEspecie.setText("Especie: " + modulo.getEspeciePescados());

        String asignadoA = modulo.getNombrePersonaAsignada() != null
                ? modulo.getNombrePersonaAsignada()
                : "Sin asignar";
        holder.tvPersonaAsignada.setText("Asignado a: " + asignadoA);
    }

    @Override
    public int getItemCount() {

        return modules.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvUbicacion, tvEspecie, tvPersonaAsignada;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvEspecie = itemView.findViewById(R.id.tvEspecie);
            tvPersonaAsignada = itemView.findViewById(R.id.tvPersonaAsignada);
        }
    }

    public void updateData(List<Modulo> newModules) {
        //modules = newModules;
        //notifyDataSetChanged();
        modules.clear();
        modules.addAll(newModules);
        notifyDataSetChanged();
    }
}
