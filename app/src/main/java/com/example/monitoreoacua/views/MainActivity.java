package com.example.monitoreoacua.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.views.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    // Usar un Handler para retrasar la transición a RegisterActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Crear el intent para navegar a RegisterActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent); // Iniciar RegisterActivity
                finish(); // Opcional: cerrar MainActivity para que no se pueda volver a ella
            }
        }, 3000); // Retraso de 2000 milisegundos (2 segundos)
    }

}