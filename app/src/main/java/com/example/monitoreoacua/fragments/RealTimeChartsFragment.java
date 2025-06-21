package com.example.monitoreoacua.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Measurement;
import com.example.monitoreoacua.business.models.Module;
import com.example.monitoreoacua.business.models.Sensor;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiMeasurementsService;
import com.example.monitoreoacua.service.ApiModulesService;
import com.example.monitoreoacua.service.request.ListMeasurementRequest;
import com.example.monitoreoacua.service.request.GetModuleRequest;
import com.example.monitoreoacua.service.response.ListMeasurementResponse;
import com.example.monitoreoacua.service.response.GetModuleResponse;
import com.example.monitoreoacua.views.charts.RealTimeLineChart;
import com.example.monitoreoacua.views.measurements.MeasurementsAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragmento para mostrar gráficos en tiempo real de mediciones de sensores
 */
public class RealTimeChartsFragment extends Fragment {
    private static final String TAG = "RealTimeChartsFragment";
    private static final String ARG_MODULE_ID = "module_id";
    private static final int UPDATE_INTERVAL_MS = 5000; // 5 segundos

    // Views
    private Spinner spinnerSensors;
    private Button btnToggleRealTime;
    private TextView tvStatus;
    private TextView tvLastUpdate;
    private TextView tvCurrentValue;
    private TextView tvMinValue;
    private TextView tvMaxValue;
    private TextView tvAvgValue;
    private TextView tvChartTitle;
    private TextView tvChartPlaceholder;
    private View statusIndicator;
    private ProgressBar progressBar;
    private RealTimeLineChart realTimeChart;
    private RecyclerView recyclerViewHistory;

    // Data
    private int moduleId;
    private List<Sensor> sensors;
    private Sensor selectedSensor;
    private MeasurementsAdapter historyAdapter;

    // Real-time updating
    private Handler updateHandler;
    private Runnable updateRunnable;
    private boolean isRealTimeActive = false;

    // Date formatter
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public static RealTimeChartsFragment newInstance(int moduleId) {
        RealTimeChartsFragment fragment = new RealTimeChartsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MODULE_ID, moduleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            moduleId = getArguments().getInt(ARG_MODULE_ID);
        }
        
