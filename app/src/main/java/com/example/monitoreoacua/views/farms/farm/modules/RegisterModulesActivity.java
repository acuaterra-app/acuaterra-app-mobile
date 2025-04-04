package com.example.monitoreoacua.views.farms.farm.modules;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Farm;
import com.example.monitoreoacua.views.BaseActivity;
public class RegisterModulesActivity extends BaseActivity {
    private Farm farm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Inflar el layout específico en el contenedor de fragmentos de BaseActivity
        FrameLayout fragmentContainer = findViewById(R.id.fragmentContainer);
        getLayoutInflater().inflate(R.layout.activity_register_modules, fragmentContainer, true);
        
        // Get farm object from intent
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            farm = getIntent().getParcelableExtra("farm", Farm.class);
        } else {
            farm = (Farm) getIntent().getParcelableExtra("farm");
        }
        if (farm == null) {
            Toast.makeText(this, "No se proporcionó información de la granja", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if farm is missing
            return;
        }
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    
    @Override
    protected String getActivityTitle() {
        return "Registrar Módulos";
    }
}
