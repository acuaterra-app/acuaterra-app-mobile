package com.example.monitoreoacua.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.monitoreoacua.R;

/**
 * Fragment for displaying and managing support options (WhatsApp and Email)
 */
public class SupportFragment extends Fragment {

    private Button btnWhatsApp, btnEmail;
    private static final String TAG = "SupportFragment";

    public SupportFragment() {
        // Required empty public constructor
    }


    public static SupportFragment newInstance() {
        return new SupportFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_support, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnWhatsApp = view.findViewById(R.id.btnWhatsApp);
        btnEmail = view.findViewById(R.id.btnEmail);

        btnWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall("3167845095");
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCorreo("acuaterra@gmail.com", "Consulta de soporte", "Hola, necesito ayuda con...");
            }
        });
    }

    private void makePhoneCall(String phoneNumber) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        } catch (Exception e) {
            Log.e(TAG, "No se pudo realizar la llamada: " + e.getMessage());
        }
    }

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

    private void sendCorreo(String destinatario, String asunto, String mensaje) {
        Context context = getContext();
        if (context == null) {
            Log.e(TAG, "Context is null");
            return;
        }

        try {
            // Crear intent de email que funcione con Gmail y otras apps
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + destinatario));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{destinatario});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, asunto);
            emailIntent.putExtra(Intent.EXTRA_TEXT, mensaje);

            // Verificar si hay apps de email disponibles
            if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
                startActivity(emailIntent);
            } else {
                // Fallback a chooser si no hay apps de email específicas
                Intent fallbackIntent = new Intent(Intent.ACTION_SEND);
                fallbackIntent.setType("message/rfc822");
                fallbackIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{destinatario});
                fallbackIntent.putExtra(Intent.EXTRA_SUBJECT, asunto);
                fallbackIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
                
                startActivity(Intent.createChooser(fallbackIntent, "Enviar correo con:"));
            }
        } catch (Exception e) {
            Log.e(TAG, "No se pudo abrir ninguna aplicación de correo: " + e.getMessage());
        }
    }
}

