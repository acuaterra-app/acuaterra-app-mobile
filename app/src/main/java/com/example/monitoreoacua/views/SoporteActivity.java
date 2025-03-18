package com.example.monitoreoacua.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.monitoreoacua.R;
import androidx.appcompat.app.AppCompatActivity;

public class SoporteActivity extends AppCompatActivity {

    private Button btnWhatsApp, btnEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soporte);

        // Vincular los botones con sus IDs en el XML
        btnWhatsApp = findViewById(R.id.btnWhatsApp);
        btnEmail = findViewById(R.id.btnEmail);

        // Abrir WhatsApp al hacer clic
        btnWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirWhatsApp("+573001234567"); // Cambia este número por el real del soporte
            }
        });

        // Enviar un correo al hacer clic
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarCorreo("soporte@acuaterratech.com", "Consulta de soporte", "Hola, necesito ayuda con...");
            }
        });
    }

    // Método para abrir WhatsApp con un número específico
    private void abrirWhatsApp(String telefono) {
        try {
            String url = "https://wa.me/" + telefono.replace("+", "").replace(" ", "");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para abrir el cliente de correo con los datos prellenados
    private void enviarCorreo(String destinatario, String asunto, String mensaje) {
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
