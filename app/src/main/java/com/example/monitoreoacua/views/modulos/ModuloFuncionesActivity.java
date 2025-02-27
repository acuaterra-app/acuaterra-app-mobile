package com.example.monitoreoacua.views.modulos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;

public class ModuloFuncionesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_funciones_modulo);

        // Obtener referencia del ImageView
        ImageView userImage = findViewById(R.id.userImage);

        // Agregar un evento de clic al ImageView
        userImage.setOnClickListener(view -> {
            // Crear un Intent para abrir modulo1Activity
            Intent intent = new Intent(ModuloFuncionesActivity.this, ModuloInfoActivity.class);
            startActivity(intent);
        });
    }
}