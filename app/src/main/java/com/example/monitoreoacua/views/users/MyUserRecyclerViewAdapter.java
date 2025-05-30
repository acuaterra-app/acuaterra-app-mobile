package com.example.monitoreoacua.views.users;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.monitoreoacua.business.models.User;
import com.example.monitoreoacua.databinding.FragmentItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link com.example.monitoreoacua.business.models.User}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyUserRecyclerViewAdapter extends RecyclerView.Adapter<MyUserRecyclerViewAdapter.ViewHolder> {

    private List<User> mValues = new ArrayList<>();
    private final OnUserClickListener onUserClickListener;

    public MyUserRecyclerViewAdapter(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }
    public void setUsers(List<User> users) {
        if (users == null) {
            users = new ArrayList<>();
        }
        mValues = users;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        User user = mValues.get(position);
        holder.mNameView.setText(user.getName());
        holder.mEmailView.setText(user.getEmail());
        holder.mDniView.setText(user.getDni());
        holder.mAddressView.setText(user.getAddress());
        holder.mContactView.setText(user.getContact());

        holder.updateButton.setOnClickListener(v -> onUserClickListener.onUserClick(user));
        holder.deleteButton.setOnClickListener(v -> onUserClickListener.onUserDelete(user));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mNameView;
        public final TextView mEmailView;
        public final TextView mDniView;
        public final TextView mAddressView;
        public final TextView mContactView;
        public final Button updateButton;
        public final Button deleteButton;

        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            mNameView = binding.itemName;
            mEmailView = binding.itemEmail;
            mDniView = binding.itemDni;
            mAddressView = binding.itemAddress;
            mContactView = binding.itemContact;
            updateButton = binding.updateButton;
            deleteButton = binding.deleteButton;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }

    public interface OnUserClickListener {
        void onUserClick(User user);
        void onUserDelete(User user);
    }
}