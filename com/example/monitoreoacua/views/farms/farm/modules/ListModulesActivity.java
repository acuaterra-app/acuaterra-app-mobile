package com.example.monitoreoacua.views.farms.farm.modules;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.adapters.ModuleAdapter;
import com.example.monitoreoacua.models.Module;
import com.example.monitoreoacua.utils.NavigationHelper;
import com.example.monitoreoacua.views.LoginActivity;
import com.example.monitoreoacua.views.SupportActivity;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;
import com.example.monitoreoacua.views.users.ListUsersActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListModulesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ModuleAdapter moduleAdapter;
    private TextView textViewModules;
    private EditText editTextSearch;
    private AppCompatImageButton buttonSort;
    private boolean isAscendingOrder = true;
    private List<Module> modulesList = new ArrayList<>();
    private List<Module> originalModulesList = new ArrayList<>();
    private View navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules_list);

        initializeViews();
        setupRecyclerView();
        setupNavigation();
        setupSearchAndSort();

        int farmId = getIntent().getIntExtra("farmId", -1);
        if (farmId != -1) {
            loadModules(farmId);
        } else {
            showError("Error: ID de granja no proporcionado");
        }
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewModules);
        textViewModules = findViewById(R.id.textViewModules);
        editTextSearch = findViewById(R.id.editTextSearchModule);
        buttonSort = findViewById(R.id.buttonSortModules);
        navigationView = findViewById(R.id.bottomNav);
    }

    private void setupNavigation() {
        NavigationHelper.setupNavigation(navigationView, itemId -> {
            Intent intent = null;
            switch (itemId) {
                case R.id.navFarms:
                    intent = new Intent(this, ListFarmsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.navUsers:
                    intent = new Intent(this, ListUsersActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.navSupport:
                    intent = new Intent(this, SupportActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.navClose:
                    intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;
            }
        });
    }

    private void setupSearchAndSort() {
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

        buttonSort.setOnClickListener(v -> {
            isAscendingOrder = !isAscendingOrder;
            sortModules();
            buttonSort.setRotation(isAscendingOrder ? 0 : 180);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        moduleAdapter = new ModuleAdapter(modulesList);
        recyclerView.setAdapter(moduleAdapter);
    }

    private void loadModules(int farmId) {
        // TODO: Implementar carga de módulos desde la base de datos
        modulesList.clear();
        originalModulesList.clear();
        
        // Datos de ejemplo
        modulesList.add(new Module(1, "Módulo 1", "Activo", "2024-03-23"));
        modulesList.add(new Module(2, "Módulo 2", "Inactivo", "2024-03-22"));
        
        originalModulesList.addAll(modulesList);
        moduleAdapter.notifyDataSetChanged();
    }

    private void filterModules(String query) {
        List<Module> filteredList = new ArrayList<>();
        for (Module module : originalModulesList) {
            if (module.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(module);
            }
        }
        modulesList.clear();
        modulesList.addAll(filteredList);
        moduleAdapter.notifyDataSetChanged();
    }

    private void sortModules() {
        Collections.sort(modulesList, (m1, m2) -> {
            int result = m1.getCreatedAt().compareTo(m2.getCreatedAt());
            return isAscendingOrder ? result : -result;
        });
        moduleAdapter.notifyDataSetChanged();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (navigationView != null) {
            NavigationHelper.updateSelectedItem(navigationView, R.id.navFarms);
        }
    }
}

