package com.example.monitoreoacua.views.users;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.User;
import com.example.monitoreoacua.interfaces.OnApiRequestCallback;
import com.example.monitoreoacua.service.request.ListUsersRequest;
import com.example.monitoreoacua.service.request.RegisterUserRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class UserFragment extends Fragment implements MyUserRecyclerViewAdapter.OnUserClickListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private ProgressBar progressBar;
    private MyUserRecyclerViewAdapter adapter;

    public UserFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UserFragment newInstance(int columnCount) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.list);
        progressBar = view.findViewById(R.id.progress_bar);
        Context context = view.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        adapter = new MyUserRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.register_user_fragment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment registerUserFragment = new RegisterUserFragment();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragmentContainer, registerUserFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        fetchUsers();

        return view;
    }

    private void fetchUsers() {
        progressBar.setVisibility(View.VISIBLE);
        new ListUsersRequest().fetchUsers(new OnApiRequestCallback<List<User>, Throwable>() {
            @Override
            public void onSuccess(List<User> users) {
                progressBar.setVisibility(View.GONE);
                adapter.setUsers(users);
            }

            @Override
            public void onFail(Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de conexión: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void editUser(User user) {
        RegisterUserFragment fragment = RegisterUserFragment.newInstance(user);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void deleteUser(User user) {
        new RegisterUserRequest().deleteUser(user.getId(), new OnApiRequestCallback<Void, Throwable>() {
            @Override
            public void onSuccess(Void response) {
                Toast.makeText(getContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show();
                fetchUsers(); // Refresh the user list
            }

            @Override
            public void onFail(Throwable t) {
                Toast.makeText(getContext(), "Error al eliminar usuario: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    @Override
    public void onUserClick(User user) {
        editUser(user);
    }

    @Override
    public void onUserDelete(User user) {
        deleteUser(user);
    }
}