package com.example.monitoreoacua.views.users;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.User;

import java.util.ArrayList;
import java.util.List;

public class MonitorUserAdapter extends RecyclerView.Adapter<MonitorUserAdapter.UserViewHolder>{
    private final List<User> users;
    private final List<Integer> selectedUserIds = new ArrayList<>();

    public MonitorUserAdapter(List<User> users) {
        this.users = users;
    }

    public List<Integer> getSelectedUserIds() {
        return selectedUserIds;
    }
    
    public void clearSelections() {
        selectedUserIds.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_user_monitor, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.name.setText(user.getName());
        
        // Configurar el checkbox
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedUserIds.contains(user.getId()));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedUserIds.add(user.getId());
            } else {
                selectedUserIds.remove((Integer) user.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CheckBox checkBox;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_user_name);
            checkBox = itemView.findViewById(R.id.checkbox_user);
        }
    }
}
