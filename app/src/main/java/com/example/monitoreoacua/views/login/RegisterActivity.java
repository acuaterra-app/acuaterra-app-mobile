package com.example.monitoreoacua.views.login;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.models.request.RegisterRequest;
import com.example.monitoreoacua.models.response.RegisterResponse;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiUsersService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText etNombre, etEmail, etNDI,etSede, etFicha, etJornada, etPrograma, etPassword, etPasswordVerify;
    private Button btnRegistrar;
    private int rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etNDI = findViewById(R.id.etNDI);
        etSede = findViewById(R.id.etSede);
        etFicha = findViewById(R.id.etFicha);
        etJornada = findViewById(R.id.etJornada);
        etPrograma = findViewById(R.id.etPrograma);
        etPassword = findViewById(R.id.etPassword);
        etPasswordVerify = findViewById(R.id.etPasswordVerify);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        // Obtiene los valores de los campos de entrada y los elimina de espacios en blanco
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String ndi = etNDI.getText().toString().trim();
        String sede = etSede.getText().toString().trim();
        String ficha = etFicha.getText().toString().trim();
        String jornada = etJornada.getText().toString().trim();
        String programa = etPrograma.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String passwordVerify = etPasswordVerify.getText().toString().trim(); // Cambié a minúscula para seguir la convención
        rol = 3; // Asigna un rol fijo (puedes cambiarlo según tu lógica)

        // Verifica si hay campos obligatorios vacíos
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || ndi.isEmpty() || sede.isEmpty() || ficha.isEmpty() || jornada.isEmpty() || programa.isEmpty() || passwordVerify.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return; // Sale del método si hay campos vacíos
        }

        // Verifica si las contraseñas coinciden
        if (!password.equals(passwordVerify)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return; // Sale del método si las contraseñas no coinciden
        }

        // Configura Retrofit para realizar la solicitud de registro
        ApiUsersService apiService = ApiClient.getClient().create(ApiUsersService.class);
        RegisterRequest registerRequest = new RegisterRequest(nombre, email, password, ndi, sede, rol, ficha, jornada, programa);

        // Realiza la solicitud de registro
        apiService.register(registerRequest).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                // Verifica si la respuesta fue exitosa
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterResponse> call, Throwable t) {
                // Maneja el caso en que la solicitud falla
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}