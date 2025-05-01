package com.example.monitoreoacua.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
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
import com.example.monitoreoacua.views.measurements.MeasurementsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment for displaying a list of measurements
 * Based on the pattern of ListModulesFragment
 */
public class ListMeasurementsFragment extends Fragment implements MeasurementsAdapter.OnMeasurementClickListener {
    private static final String TAG = "ListMeasurementsFragment";
    private static final String ARG_MODULE_ID = "moduleId";
    private static final String ARG_SENSOR_ID = "sensorId";

    private RecyclerView recyclerViewMeasurements;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvEmptyView;
    private ProgressBar progressBar;
    private MeasurementsAdapter measurementAdapter;

    private String moduleId;
    private String sensorId;

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
    public static ListMeasurementsFragment newInstance(String moduleId, String sensorId) {
        ListMeasurementsFragment fragment = new ListMeasurementsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MODULE_ID, moduleId);
        args.putString(ARG_SENSOR_ID, sensorId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            moduleId = getArguments().getString(ARG_MODULE_ID);
            sensorId = getArguments().getString(ARG_SENSOR_ID);
            Log.d(TAG, "moduleId: " + moduleId + ", sensorId: " + sensorId);
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

        // Fetch measurements data
        fetchMeasurements();
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
     * Fetch measurements from the API
     */
    private void fetchMeasurements() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewMeasurements.setVisibility(View.GONE);
        tvEmptyView.setVisibility(View.GONE);

        if (moduleId == null || moduleId.isEmpty() || sensorId == null || sensorId.isEmpty()) {
            showError("Error: ID del módulo o sensor no válido");
            Log.e(TAG, "Error: moduleId o sensorId es nulo o vacío");
            return;
        }
        
        try {
            int moduleIdInt = Integer.parseInt(moduleId);
            int sensorIdInt = Integer.parseInt(sensorId);
            
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
            
            // Log para depuración
            Log.d(TAG, "Realizando petición con moduleId: " + moduleIdInt + ", sensorId: " + sensorIdInt);
            Log.d(TAG, "Petición a endpoint: /api/v2/owner/modules/measurements?moduleId=" + moduleIdInt + "&sensorId=" + sensorIdInt);
            Log.d(TAG, "Token (primeros 10 caracteres): " + 
                    (authToken.length() > 10 ? authToken.substring(0, 10) + "..." : authToken));
            
            // Hacer llamada a la API
            Call<ListMeasurementResponse> call = apiService.getMeasurements(
                    moduleIdInt,
                    sensorIdInt,
                    authToken
            );
            
            call.enqueue(new Callback<ListMeasurementResponse>() {
                @Override
                public void onResponse(@NonNull Call<ListMeasurementResponse> call, @NonNull Response<ListMeasurementResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    
                    // Log de la respuesta completa
                    Log.d(TAG, "Respuesta recibida - Código: " + response.code());
                    Log.d(TAG, "URL de la petición: " + call.request().url());
                    
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Respuesta exitosa");
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
            showError("Error: Los IDs del módulo y sensor deben ser números válidos");
            Log.e(TAG, "Error al convertir IDs a enteros: moduleId=" + moduleId + ", sensorId=" + sensorId, e);
        }
    }

    /**
     * Show error message and update UI
     *
     * @param message The error message to display
     */
    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        recyclerViewMeasurements.setVisibility(View.GONE);
        tvEmptyView.setVisibility(View.VISIBLE);
        tvEmptyView.setText(R.string.error_loading_measurements);
    }

    /**
     * Refresh measurements data
     * Called from outside the fragment to refresh the list
     */
    public void refreshMeasurements() {
        fetchMeasurements();
    }
}