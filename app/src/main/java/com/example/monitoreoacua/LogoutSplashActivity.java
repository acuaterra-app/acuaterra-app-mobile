package com.example.monitoreoacua;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.views.login.LoginActivity;

/**
 * Splash screen shown when logging out.
 * Displays a goodbye message and animations before redirecting to the login screen.
 */
public class LogoutSplashActivity extends AppCompatActivity {

    private static final int DURACION_SPLASH = 3500; // 3.5 segundos (un poco más largo pero no demasiado)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_splash);

        ImageView logo = findViewById(R.id.logoImageLogout);
        TextView goodbyeText = findViewById(R.id.goodbyeText);
        TextView goodbyeSubText = findViewById(R.id.goodbyeSubText);

        // Cargar las animaciones para el cierre de sesión
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_fade_out);
        Animation textAnimation = AnimationUtils.loadAnimation(this, R.anim.text_fade_out);
        
        // Iniciar las animaciones
        logo.startAnimation(logoAnimation);
        goodbyeText.startAnimation(textAnimation);
        goodbyeSubText.startAnimation(textAnimation);

        // Retrasar el inicio de la actividad de login
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LogoutSplashActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Cierra esta actividad para que no se pueda volver atrás
        }, DURACION_SPLASH);
    }
}

