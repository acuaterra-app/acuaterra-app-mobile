package com.example.monitoreoacua.views.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.models.request.LoginRequest;
import com.example.monitoreoacua.models.response.LoginResponse;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiUsersService;
import com.example.monitoreoacua.views.menu.HomeActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private int contadorIntentos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);


        btnLogin.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validar campos obligatorios
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar formato del email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor, introduce un email válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contadorIntentos >= 3) {
            btnLogin.setEnabled(false);
            Toast.makeText(this, "Acceso bloqueado por múltiples intentos fallidos", Toast.LENGTH_LONG).show();
            return;
        }

        // Configurar Retrofit
        ApiUsersService apiUserService = ApiClient.getClient().create(ApiUsersService.class);
        LoginRequest loginRequest = new LoginRequest(email, password);

        btnLogin.setEnabled(false); // Deshabilitar botón mientras se procesa
        apiUserService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnLogin.setEnabled(true); // Rehabilitar botón
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    token = token.trim();
                    String nombreUsuario = response.body().getUser().getNombre();
                    Toast.makeText(LoginActivity.this, "Bienvenido, " + nombreUsuario, Toast.LENGTH_SHORT).show();
                    contadorIntentos = 0;

                    // Guardar el token de manera persistente, eliminando cualquier token previo
                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    //editor.clear(); // Elimina cualquier dato previo guardado
                    editor.putString("token", token);
                    //editor.putInt("user_id", response.body().getUser().getId());
                    editor.apply();


                    // Recuperar el token
                    String storedToken = sharedPreferences.getString("token", "No token found");

                    // Usar un Handler para crear un retraso de 3 segundos
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Mostrar el token en un Toast después de 3 segundos
                            Toast.makeText(getApplicationContext(), storedToken, Toast.LENGTH_LONG).show();
                        }
                    }, 1000); // 3000 milisegundos = 3 segundos



                    // Redirigir al HomeActivity
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish(); // Evitar regresar al login
                } else {
                    contadorIntentos++;
                    Toast.makeText(LoginActivity.this, "Credenciales inválidas. Intento " + contadorIntentos + " de 3", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnLogin.setEnabled(true); // Rehabilitar botón
                Toast.makeText(LoginActivity.this, "Error al conectar con el servidor: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
