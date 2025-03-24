package com.example.monitoreoacua.views.menu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.monitoreoacua.R;
import com.example.monitoreoacua.views.farms.ListFarmsActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

public class SupportActivity extends AppCompatActivity {

    private Button btnWhatsApp, btnEmail;
    // Declaration of navigation bar elements
    private AppCompatImageButton navHome, navSettings, navProfile, navCloseSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soporte);

        // Initialize navigation bar elements
        navHome = findViewById(R.id.navHome);
        // navSettings = findViewById(R.id.navSettings);
        navProfile = findViewById(R.id.navProfile);
        navCloseSesion = findViewById(R.id.navCloseSesion);

        // Events for the navigation bar
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(SupportActivity.this, ListFarmsActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SupportActivity.this, SupportActivity.class);
            startActivity(intent);
        });

        navCloseSesion.setOnClickListener(v -> {
            Intent intent = new Intent(SupportActivity.this, CloseSectionActivity.class);
            startActivity(intent);
        });

        // Link the buttons with their IDs in the XML
        btnWhatsApp = findViewById(R.id.btnWhatsApp);
        btnEmail = findViewById(R.id.btnEmail);

        // Open WhatsApp on click
        btnWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsApp("+573001234567"); // Change this number to the actual support number
            }
        });

        // Send an email on click
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCorreo("soporte@acuaterratech.com", "Consulta de soporte", "Hola, necesito ayuda con...");
            }
        });
    }

    // Method to open WhatsApp with a specific number
    private void openWhatsApp(String numberPhone) {
        try {
            String url = "https://wa.me/" + numberPhone.replace("+", "").replace(" ", "");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to open the email client with pre-filled data
    private void sendCorreo(String destinatario, String asunto, String mensaje) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // Solo las apps de correo pueden manejar esto
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{destinatario});
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        intent.putExtra(Intent.EXTRA_TEXT, mensaje);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
