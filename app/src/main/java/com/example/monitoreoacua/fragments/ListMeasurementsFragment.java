package com.example.monitoreoacua.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Measurement;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiMeasurementsService;
import com.example.monitoreoacua.service.request.ListMeasurementRequest;
import com.example.monitoreoacua.service.response.ListMeasurementResponse;
import com.example.monitoreoacua.utils.ApplicationContextProvider;
import com.example.monitoreoacua.views.measurements.MeasurementsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;

import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment for displaying a list of measurements
 * Based on the pattern of ListModulesFragment
 */
public class ListMeasurementsFragment extends Fragment implements MeasurementsAdapter.OnMeasurementClickListener {

    private static final String TAG = "ListMeasurementsFragment";

    private RecyclerView recyclerViewMeasurements;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvEmptyView;
    private ProgressBar progressBar;
    private MeasurementsAdapter measurementAdapter;

    private String moduleId;

    private OnMeasurementInteractionListener listener;

    /**
     * Interface for handling measurement interactions
     */
    public interface OnMeasurementInteractionListener {
        void onMeasurementSelected(Measurement measurement);
    }

    /**
     * Create a new instance of ListMeasurementsFragment with moduleId
     *
     * @param moduleId The ID of the module to fetch measurements for
     * @return A new instance of ListMeasurementsFragment
     */
    public static ListMeasurementsFragment newInstance(String moduleId) {
        ListMeasurementsFragment fragment = new ListMeasurementsFragment();
        Bundle args = new Bundle();
        args.putString("moduleId", moduleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            moduleId = getArguments().getString("moduleId");
            Log.d(TAG, "moduleId: " + moduleId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_measurements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerViewMeasurements = view.findViewById(R.id.recyclerViewMeasurements);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        tvEmptyView = view.findViewById(R.id.tvEmptyView);

        // Set up RecyclerView
        // Set up RecyclerView
        recyclerViewMeasurements.setLayoutManager(new LinearLayoutManager(getContext()));
        measurementAdapter = new MeasurementsAdapter();
        measurementAdapter.setOnMeasurementClickListener(this);
        recyclerViewMeasurements.setAdapter(measurementAdapter);
        // Set up SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::fetchMeasurements);

        // Verify token before fetching measurements
        verifyTokenAndFetchMeasurements();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnMeasurementInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnMeasurementInteractionListener");
        }
    }

    @Override
    public void onMeasurementClick(Measurement measurement) {
        if (listener != null) {
            listener.onMeasurementSelected(measurement);
        }
    }

    /**
     * Verify token before fetching measurements
     * This helps identify token related issues early
     */
    private void verifyTokenAndFetchMeasurements() {
        // Create request to get token
        ListMeasurementRequest listMeasurementsRequest = new ListMeasurementRequest();
        String authToken = listMeasurementsRequest.getAuthToken();

        // Log token details for debugging
        if (authToken == null) {
            Log.e(TAG, "PROBLEMA DE AUTENTICACIÓN: Token es NULL");
            showError("Error de autenticación: token no encontrado. Por favor, inicie sesión nuevamente.");
            return;
        } else if (authToken.isEmpty()) {
            Log.e(TAG, "PROBLEMA DE AUTENTICACIÓN: Token está vacío");
            showError("Error de autenticación: token vacío. Por favor, inicie sesión nuevamente.");
            return;
        } else {
            // Check token format (should start with Bearer)
            if (!authToken.trim().startsWith("Bearer ") && !authToken.trim().startsWith("bearer ")) {
                // Not a critical error, just add "Bearer " prefix
                Log.w(TAG, "Token no tiene el prefijo 'Bearer'. Se añadirá automáticamente.");
                authToken = "Bearer " + authToken;
            }
            
            // Get token length and show first/last few characters for debugging
            int tokenLength = authToken.length();
            String tokenPreview = (tokenLength > 15) 
                ? authToken.substring(0, 7) + "..." + authToken.substring(tokenLength - 7)
                : authToken;
            
            Log.d(TAG, "Token verificado (long: " + tokenLength + "): " + tokenPreview);
            Log.d(TAG, "Continuando con la carga de mediciones...");
        }
        
        // If we get here, token exists and has been validated
        fetchMeasurements();
    }

    /**
     * Fetch measurements from the API
     */
    private void fetchMeasurements() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewMeasurements.setVisibility(View.GONE);
        tvEmptyView.setVisibility(View.GONE);

        // Validar moduleId antes de hacer la llamada
        if (moduleId == null || moduleId.isEmpty()) {
            showError("Error: ID del módulo no válido");
            Log.e(TAG, "Error: moduleId es nulo o vacío");
            return;
        }

        try {
            int moduleIdInt = Integer.parseInt(moduleId);
            
            // Crear API service
            ApiMeasurementsService apiService = ApiClient.getClient().create(ApiMeasurementsService.class);
            ListMeasurementRequest listMeasurementsRequest = new ListMeasurementRequest();
            
            // Obtener y verificar token
            String authToken = listMeasurementsRequest.getAuthToken();
            if (authToken == null || authToken.isEmpty()) {
                showError("Error: Token de autorización no válido");
                Log.e(TAG, "Error: token de autorización es nulo o vacío");
                return;
            }
            
            // Asegurarse que el token tenga el prefijo "Bearer"
            if (!authToken.trim().startsWith("Bearer ") && !authToken.trim().startsWith("bearer ")) {
                authToken = "Bearer " + authToken;
            }
            
            // Log para depuración exhaustiva
            Log.d(TAG, "==== INICIANDO PETICIÓN API ====");
            Log.d(TAG, "URL: /api/v2/module/measurement?moduleId=" + moduleIdInt);
            Log.d(TAG, "Método: GET");
            Log.d(TAG, "Token: " + (authToken.length() > 20 
                ? authToken.substring(0, 10) + "..." + authToken.substring(authToken.length() - 10)
                : authToken));
            Log.d(TAG, "Headers: Authorization: " + (authToken.startsWith("Bearer ") ? "Bearer ***" : "Sin prefijo Bearer"));
            Log.d(TAG, "QueryParams: moduleId=" + moduleIdInt);
            Log.d(TAG, "================================");
            
            // Hacer llamada a la API
            Call<ListMeasurementResponse> call = apiService.getMeasurements(
                    moduleIdInt,
                    authToken
            );
            
            // Guardar el tiempo de inicio para medir latencia
            call.request().tag(System.currentTimeMillis());
            
            call.enqueue(new Callback<ListMeasurementResponse>() {
                @Override
                public void onResponse(@NonNull Call<ListMeasurementResponse> call, @NonNull Response<ListMeasurementResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    
                    // Log de la respuesta completa
                    Log.d(TAG, "==== RESPUESTA API RECIBIDA ====");
                    Log.d(TAG, "URL: " + call.request().url());
                    Log.d(TAG, "Código HTTP: " + response.code() + " - " + response.message());
                    Log.d(TAG, "Headers: " + response.headers().toString());
                    Log.d(TAG, "Tiempo transcurrido: " + (System.currentTimeMillis() - call.request().tag()) + " ms");
                    Log.d(TAG, "================================");
                    
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Respuesta exitosa con cuerpo válido");
                        try {
                        ListMeasurementResponse measurementResponse = response.body();
                        List<Measurement> measurements = measurementResponse.getData();
                        
                        // Update UI based on response
                        if (measurements != null && !measurements.isEmpty()) {
                            Log.d(TAG, "Se recibieron " + measurements.size() + " mediciones");
                            measurementAdapter.setMeasurementList(measurements);
                            recyclerViewMeasurements.setVisibility(View.VISIBLE);
                            tvEmptyView.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "No se recibieron mediciones");
                            measurementAdapter.setMeasurementList(new ArrayList<>());
                            recyclerViewMeasurements.setVisibility(View.GONE);
                            tvEmptyView.setVisibility(View.VISIBLE);
                            tvEmptyView.setText(R.string.empty_measurements_list);
                        }
                        } catch (Exception e) {
                            Log.e(TAG, "Error procesando respuesta JSON", e);
                            showError("Error procesando datos: " + e.getMessage());
                        }
                    } else {
                        // Manejo específico de códigos de error
                        String errorMsg;
                        switch (response.code()) {
                            case 400:
                                errorMsg = "Error: Solicitud incorrecta. Verifica los parámetros.";
                                Log.e(TAG, "Error 400: Parámetros de la solicitud incorrectos");
                                break;
                            case 401:
                                errorMsg = "Error: No autorizado. Por favor inicia sesión nuevamente.";
                                Log.e(TAG, "Error 401: Token de autorización no válido");
                                break;
                            case 403:
                                errorMsg = "Error: Acceso denegado. No tienes permisos para ver estas mediciones.";
                                Log.e(TAG, "Error 403: Permisos insuficientes");
                                break;
                            case 404:
                                errorMsg = "Error: Módulo no encontrado. Verifica que el módulo exista.";
                                Log.e(TAG, "Error 404: Módulo " + moduleId + " no encontrado");
                                break;
                            case 500:
                                errorMsg = "Error: Problema en el servidor. Inténtalo más tarde.";
                                Log.e(TAG, "Error 500: Error interno del servidor");
                                break;
                            default:
                                errorMsg = "Error al obtener las mediciones. Código: " + response.code();
                                Log.e(TAG, "Error " + response.code() + ": " + response.message());
                                break;
                        }
                        
                        // Intentar obtener más detalles del cuerpo de error
                        try {
                            ResponseBody errorBody = response.errorBody();
                            if (errorBody != null) {
                                String errorContent = errorBody.string();
                                Log.e(TAG, "Cuerpo de error: " + errorContent);
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "No se pudo leer el cuerpo de error", e);
                        }
                        
                        Log.e(TAG, "Error al obtener las mediciones: " + response.toString());
                        showError(errorMsg);
                    }
                }
                
                @Override
                public void onFailure(@NonNull Call<ListMeasurementResponse> call, @NonNull Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    
                    // Mensajes específicos según el tipo de error
                    String errorMsg;
                    if (t instanceof java.net.UnknownHostException) {
                        errorMsg = "Error: No hay conexión a Internet o el servidor no está disponible.";
                        Log.e(TAG, "Error de host desconocido: No hay conexión a Internet", t);
                    } else if (t instanceof java.net.SocketTimeoutException) {
                        errorMsg = "Error: Tiempo de espera agotado. El servidor tardó demasiado en responder.";
                        Log.e(TAG, "Error de timeout: El servidor tardó demasiado en responder", t);
                    } else {
                        errorMsg = "Error de conexión: " + t.getMessage();
                        Log.e(TAG, "Error de conexión", t);
                    }
                    showError(errorMsg);
                }
            });
        } catch (NumberFormatException e) {
            showError("Error: El ID del módulo debe ser un número válido");
            Log.e(TAG, "Error al convertir moduleId a entero: " + moduleId, e);
        }
    }

    /**
     * Show error message and update UI
     *
     * @param message The error message to display
     */
    private void showError(String message) {
        if (getContext() != null) {
            // Mostrar toast con el mensaje de error específico
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            
            // Registrar el mensaje de error
            Log.e(TAG, "Error mostrado al usuario: " + message);
            
            // Actualizar UI
            recyclerViewMeasurements.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
            
            // Mostrar mensaje personalizado en lugar del genérico
            tvEmptyView.setText(message);
            
            // Agregar opción de depuración cuando hay errores
            Button debugButton = new Button(getContext());
            debugButton.setText("Diagnosticar Problema");
            debugButton.setOnClickListener(v -> runDiagnostics());
            
            // Añadir el botón debajo del mensaje de error
            if (tvEmptyView.getParent() instanceof ViewGroup) {
                ViewGroup parent = (ViewGroup) tvEmptyView.getParent();
                
                // Verificar si ya existe un botón de diagnóstico
                boolean buttonExists = false;
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View child = parent.getChildAt(i);
                    if (child.getId() == R.id.btnDiagnostics) {
                        buttonExists = true;
                        break;
                    }
                }
                
                if (!buttonExists) {
                    debugButton.setId(R.id.btnDiagnostics);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    parent.addView(debugButton, params);
                }
            }
        } else {
            Log.e(TAG, "No se pudo mostrar error: contexto es null. Mensaje: " + message);
        }
    }

    /**
     * Refresh measurements data
     * Called from outside the fragment to refresh the list
     */
    public void refreshMeasurements() {
        verifyTokenAndFetchMeasurements();
    }
    
    /**
     * Run comprehensive diagnostics on the measurement loading process
     * This method checks each possible point of failure
     */
    public void runDiagnostics() {
        if (getContext() == null) {
            Log.e(TAG, "DIAGNÓSTICO: Contexto del fragmento es NULL. No se puede continuar.");
            return;
        }
        
        StringBuilder report = new StringBuilder();
        report.append("=== REPORTE DE DIAGNÓSTICO ===\n\n");
        
        // 1. Verificar ApplicationContext
        report.append("1. VERIFICACIÓN DE CONTEXTO DE APLICACIÓN:\n");
        Context appContext = ApplicationContextProvider.getContext();
        if (appContext == null) {
            report.append("   ❌ ERROR: ApplicationContext es NULL\n");
            report.append("   - Posible causa: ApplicationContextProvider no está registrado en AndroidManifest.xml\n");
            report.append("   - O no se inicializó correctamente en Application.onCreate()\n");
        } else {
            report.append("   ✓ ApplicationContext inicializado correctamente\n");
        }
        report.append("\n");
        
        // 2. Verificar Módulo ID
        report.append("2. VERIFICACIÓN DE MÓDULO ID:\n");
        if (moduleId == null) {
            report.append("   ❌ ERROR: moduleId es NULL\n");
            report.append("   - Posible causa: No se pasó el ID al crear el fragmento\n");
        } else if (moduleId.isEmpty()) {
            report.append("   ❌ ERROR: moduleId está vacío\n");
            report.append("   - Posible causa: Se pasó un string vacío al crear el fragmento\n");
        } else {
            try {
                int moduleIdInt = Integer.parseInt(moduleId);
                report.append("   ✓ moduleId válido: ").append(moduleIdInt).append("\n");
            } catch (NumberFormatException e) {
                report.append("   ❌ ERROR: moduleId no es un número válido: \"").append(moduleId).append("\"\n");
                report.append("   - Posible causa: El ID pasado no es numérico\n");
            }
        }
        report.append("\n");
        
        // 3. Verificar Token
        report.append("3. VERIFICACIÓN DE TOKEN DE AUTENTICACIÓN:\n");
        if (appContext != null) {
            try {
                SharedPreferences prefs = appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                String token = prefs.getString("token", null);
                if (token == null) {
                    report.append("   ❌ ERROR: Token no encontrado en SharedPreferences\n");
                    report.append("   - Posible causa: Usuario no ha iniciado sesión o sesión expirada\n");
                } else if (token.isEmpty()) {
                    report.append("   ❌ ERROR: Token está vacío en SharedPreferences\n");
                    report.append("   - Posible causa: Token invalidado o cerrado de sesión\n");
                } else {
                    // Verificar formato del token (JWT básico)
                    if (!token.startsWith("Bearer ")) {
                        report.append("   ⚠️ ADVERTENCIA: Token no tiene prefijo 'Bearer '\n");
                        report.append("   - Esto se corrige automáticamente en la petición\n");
                    }
                    
                    String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
                    String[] parts = actualToken.split("\\.");
                    
                    if (parts.length != 3) {
                        report.append("   ⚠️ ADVERTENCIA: Token no parece tener formato JWT válido (debería tener 3 partes separadas por puntos)\n");
                    } else {
                        report.append("   ✓ Token encontrado y con formato aparentemente válido\n");
                        report.append("   - Longitud: ").append(token.length()).append(" caracteres\n");
                        // Mostrar inicio y fin para debugging
                        if (token.length() > 20) {
                            report.append("   - Primeros 10 caracteres: ")
                                  .append(token.substring(0, 10))
                                  .append("...\n");
                            report.append("   - Últimos 10 caracteres: ...")
                                  .append(token.substring(token.length() - 10))
                                  .append("\n");
                        }
                    }
                }
            } catch (Exception e) {
                report.append("   ❌ ERROR al acceder a SharedPreferences: ").append(e.getMessage()).append("\n");
            }
        } else {
            report.append("   ❌ ERROR: No se puede verificar token porque ApplicationContext es NULL\n");
        }
        report.append("\n");
        
        // 4. Verificar API y conexión
        report.append("4. VERIFICACIÓN DE API Y CONEXIÓN:\n");
        try {
            // Verificar si el cliente Retrofit está configurado
            if (ApiClient.getClient() == null) {
                report.append("   ❌ ERROR: Cliente API no inicializado\n");
            } else {
                report.append("   ✓ Cliente API inicializado correctamente\n");
                
                // Intentar recuperar la URL base
                String baseUrl = ApiClient.getClient().baseUrl().toString();
                report.append("   - URL Base: ").append(baseUrl).append("\n");
                
                // Hacer ping a la URL base para verificar conectividad
                report.append("   ⚠️ Conectividad a servidor necesita prueba manual\n");
                report.append("   - Recomendación: Verificar conexión a ").append(baseUrl).append("\n");
            }
        } catch (Exception e) {
            report.append("   ❌ ERROR en validación de API: ").append(e.getMessage()).append("\n");
        }
        
        // Mostrar reporte completo
        Log.d(TAG, "\n" + report.toString());
        
        // Mostrar un diálogo con la información de diagnóstico
        if (getContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Diagnóstico de Problemas");
            
            // Crear layout para mostrar el texto con scroll
            ScrollView scrollView = new ScrollView(getContext());
            TextView textView = new TextView(getContext());
            textView.setPadding(30, 30, 30, 30);
            textView.setText(report.toString());
            scrollView.addView(textView);
            
            builder.setView(scrollView);
            builder.setPositiveButton("Cerrar", null);
            builder.setNeutralButton("Copiar Reporte", (dialog, which) -> {
                // Copiar reporte al portapapeles
                android.content.ClipboardManager clipboard = 
                    (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = 
                    android.content.ClipData.newPlainText("Diagnóstico Mediciones", report.toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Reporte copiado al portapapeles", Toast.LENGTH_SHORT).show();
            });
            builder.show();
        }
    }

    /**
     * Utility method to debug token issues - call this from UI if needed
     */
    public void debugTokenIssues() {
        ListMeasurementRequest request = new ListMeasurementRequest();
        String token = request.getAuthToken();
        
        StringBuilder info = new StringBuilder();
        info.append("Información de Depuración de Token:\n\n");
        
        if (token == null) {
            info.append("- Token es NULL\n");
            info.append("- Probable problema con SharedPreferences o sesión\n");
        } else if (token.isEmpty()) {
            info.append("- Token está vacío\n");
            info.append("- Sesión posiblemente cerrada o inválida\n");
        } else {
            info.append("- Token existente (longitud: ").append(token.length()).append(")\n");
            
            // Verificar formato de token (Bearer + JWT)
            if (!token.startsWith("Bearer ")) {
                info.append("- Advertencia: Token no tiene prefijo 'Bearer'\n");
            }
            
            // Verificar si es un JWT válido (formato simple)
            String[] parts = token.replace("Bearer ", "").split("\\.");
            if (parts.length != 3) {
                info.append("- Advertencia: Token no parece tener formato JWT válido\n");
            } else {
                info.append("- Token parece tener formato JWT válido\n");
            }
        }
        
        // Mostrar resultado en el log
        Log.d(TAG, info.toString());
        
        // También mostrar en UI para debugging
        if (getContext() != null) {
            Toast.makeText(getContext(), 
                    "Información de depuración registrada en el log", 
                    Toast.LENGTH_SHORT).show();
        }
    }
}