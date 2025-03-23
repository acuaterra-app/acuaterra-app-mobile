package com.example.monitoreoacua.views.farms.farm.modules;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiModulesService;
import com.example.monitoreoacua.service.request.ListModulesRequest;
import com.example.monitoreoacua.service.response.ListModuleResponse;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;
import com.example.monitoreoacua.views.menu.CloseSessionActivity;
import com.example.monitoreoacua.views.menu.SupportActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListModulesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ModuleAdapter moduleAdapter;
    private ProgressBar progressBar;
    private ApiModulesService apiModulesService;

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
        recyclerView = findViewById(R.id.recyclerViewModules);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        moduleAdapter = new ModuleAdapter();
        recyclerView.setAdapter(moduleAdapter);

        ApiModulesService apiModulesService = ApiClient.getClient().create(ApiModulesService.class);

        // Get farm ID and load modules
        int farmId = getIntent().getIntExtra("FARM_ID", -1);
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
            Intent intent = new Intent(ListModulesActivity.this, CloseSessionActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Loads the modules for the specified farm.
     * @param farmId The ID of the farm to load modules for.
     */
    private void loadModules(int farmId) {
        String token = "Bearer " + new ListModulesRequest().getAuthToken();

        apiModulesService.getModules(farmId, token).enqueue(new Callback<ListModuleResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListModuleResponse> call, @NonNull Response<ListModuleResponse> response) {
                Log.d("API_RESPONSE", "On response: " + response);
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Module> modules = response.body().getAllModules();

                    if (modules != null && !modules.isEmpty()) {
                        moduleAdapter.setModuleList(modules);
                    } else {
                        Toast.makeText(ListModulesActivity.this, "No se encontraron módulos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ListModulesActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListModuleResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ListModulesActivity.this, "Error de conexión: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }

}
