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
                openWhatsApp("+573001234567"); // Change this number to the actual support number
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCorreo("soporte@acuaterratech.com", "Consulta de soporte", "Hola, necesito ayuda con...");
            }
        });
        return view;
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
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // Solo las apps de correo pueden manejar esto
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{destinatario});
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        intent.putExtra(Intent.EXTRA_TEXT, mensaje);

        Context context = getContext();
        if (context != null && intent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.e(TAG, "No email app found or context is null");
        }
    }
}

