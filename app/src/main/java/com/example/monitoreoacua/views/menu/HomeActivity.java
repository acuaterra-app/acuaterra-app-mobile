package com.example.monitoreoacua.views.menu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.views.pruebas.PruebaBitacora;
import com.example.monitoreoacua.views.pruebas.PruebaJson;
import com.example.monitoreoacua.views.soportetec.soporteActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Referenciar el botón "Cerrar Sesión"
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        Button btnModulos = findViewById(R.id.btnModulo);
        Button btnGuia = findViewById(R.id.btnGuia);
        Button btnSoporte = findViewById(R.id.btnSoporte);



        // Agregar funcionalidad al botón cerrarsesion
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar un cuadro de diálogo de confirmación
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Cerrar sesión")
                        .setMessage("¿Estás seguro de que deseas salir de la aplicación?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Cerrar la aplicación
                                finishAffinity(); // Cierra todas las actividades relacionadas
                            }
                        })
                        .setNegativeButton("No", null) // No hacer nada si se presiona "No"
                        .show();
            }
        });

        //  Navegar a SoporteActivity
        btnSoporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, soporteActivity.class);
                startActivity(intent);
            }
        });

        btnModulos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a HomeModulosActivity
                //Intent intent = new Intent(HomeActivity.this, HomeModulosActivity.class);
                Intent intent = new Intent(HomeActivity.this, PruebaJson.class);

                startActivity(intent);
            }
        });

        btnGuia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a HomeModulosActivity
                //Intent intent = new Intent(HomeActivity.this, HomeModulosActivity.class);
                Intent intent = new Intent(HomeActivity.this, PruebaBitacora.class);

                startActivity(intent);
            }
        });

    }
}
