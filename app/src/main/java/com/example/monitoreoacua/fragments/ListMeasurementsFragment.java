package com.example.monitoreoacua.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

    // Filtrado por fechas
    private Button btnFromDate;
    private Button btnToDate;
    private Button btnApplyDateFilter;
    private Button btnClearDateFilter;
    
    private Calendar fromDate;
    private Calendar toDate;
    private SimpleDateFormat dateFormatter;
    
    // Lista completa de mediciones (sin filtrar)
    private List<Measurement> allMeasurements;
    
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
        
        // Initialize date filter controls
        btnFromDate = view.findViewById(R.id.btnFromDate);
        btnToDate = view.findViewById(R.id.btnToDate);
        btnApplyDateFilter = view.findViewById(R.id.btnApplyDateFilter);
        btnClearDateFilter = view.findViewById(R.id.btnClearDateFilter);
        
        // Initialize date formatter and lists
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        allMeasurements = new ArrayList<>();
        
        // Setup date filter listeners
        setupDateFilterListeners();

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
                            
                            // Store all measurements for filtering
                            allMeasurements.clear();
                            allMeasurements.addAll(measurements);
                            
                            // Display measurements in adapter
                            measurementAdapter.setMeasurementList(measurements);
                            recyclerViewMeasurements.setVisibility(View.VISIBLE);
                            tvEmptyView.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "No se recibieron mediciones");
                            
                            // Clear stored measurements
                            allMeasurements.clear();
                            
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

    /**
     * Setup listeners for date filter controls
     */
    private void setupDateFilterListeners() {
        // From date button
        btnFromDate.setOnClickListener(v -> showDatePicker(true));
        
        // To date button
        btnToDate.setOnClickListener(v -> showDatePicker(false));
        
        // Apply filter button
        btnApplyDateFilter.setOnClickListener(v -> applyDateFilter());
        
        // Clear filter button
        btnClearDateFilter.setOnClickListener(v -> clearDateFilter());
    }

    /**
     * Show date picker dialog
     * @param isFromDate true if selecting from date, false for to date
     */
    private void showDatePicker(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        
        // Set initial date if already selected
        if (isFromDate && fromDate != null) {
            calendar = fromDate;
        } else if (!isFromDate && toDate != null) {
            calendar = toDate;
        }
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            getContext(),
            (view, year, month, dayOfMonth) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                
                if (isFromDate) {
                    fromDate = selectedDate;
                    btnFromDate.setText(dateFormatter.format(selectedDate.getTime()));
                } else {
                    toDate = selectedDate;
                    btnToDate.setText(dateFormatter.format(selectedDate.getTime()));
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.show();
    }

    /**
     * Apply date filter to measurements
     */
    private void applyDateFilter() {
        if (fromDate == null && toDate == null) {
            Toast.makeText(getContext(), "Selecciona al menos una fecha", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (fromDate != null && toDate != null && fromDate.after(toDate)) {
            Toast.makeText(getContext(), "La fecha 'Desde' debe ser anterior a la fecha 'Hasta'", Toast.LENGTH_SHORT).show();
            return;
        }
        
        List<Measurement> filteredMeasurements = new ArrayList<>();
        
        for (Measurement measurement : allMeasurements) {
            if (isDateInRange(measurement)) {
                filteredMeasurements.add(measurement);
            }
        }
        
        // Update adapter with filtered list
        measurementAdapter.setMeasurementList(filteredMeasurements);
        
        // Update UI visibility
        if (filteredMeasurements.isEmpty()) {
            recyclerViewMeasurements.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
            tvEmptyView.setText("No se encontraron mediciones en el rango de fechas seleccionado");
        } else {
            recyclerViewMeasurements.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }
        
        Toast.makeText(getContext(), "Filtro aplicado: " + filteredMeasurements.size() + " mediciones encontradas", Toast.LENGTH_SHORT).show();
    }

    /**
     * Clear date filter and show all measurements
     */
    private void clearDateFilter() {
        fromDate = null;
        toDate = null;
        
        btnFromDate.setText("Seleccionar fecha");
        btnToDate.setText("Seleccionar fecha");
        
        // Show all measurements
        measurementAdapter.setMeasurementList(allMeasurements);
        
        // Update UI visibility
        if (allMeasurements.isEmpty()) {
            recyclerViewMeasurements.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
            tvEmptyView.setText(getString(R.string.empty_measurements_list));
        } else {
            recyclerViewMeasurements.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }
        
        Toast.makeText(getContext(), "Filtro eliminado", Toast.LENGTH_SHORT).show();
    }

    /**
     * Check if a measurement's date is within the selected range
     * @param measurement The measurement to check
     * @return true if the measurement is within the date range
     */
    private boolean isDateInRange(Measurement measurement) {
        if (measurement.getDate() == null || measurement.getDate().isEmpty()) {
            return false;
        }
        
        try {
            Date measurementDate = dateFormatter.parse(measurement.getDate());
            
            if (measurementDate == null) {
                return false;
            }
            
            // Check from date
            if (fromDate != null) {
                Calendar fromCal = Calendar.getInstance();
                fromCal.setTime(fromDate.getTime());
                fromCal.set(Calendar.HOUR_OF_DAY, 0);
                fromCal.set(Calendar.MINUTE, 0);
                fromCal.set(Calendar.SECOND, 0);
                fromCal.set(Calendar.MILLISECOND, 0);
                
                if (measurementDate.before(fromCal.getTime())) {
                    return false;
                }
            }
            
            // Check to date
            if (toDate != null) {
                Calendar toCal = Calendar.getInstance();
                toCal.setTime(toDate.getTime());
                toCal.set(Calendar.HOUR_OF_DAY, 23);
                toCal.set(Calendar.MINUTE, 59);
                toCal.set(Calendar.SECOND, 59);
                toCal.set(Calendar.MILLISECOND, 999);
                
                if (measurementDate.after(toCal.getTime())) {
                    return false;
                }
            }
            
            return true;
            
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing measurement date: " + measurement.getDate(), e);
            return false;
        }
    }
}