        sensors = new ArrayList<>();
        updateHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_real_time_charts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        setupChart();
        setupHistoryRecyclerView();
        setupListeners();
        loadModuleSensors();
    }

    private void initializeViews(View view) {
        spinnerSensors = view.findViewById(R.id.spinnerSensors);
        btnToggleRealTime = view.findViewById(R.id.btnToggleRealTime);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvLastUpdate = view.findViewById(R.id.tvLastUpdate);
        tvCurrentValue = view.findViewById(R.id.tvCurrentValue);
        tvMinValue = view.findViewById(R.id.tvMinValue);
        tvMaxValue = view.findViewById(R.id.tvMaxValue);
        tvAvgValue = view.findViewById(R.id.tvAvgValue);
        tvChartTitle = view.findViewById(R.id.tvChartTitle);
        tvChartPlaceholder = view.findViewById(R.id.tvChartPlaceholder);
        statusIndicator = view.findViewById(R.id.statusIndicator);
        progressBar = view.findViewById(R.id.progressBar);
        realTimeChart = view.findViewById(R.id.realTimeChart);
        recyclerViewHistory = view.findViewById(R.id.recyclerViewHistory);
    }

    private void setupChart() {
        realTimeChart.setTitle("Datos del Sensor");
        realTimeChart.setYAxisLabel("Valor");
        realTimeChart.setUnit("");
    }

    private void setupHistoryRecyclerView() {
        historyAdapter = new MeasurementsAdapter();
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewHistory.setAdapter(historyAdapter);
    }

    private void setupListeners() {
        btnToggleRealTime.setOnClickListener(v -> toggleRealTimeUpdates());

        // Configurar el updateRunnable
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRealTimeActive && selectedSensor != null) {
                    fetchLatestMeasurement();
                    updateHandler.postDelayed(this, UPDATE_INTERVAL_MS);
                }
            }
        };
    }

    private void loadModuleSensors() {
        progressBar.setVisibility(View.VISIBLE);
        
        ApiModulesService apiService = ApiClient.getClient().create(ApiModulesService.class);
        GetModuleRequest request = new GetModuleRequest();
        String authToken = request.getAuthToken();

        if (authToken == null || authToken.isEmpty()) {
            showError("Error: Token de autorización no válido");
            return;
        }

        Call<GetModuleResponse> call = apiService.getModuleById(authToken, moduleId);
        call.enqueue(new Callback<GetModuleResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetModuleResponse> call, @NonNull Response<GetModuleResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    Module module = response.body().getModule();
                    if (module != null && module.getSensors() != null) {
                        sensors.clear();
                        sensors.addAll(module.getSensors());
                        setupSensorSpinner();
                    } else {
                        showError("No se encontraron sensores en este módulo");
                    }
                } else {
                    showError("Error al cargar los sensores del módulo");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetModuleResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Error de conexión: " + t.getMessage());
                Log.e(TAG, "Error loading module sensors", t);
            }
        });
    }

    private void setupSensorSpinner() {
        List<String> sensorNames = new ArrayList<>();
        sensorNames.add("Selecciona un sensor");
        
        for (Sensor sensor : sensors) {
            String displayName = sensor.getName();
            if (displayName == null || displayName.isEmpty()) {
                displayName = "Sensor " + sensor.getId();
            }
            sensorNames.add(displayName);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), 
            android.R.layout.simple_spinner_item, sensorNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSensors.setAdapter(adapter);

        spinnerSensors.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedSensor = sensors.get(position - 1);
                    onSensorSelected();
                } else {
                    selectedSensor = null;
                    clearChart();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedSensor = null;
                clearChart();
            }
        });
    }

    private void onSensorSelected() {
        if (selectedSensor == null) return;

        // Configurar el gráfico para el sensor seleccionado
        setupChartForSensor(selectedSensor);
        
        // Cargar datos históricos iniciales
        loadInitialData();
        
        // Habilitar el botón de tiempo real
        btnToggleRealTime.setEnabled(true);
    }

    private void setupChartForSensor(Sensor sensor) {
        String sensorType = getSensorTypeFromThresholds(sensor);
        String readableType = getReadableSensorType(sensorType);
        String unit = getSensorUnit(sensorType);

        realTimeChart.clearData();
        realTimeChart.setTitle("Datos de " + readableType);
        realTimeChart.setYAxisLabel(readableType);
        realTimeChart.setUnit(unit);
        
        // Configurar color basado en el tipo de sensor
        int color = getSensorColor(sensorType);
        realTimeChart.setLineColor(color);

        tvChartTitle.setText("Valores de " + readableType + " en Tiempo Real");
        tvChartPlaceholder.setVisibility(View.GONE);
        realTimeChart.setVisibility(View.VISIBLE);
    }

    private void loadInitialData() {
        if (selectedSensor == null) return;

        ApiMeasurementsService apiService = ApiClient.getClient().create(ApiMeasurementsService.class);
        ListMeasurementRequest request = new ListMeasurementRequest();
        String authToken = request.getAuthToken();

        Call<ListMeasurementResponse> call = apiService.getMeasurements(
            moduleId, selectedSensor.getId(), authToken);

        call.enqueue(new Callback<ListMeasurementResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListMeasurementResponse> call, @NonNull Response<ListMeasurementResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Measurement> measurements = response.body().getData();
                    if (measurements != null && !measurements.isEmpty()) {
                        // Mostrar las últimas 20 mediciones en el gráfico
                        int startIndex = Math.max(0, measurements.size() - 20);
                        for (int i = startIndex; i < measurements.size(); i++) {
                            Measurement measurement = measurements.get(i);
                            realTimeChart.addDataPoint((float) measurement.getValue());
                        }
                        
                        // Mostrar las últimas 10 en el historial
                        List<Measurement> recentMeasurements = measurements.subList(
                            Math.max(0, measurements.size() - 10), measurements.size());
                        historyAdapter.setMeasurementList(recentMeasurements);
                        
                        updateStatistics();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListMeasurementResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error loading initial data", t);
            }
        });
    }

    private void toggleRealTimeUpdates() {
        if (selectedSensor == null) {
            Toast.makeText(getContext(), "Selecciona un sensor primero", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isRealTimeActive) {
            stopRealTimeUpdates();
        } else {
            startRealTimeUpdates();
        }
    }

    private void startRealTimeUpdates() {
        isRealTimeActive = true;
        btnToggleRealTime.setText("Detener");
        btnToggleRealTime.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        
        tvStatus.setText("Conectado");
        statusIndicator.setBackgroundResource(R.drawable.status_indicator_connected);
        
        updateHandler.post(updateRunnable);
        
        Toast.makeText(getContext(), "Monitorización en tiempo real iniciada", Toast.LENGTH_SHORT).show();
    }

    private void stopRealTimeUpdates() {
        isRealTimeActive = false;
        btnToggleRealTime.setText("Iniciar");
        btnToggleRealTime.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        
        tvStatus.setText("Desconectado");
        statusIndicator.setBackgroundResource(R.drawable.status_indicator_disconnected);
        
        updateHandler.removeCallbacks(updateRunnable);
        
        Toast.makeText(getContext(), "Monitorización en tiempo real detenida", Toast.LENGTH_SHORT).show();
    }

    private void fetchLatestMeasurement() {
        if (selectedSensor == null) return;

        ApiMeasurementsService apiService = ApiClient.getClient().create(ApiMeasurementsService.class);
        ListMeasurementRequest request = new ListMeasurementRequest();
        String authToken = request.getAuthToken();

        Call<ListMeasurementResponse> call = apiService.getMeasurements(
            moduleId, selectedSensor.getId(), authToken);

        call.enqueue(new Callback<ListMeasurementResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListMeasurementResponse> call, @NonNull Response<ListMeasurementResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Measurement> measurements = response.body().getData();
                    if (measurements != null && !measurements.isEmpty()) {
                        // Obtener la medición más reciente
                        Measurement latest = measurements.get(measurements.size() - 1);
                        
                        // Agregar al gráfico
                        realTimeChart.addDataPoint((float) latest.getValue());
                        
                        // Actualizar estadísticas
                        updateStatistics();
                        
                        // Actualizar historial (últimas 10 mediciones)
                        List<Measurement> recentMeasurements = measurements.subList(
                            Math.max(0, measurements.size() - 10), measurements.size());
                        historyAdapter.setMeasurementList(recentMeasurements);
                        
                        // Actualizar timestamp
                        tvLastUpdate.setText("Última actualización: " + timeFormatter.format(new Date()));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListMeasurementResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error fetching latest measurement", t);
                // No mostrar error para no molestar al usuario durante actualizaciones automáticas
            }
        });
    }

    private void updateStatistics() {
        if (realTimeChart.getDataPointCount() == 0) return;

        tvCurrentValue.setText(String.format(Locale.getDefault(), "%.2f", realTimeChart.getLastValue()));
        tvMinValue.setText(String.format(Locale.getDefault(), "%.2f", realTimeChart.getMinValue()));
        tvMaxValue.setText(String.format(Locale.getDefault(), "%.2f", realTimeChart.getMaxValue()));
        tvAvgValue.setText(String.format(Locale.getDefault(), "%.2f", realTimeChart.getAverageValue()));
    }

    private void clearChart() {
        realTimeChart.clearData();
        realTimeChart.setVisibility(View.GONE);
        tvChartPlaceholder.setVisibility(View.VISIBLE);
        
        tvCurrentValue.setText("--");
        tvMinValue.setText("--");
        tvMaxValue.setText("--");
        tvAvgValue.setText("--");
        
        historyAdapter.setMeasurementList(new ArrayList<>());
        
        btnToggleRealTime.setEnabled(false);
        
        if (isRealTimeActive) {
            stopRealTimeUpdates();
        }
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Métodos helper reutilizados del MeasurementsAdapter
    private String getSensorTypeFromThresholds(Sensor sensor) {
        if (sensor == null || sensor.getThresholds() == null || sensor.getThresholds().isEmpty()) {
            return "";
        }

        for (Sensor.Threshold threshold : sensor.getThresholds()) {
            if (threshold != null && threshold.getType() != null && !threshold.getType().isEmpty()) {
                return threshold.getType();
            }
        }
        return "";
    }

    private String getReadableSensorType(String type) {
        if (type == null) return "Desconocido";

        switch (type.toLowerCase()) {
            case "temperature":
                return "Temperatura";
            case "ph":
                return "pH";
            case "oxygen":
                return "Oxígeno Disuelto";
            case "ec":
                return "Conductividad Eléctrica";
            case "humidity":
                return "Humedad";
            case "light":
                return "Luz";
            case "waterlevel":
                return "Nivel de Agua";
            default:
                return type;
        }
    }

    private String getSensorUnit(String type) {
        if (type == null) return "";

        switch (type.toLowerCase()) {
            case "temperature":
                return "°C";
            case "ph":
                return "pH";
            case "oxygen":
                return "mg/L";
            case "ec":
                return "μS/cm";
            case "humidity":
                return "%";
            case "light":
                return "lux";
            case "waterlevel":
                return "cm";
            default:
                return "";
        }
    }

    private int getSensorColor(String type) {
        if (type == null) return Color.BLUE;

        switch (type.toLowerCase()) {
            case "temperature":
                return Color.RED;
            case "ph":
                return Color.MAGENTA;
            case "oxygen":
                return Color.CYAN;
            case "ec":
                return Color.YELLOW;
            case "humidity":
                return Color.BLUE;
            case "light":
                return Color.parseColor("#FFA500"); // Orange
            case "waterlevel":
                return Color.parseColor("#0000FF"); // Blue
            default:
                return Color.BLUE;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isRealTimeActive) {
            stopRealTimeUpdates();
        }
    }
}

