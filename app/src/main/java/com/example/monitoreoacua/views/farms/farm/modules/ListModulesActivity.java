package com.example.monitoreoacua.views.farms.farm.modules;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import java.util.Date;

import androidx.activity.EdgeToEdge;
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
import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiModulesService;
import com.example.monitoreoacua.service.request.ListModulesRequest;
import com.example.monitoreoacua.service.response.ListModuleResponse;
import com.example.monitoreoacua.utils.NavigationHelper;

import java.util.ArrayList;
import java.util.Collections;
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modules_list);

        Log.d("ModulesListHome", "Activity started");
        Toast.makeText(this, "Bienvenido a Módulos", Toast.LENGTH_SHORT).show();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize navigation
        navigationContainer = findViewById(R.id.bottomNav);
        setupNavigation();

        // Initialize UI elements
        textViewModules = findViewById(R.id.textViewModules);
        recyclerView = findViewById(R.id.recyclerViewModules);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        moduleAdapter = new ModuleAdapter();
        recyclerView.setAdapter(moduleAdapter);

        // Get farm ID and load modules
        int farmId = getIntent().getIntExtra("farmId", -1);
        Toast.makeText(this, "Id Farm: " + farmId, Toast.LENGTH_SHORT).show();
        if (farmId != -1) {
            loadModules(farmId);
        } else {
            Toast.makeText(this, "Error al obtener la granja", Toast.LENGTH_SHORT).show();
        }

        // Initialize UI buttons
        buttonAddModule = findViewById(R.id.buttonAddModule);
        
        // Initialize search and sort UI elements
        editTextSearch = findViewById(R.id.editTextSearchModule);
        buttonSort = findViewById(R.id.buttonSortModules);

        // Set up search text change listener
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

        // Set up sort button click listener
        buttonSort.setOnClickListener(v -> {
            isAscendingOrder = !isAscendingOrder;
            sortModules();
            buttonSort.setRotation(isAscendingOrder ? 0 : 180);
        });

        // Set button click listeners
        buttonAddModule.setOnClickListener(v -> {
            //int farmId = getIntent().getIntExtra("farmId", -1);
            if (farmId != -1) {
                Intent intent = new Intent(ListModulesActivity.this, RegisterModulesActivity.class);
                intent.putExtra("farmId", farmId);  // Pass the farmId to the registration activity
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error: ID de granja no proporcionado", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Sets up the bottom navigation using NavigationHelper
     */
    private void setupNavigation() {
        NavigationHelper.setupNavigation(navigationContainer, 
            itemId -> NavigationHelper.navigateToSection(this, itemId, ListModulesActivity.class));

    }

    /**
     * Loads the modules for the specified farm.
     * @param farmId The ID of the farm to load modules for.
     */
    private void loadModules(int farmId) {
        ApiModulesService apiModulesService = ApiClient.getClient().create(ApiModulesService.class);
        ListModulesRequest  listModulesRequest = new ListModulesRequest();
        String authToken = listModulesRequest.getAuthToken();
        apiModulesService.getModules(farmId, authToken).enqueue(new Callback<ListModuleResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListModuleResponse> call, @NonNull Response<ListModuleResponse> response) {
                Log.d("API_RESPONSE", "On response: " + response);
                textViewModules.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    ListModuleResponse listModuleResponse = response.body();
                    List<Module> modules = listModuleResponse != null ? listModuleResponse.getAllModules() : null;

                    if (modules != null && !modules.isEmpty()) {
                        originalModulesList = new ArrayList<>(modules);
                        modulesList = new ArrayList<>(modules);
                        moduleAdapter.setModuleList(modulesList);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        textViewModules.setVisibility(View.VISIBLE);
                        textViewModules.setText("No se encontraron módulos");
                        recyclerView.setVisibility(View.GONE);
                        Toast.makeText(ListModulesActivity.this, "No se encontraron módulos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ListModulesActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListModuleResponse> call, @NonNull Throwable t) {
                Toast.makeText(ListModulesActivity.this, "Error de conexión: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }

    /**
     * Filters modules based on a search query string
     * @param query The search text to filter by
     */
    private void filterModules(String query) {
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

    /**
     * Sorts the modules list by creation date
     * Toggles between ascending and descending order
     */
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
