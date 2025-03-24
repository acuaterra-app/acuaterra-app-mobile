package com.example.monitoreoacua.views.farms.farm.modules;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.api.ApiClient;
import com.example.monitoreoacua.api.modules.ApiModulesService;
import com.example.monitoreoacua.api.modules.ListModuleResponse;
import com.example.monitoreoacua.api.modules.ListModulesRequest;
import com.example.monitoreoacua.models.Module;
import com.example.monitoreoacua.utils.NavigationHelper;
import com.example.monitoreoacua.views.farms.farm.modules.RegisterModulesActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListModulesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ModuleAdapter moduleAdapter;
    private TextView textViewModules;
    private List<Module> modulesList = new ArrayList<>();
    private EditText editTextSearch;
    private AppCompatImageButton buttonSort;
    private boolean isAscendingOrder = true;
    private List<Module> originalModulesList = new ArrayList<>();
    private LinearLayout navigationContainer;
    private Button buttonAddModule;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules_list);
        
        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewModules);
        textViewModules = findViewById(R.id.textViewModules);
        editTextSearch = findViewById(R.id.editTextSearchModule);
        buttonSort = findViewById(R.id.buttonSortModules);
        navigationContainer = findViewById(R.id.bottomNav);
        buttonAddModule = findViewById(R.id.buttonAddModule);
        
        // Initialize navigation
        NavigationHelper.setupNavigation(navigationContainer, 
            itemId -> NavigationHelper.navigateToSection(this, itemId, ListModulesActivity.class));
        NavigationHelper.updateSelectedItem(navigationContainer, R.id.navModules);
        
        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        moduleAdapter = new ModuleAdapter();
        recyclerView.setAdapter(moduleAdapter);
        
        // Setup search
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterModules(s.toString());
            }
        });
        
        // Setup sort button
        buttonSort.setOnClickListener(v -> {
            isAscendingOrder = !isAscendingOrder;
            sortModules();
            buttonSort.setRotation(isAscendingOrder ? 0 : 180);
        });
        
        // Add module button click listener
        buttonAddModule.setOnClickListener(v -> {
            int farmId = getIntent().getIntExtra("farmId", -1);
            if (farmId != -1) {
                // Navigate to RegisterModulesActivity with farmId
                // Implementation would go here
            } else {
                Toast.makeText(this, "Error: ID de granja no proporcionado", Toast.LENGTH_LONG).show();
            }
        });
        
        // Get farm ID and load modules
        int farmId = getIntent().getIntExtra("farmId", -1);
        if (farmId != -1) {
            loadModules(farmId);
        } else {
            showError("Error: ID de granja no proporcionado");
        }
    }

    private void loadModules(int farmId) {
        ApiModulesService apiModulesService = ApiClient.getClient().create(ApiModulesService.class);
        ListModulesRequest listModulesRequest = new ListModulesRequest();
        String authToken = listModulesRequest.getAuthToken();

        textViewModules.setVisibility(View.VISIBLE);
        textViewModules.setText("Cargando módulos...");
        recyclerView.setVisibility(View.GONE);

        apiModulesService.getModules(farmId, authToken).enqueue(new Callback<ListModuleResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListModuleResponse> call, @NonNull Response<ListModuleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Module> modules = response.body().getAllModules();
                    if (modules != null && !modules.isEmpty()) {
                        originalModulesList = new ArrayList<>(modules);
                        modulesList = new ArrayList<>(modules);
                        moduleAdapter.setModuleList(modulesList);
                        textViewModules.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        showError("No se encontraron módulos");
                    }
                } else {
                    showError("Error al cargar los módulos: " + 
                        (response.errorBody() != null ? response.errorBody().toString() : "Error desconocido"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListModuleResponse> call, @NonNull Throwable t) {
                showError("Error de conexión: " + t.getLocalizedMessage());
                Log.e("API_ERROR", "Error loading modules", t);
            }
        });
    }

    private void showError(String message) {
        textViewModules.setVisibility(View.VISIBLE);
        textViewModules.setText(message);
        recyclerView.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void filterModules(String query) {
        if (originalModulesList == null) return;

        if (query.isEmpty()) {
            moduleAdapter.setModuleList(originalModulesList);
            return;
        }

        List<Module> filteredList = new ArrayList<>();
        for (Module module : originalModulesList) {
            if (module.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(module);
            }
        }
        moduleAdapter.setModuleList(filteredList);
    }

    private void sortModules() {
        List<Module> currentList = new ArrayList<>(moduleAdapter.getModuleList());
        if (isAscendingOrder) {
            Collections.sort(currentList, (m1, m2) -> {
                Date date1 = m1.getCreatedAt();
                Date date2 = m2.getCreatedAt();
                if (date1 == null && date2 == null) return 0;
                if (date1 == null) return -1;
                if (date2 == null) return 1;
                return date1.compareTo(date2);
            });
        } else {
            Collections.sort(currentList, (m1, m2) -> {
                Date date1 = m1.getCreatedAt();
                Date date2 = m2.getCreatedAt();
                if (date1 == null && date2 == null) return 0;
                if (date1 == null) return 1;
                if (date2 == null) return -1;
                return date2.compareTo(date1);
            });
        }
        moduleAdapter.setModuleList(currentList);
    }
}

