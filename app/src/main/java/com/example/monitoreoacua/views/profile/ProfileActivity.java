package com.example.monitoreoacua.views.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.utils.SharedPreferencesKeys;
import com.example.monitoreoacua.views.BaseActivity;

/**
 * Actividad que muestra la información del perfil de usuario
 * Incluye datos como nombre, email, DNI y rol del usuario
 */
public class ProfileActivity extends BaseActivity {

    private static final String TAG = "ProfileActivity";
    
    private TextView tvUserName;
    private TextView tvUserEmail;
    private TextView tvUserDni;
    private TextView tvUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // El layout base ya está configurado en BaseActivity
        getLayoutInflater().inflate(R.layout.activity_profile, findViewById(R.id.fragmentContainer));

        // Inicializar vistas
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserDni = findViewById(R.id.tvUserDni);
        tvUserRole = findViewById(R.id.tvUserRole);

        // Cargar datos del usuario desde SharedPreferences
        loadUserData();
    }

    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesKeys.PREFS_NAME, MODE_PRIVATE);
        
        // Verificar los datos
        Log.d(TAG, "Verificando datos en SharedPreferences");
        String name = sharedPreferences.getString(SharedPreferencesKeys.KEY_USER_NAME, null);
        String email = sharedPreferences.getString(SharedPreferencesKeys.KEY_USER_EMAIL, null);
        String dni = sharedPreferences.getString(SharedPreferencesKeys.KEY_USER_DNI, null);
        String role = sharedPreferences.getString(SharedPreferencesKeys.KEY_USER_ROLE, null);
        
        Log.d(TAG, "user_name: " + (name != null ? name : "No disponible"));
        Log.d(TAG, "user_email: " + (email != null ? email : "No disponible"));
        Log.d(TAG, "user_dni: " + (dni != null ? dni : "No disponible"));
        Log.d(TAG, "user_role: " + (role != null ? role : "No disponible"));
        
        tvUserName.setText(name != null ? name : "No disponible");
        tvUserEmail.setText(email != null ? email : "No disponible");
        tvUserDni.setText(dni != null ? dni : "No disponible");
        tvUserRole.setText(role != null ? role : "No disponible");
        
        if (name == null && email == null && dni == null && role == null) {
            Log.w(TAG, "No se encontraron datos de usuario en SharedPreferences");
        }
    }

    @Override
    protected String getActivityTitle() {
        return "Mi Perfil";
    }
    
    @Override
    protected void loadInitialFragment() {
        // No es necesario cargar un fragmento inicial, ya que esta actividad 
        // utiliza su propio layout inflado en el fragmentContainer
    }
}

