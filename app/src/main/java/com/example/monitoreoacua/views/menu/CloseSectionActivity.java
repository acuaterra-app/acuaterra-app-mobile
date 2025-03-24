package com.example.monitoreoacua.views.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.views.login.LoginActivity; // Asegúrate de importar la actividad de inicio de sesión

public class CloseSectionActivity extends AppCompatActivity {

    private Button buttonConfirmLogout, buttonCancelLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_closesection);

        // Configura el padding para la barra de sistema (EdgeToEdge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener referencias a los botones
        buttonConfirmLogout = findViewById(R.id.buttonConfirmLogout);
        buttonCancelLogout = findViewById(R.id.buttonCancelLogout);

        // Configurar el listener para el botón "Cerrar sesión"
        buttonConfirmLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        // Configurar el listener para el botón "Cancelar"
        buttonCancelLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarCerrarSesion();
            }
        });
    }

    // Método para cerrar sesión
    private void cerrarSesion() {


        // Redirigir al usuario a la pantalla de inicio de sesión
        Intent intent = new Intent(CloseSectionActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpiar el stack de actividades
        startActivity(intent);
        finish(); // Finalizar la actividad actual
    }

    // Método para cancelar el cierre de sesión
    private void cancelarCerrarSesion() {
        // Simplemente finaliza la actividad actual para regresar a la pantalla anterior
        finish();
    }
}