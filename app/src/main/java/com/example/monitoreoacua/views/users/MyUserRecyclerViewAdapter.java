package com.example.monitoreoacua.views.users;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.monitoreoacua.business.models.User;
import com.example.monitoreoacua.views.users.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.monitoreoacua.databinding.FragmentItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link com.example.monitoreoacua.business.models.User}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyUserRecyclerViewAdapter extends RecyclerView.Adapter<MyUserRecyclerViewAdapter.ViewHolder> {

    private List<User> mValues = new ArrayList<>();


    public void setUsers(List<User> users) {
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

        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            mNameView = binding.itemName;
            mEmailView = binding.itemEmail;
            mDniView = binding.itemDni;
            mAddressView = binding.itemAddress;
            mContactView = binding.itemContact;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}