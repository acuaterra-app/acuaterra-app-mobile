package com.example.monitoreoacua.views.farms.farm.modules;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiModulesService;
import com.example.monitoreoacua.service.request.ListFarmsRequest;
import com.example.monitoreoacua.service.request.ListModulesRequest;
import com.example.monitoreoacua.service.response.ListFarmResponse;
import com.example.monitoreoacua.service.response.ListModuleResponse;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;
import com.example.monitoreoacua.views.menu.LogoutActivity;
import com.example.monitoreoacua.views.menu.SupportActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListModulesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ModuleAdapter moduleAdapter;
    private TextView textViewModules;
    private List<Module> modulesList = new ArrayList<>();

    // Navigation bar elements
    private AppCompatImageButton navHome, navProfile, navCloseSesion;
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

        // Initialize navigation buttons
        buttonAddModule = findViewById(R.id.buttonAddModule);
        navHome = findViewById(R.id.navHome);
        navProfile = findViewById(R.id.navProfile);
        navCloseSesion = findViewById(R.id.navCloseSesion);

        // Set button click listeners
        buttonAddModule.setOnClickListener(v -> {
            Intent intent = new Intent(ListModulesActivity.this, RegisterModulesActivity.class);
            startActivity(intent);
        });

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(ListModulesActivity.this, ListFarmsActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ListModulesActivity.this, SupportActivity.class);
            startActivity(intent);
        });

        navCloseSesion.setOnClickListener(v -> {
            Intent intent = new Intent(ListModulesActivity.this, LogoutActivity.class);
            startActivity(intent);
        });
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
                        modulesList = new ArrayList<>(modules);
                        moduleAdapter.setModuleList(modulesList);
                    } else {
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

}
