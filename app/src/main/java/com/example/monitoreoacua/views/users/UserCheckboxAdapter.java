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
import com.example.monitoreoacua.service.response.UserMonitorResponse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserCheckboxAdapter extends RecyclerView.Adapter<UserCheckboxAdapter.ViewHolder> {

    private final List<User> users = new ArrayList<>();
    private final List<Integer> selectedUserIds = new ArrayList<>();

    public void setUsers(List<User> users) {
        this.users.clear();
        if (users != null) {
            for (User user : users) {
                if (user.isActive()) {
                    this.users.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedUserIds() {
        return selectedUserIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.name.setText(user.getName());
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView name;
        public final CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_user_name);
            checkBox = itemView.findViewById(R.id.checkbox_user);
        }
    }

    public void removeUsersByIds(List<Integer> userIds) {
        if (userIds == null || users.isEmpty()) {
            return; // No hay IDs para eliminar
        }
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (userIds.contains(user.getId())) {
                iterator.remove(); // Usar el método remove del iterador
            }
        }
    }
}