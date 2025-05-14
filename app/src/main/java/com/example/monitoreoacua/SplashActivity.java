package com.example.monitoreoacua;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.views.MainActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int DURACION_SPLASH = 3000; // 3 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logoImage);
        TextView welcomeText = findViewById(R.id.welcomeText);

        // Cargar las animaciones desde los archivos XML
        // Opción 1: Usar animaciones individuales
        // Animation animLogo = AnimationUtils.loadAnimation(this, R.anim.logo_slide_up);
        // Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        // Animation animBounce = AnimationUtils.loadAnimation(this, R.anim.scale_bounce);
        
        // Opción 2: Usar conjuntos de animaciones predefinidos (recomendado)
        Animation logoAnimSet = AnimationUtils.loadAnimation(this, R.anim.logo_animation_set);
        Animation textAnimSet = AnimationUtils.loadAnimation(this, R.anim.text_combination);
        
        // Iniciar las animaciones
        logo.startAnimation(logoAnimSet);
        welcomeText.startAnimation(textAnimSet);


        // Retrasar el inicio de la actividad principal
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Cierra esta actividad para que no se pueda volver atrás
        }, DURACION_SPLASH);
    }
}

