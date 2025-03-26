package com.example.monitoreoacua.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiModulesService;
import com.example.monitoreoacua.service.request.ListModulesRequest;
import com.example.monitoreoacua.service.response.ListModuleResponse;
import com.example.monitoreoacua.views.farms.farm.modules.ModuleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListModulesFragment extends Fragment implements ModuleAdapter.OnModuleClickListener {

    private RecyclerView recyclerViewModules;
    private ModuleAdapter moduleAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvEmptyView;
    private Button btnRegisterModules;
    
    private String farmId;

    private OnModuleInteractionListener listener;

    private static final String PREF_NAME = "user_prefs";

    public interface OnModuleInteractionListener {
        void onModuleSelected(Module module);
        void onRegisterNewModule();
    }
    
    public static ListModulesFragment newInstance(String farmId) {
        ListModulesFragment fragment = new ListModulesFragment();
        Bundle args = new Bundle();
        args.putString("farmId", farmId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            farmId = getArguments().getString("farmId");
            Log.d("ListModulesFragment", "farmId: " + farmId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_modules, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        recyclerViewModules = view.findViewById(R.id.recyclerViewModules);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        tvEmptyView = view.findViewById(R.id.tvEmptyView);
        btnRegisterModules = view.findViewById(R.id.btnRegisterModules);
        
        recyclerViewModules.setLayoutManager(new LinearLayoutManager(getContext()));
        moduleAdapter = new ModuleAdapter();
        moduleAdapter.setOnModuleClickListener(this);
        recyclerViewModules.setAdapter(moduleAdapter);
        
        swipeRefreshLayout.setOnRefreshListener(this::fetchModules);

        btnRegisterModules.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRegisterNewModule();
            }
        });

        fetchModules();
    }
    
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnModuleInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnModuleInteractionListener");
        }
    }

    @Override
    public void onModuleClick(Module module) {
        if (listener != null) {
            listener.onModuleSelected(module);
        }
    }
    
    private void fetchModules() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewModules.setVisibility(View.GONE);
        tvEmptyView.setVisibility(View.GONE);
        
        Map<String, Object> params = new HashMap<>();
        params.put("farmId", farmId);
        ApiModulesService apiService = ApiClient.getClient().create(ApiModulesService.class);
        ListModulesRequest listModulesRequest = new ListModulesRequest();

        Call<ListModuleResponse> call = apiService.getModules(Integer.parseInt(farmId), listModulesRequest.getAuthToken());
        call.enqueue(new Callback<ListModuleResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListModuleResponse> call, @NonNull Response<ListModuleResponse> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    ListModuleResponse moduleResponse = response.body();
                    List<Module> modules = moduleResponse.getData();
                    
                    if (modules != null && !modules.isEmpty()) {
                        moduleAdapter.setModuleList(modules);
                        recyclerViewModules.setVisibility(View.VISIBLE);
                        tvEmptyView.setVisibility(View.GONE);
                    } else {
                        moduleAdapter.setModuleList(new ArrayList<>());
                        recyclerViewModules.setVisibility(View.GONE);
                        tvEmptyView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.e("ListModulesFragment", "Error al obtener los módulos: " + response);
                    showError("Error al obtener los módulos: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ListModuleResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                showError("Error de conexión: " + t.getMessage());
            }
        });
    }
    
    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        recyclerViewModules.setVisibility(View.GONE);
        tvEmptyView.setVisibility(View.VISIBLE);
        tvEmptyView.setText(R.string.error_loading_modules);
    }
    
    public void refreshModules() {
        fetchModules();
    }
}

