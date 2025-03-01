package com.example.monitoreoacua.views.soportetec;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;

public class soporteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soporte);

        // Referenciar botones
        Button btnWhatsApp = findViewById(R.id.btnWhatsApp);
        Button btnEmail = findViewById(R.id.btnEmail);

        // Configurar click listeners
        btnWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsApp();
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    // Método para abrir WhatsApp
    private void openWhatsApp() {
        try {
            String phoneNumber = "573001234567";
            Uri uri = Uri.parse("https://wa.me/" + phoneNumber);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para enviar un correo electrónico
    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:soporte@example.com")); // Correo de soporte
        intent.putExtra(Intent.EXTRA_SUBJECT, "Soporte AcuaTerra");
        intent.putExtra(Intent.EXTRA_TEXT, "Hola, necesito ayuda con...");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
