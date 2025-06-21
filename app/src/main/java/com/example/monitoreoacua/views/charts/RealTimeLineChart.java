package com.example.monitoreoacua.views.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista personalizada para mostrar gráficos de líneas en tiempo real
 * Dibuja datos de sensores con actualizaciones en tiempo real
 */
public class RealTimeLineChart extends View {
    
    // Configuración de colores y estilos
    private static final int BACKGROUND_COLOR = Color.WHITE;
    private static final int GRID_COLOR = Color.LTGRAY;
    private static final int LINE_COLOR = Color.BLUE;
    private static final int AXIS_COLOR = Color.BLACK;
    private static final int TEXT_COLOR = Color.DKGRAY;
    
    // Configuración de líneas
    private static final float LINE_WIDTH = 3f;
    private static final float GRID_WIDTH = 1f;
    private static final float AXIS_WIDTH = 2f;
    
    // Máximo número de puntos a mostrar
    private static final int MAX_DATA_POINTS = 50;
    
    // Datos del gráfico
    private List<DataPoint> dataPoints;
    private String title = "Sensor Data";
    private String yAxisLabel = "Value";
    private String unit = "";
    
    // Paints para dibujar
    private Paint linePaint;
    private Paint gridPaint;
    private Paint axisPaint;
    private Paint textPaint;
    private Paint backgroundPaint;
    
    // Área de dibujo
    private float chartLeft, chartTop, chartRight, chartBottom;
    private float marginLeft = 80f;
    private float marginTop = 60f;
    private float marginRight = 40f;
    private float marginBottom = 80f;
    
    // Valores mínimos y máximos
    private float minValue = Float.MAX_VALUE;
    private float maxValue = Float.MIN_VALUE;
    private long minTime = Long.MAX_VALUE;
    private long maxTime = Long.MIN_VALUE;
    
    // Formateadores
    private DecimalFormat valueFormatter = new DecimalFormat("#.##");
    
    /**
     * Clase para representar un punto de datos
     */
    public static class DataPoint {
        public long timestamp;
        public float value;
        
        public DataPoint(long timestamp, float value) {
            this.timestamp = timestamp;
            this.value = value;
        }
    }
    
    public RealTimeLineChart(Context context) {
        super(context);
        init();
    }
    
    public RealTimeLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public RealTimeLineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    /**
     * Inicializar los paints y configuraciones
     */
    private void init() {
        dataPoints = new ArrayList<>();
        
        // Configurar paint para la línea
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(LINE_COLOR);
        linePaint.setStrokeWidth(LINE_WIDTH);
        linePaint.setStyle(Paint.Style.STROKE);
        
        // Configurar paint para la grilla
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(GRID_COLOR);
        gridPaint.setStrokeWidth(GRID_WIDTH);
        gridPaint.setStyle(Paint.Style.STROKE);
        
        // Configurar paint para los ejes
        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(AXIS_COLOR);
        axisPaint.setStrokeWidth(AXIS_WIDTH);
        axisPaint.setStyle(Paint.Style.STROKE);
        
        // Configurar paint para el texto
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(TEXT_COLOR);
        textPaint.setTextSize(24f);
        
        // Configurar paint para el fondo
        backgroundPaint = new Paint();
        backgroundPaint.setColor(BACKGROUND_COLOR);
        backgroundPaint.setStyle(Paint.Style.FILL);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        // Calcular área de dibujo del gráfico
        chartLeft = marginLeft;
        chartTop = marginTop;
        chartRight = w - marginRight;
        chartBottom = h - marginBottom;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Dibujar fondo
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
        
        if (dataPoints.isEmpty()) {
            drawEmptyState(canvas);
            return;
        }
        
        // Dibujar grilla
        drawGrid(canvas);
        
        // Dibujar ejes
        drawAxes(canvas);
        
        // Dibujar datos
        drawData(canvas);
        
        // Dibujar etiquetas
        drawLabels(canvas);
        
        // Dibujar título
        drawTitle(canvas);
    }
    
    /**
     * Dibujar estado vacío cuando no hay datos
     */
    private void drawEmptyState(Canvas canvas) {
        String emptyText = "No hay datos disponibles";
        Rect textBounds = new Rect();
        textPaint.getTextBounds(emptyText, 0, emptyText.length(), textBounds);
        
        float x = (getWidth() - textBounds.width()) / 2f;
        float y = (getHeight() + textBounds.height()) / 2f;
        
        canvas.drawText(emptyText, x, y, textPaint);
    }
    
    /**
     * Dibujar la grilla del gráfico
     */
    private void drawGrid(Canvas canvas) {
        // Líneas verticales
        int verticalLines = 6;
        for (int i = 0; i <= verticalLines; i++) {
            float x = chartLeft + (chartRight - chartLeft) * i / verticalLines;
            canvas.drawLine(x, chartTop, x, chartBottom, gridPaint);
        }
        
        // Líneas horizontales
        int horizontalLines = 6;
        for (int i = 0; i <= horizontalLines; i++) {
            float y = chartTop + (chartBottom - chartTop) * i / horizontalLines;
            canvas.drawLine(chartLeft, y, chartRight, y, gridPaint);
        }
    }
    
    /**
     * Dibujar los ejes X e Y
     */
    private void drawAxes(Canvas canvas) {
        // Eje Y (izquierda)
        canvas.drawLine(chartLeft, chartTop, chartLeft, chartBottom, axisPaint);
        
        // Eje X (abajo)
        canvas.drawLine(chartLeft, chartBottom, chartRight, chartBottom, axisPaint);
    }
    
    /**
     * Dibujar los datos como línea
     */
    private void drawData(Canvas canvas) {
        if (dataPoints.size() < 2) return;
        
        Path path = new Path();
        boolean firstPoint = true;
        
        for (DataPoint point : dataPoints) {
            float x = getXPosition(point.timestamp);
            float y = getYPosition(point.value);
            
            if (firstPoint) {
                path.moveTo(x, y);
                firstPoint = false;
            } else {
                path.lineTo(x, y);
            }
        }
        
        canvas.drawPath(path, linePaint);
        
        // Dibujar puntos individuales
        Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(LINE_COLOR);
        pointPaint.setStyle(Paint.Style.FILL);
        
        for (DataPoint point : dataPoints) {
            float x = getXPosition(point.timestamp);
            float y = getYPosition(point.value);
            canvas.drawCircle(x, y, 4f, pointPaint);
        }
    }
    
    /**
     * Dibujar etiquetas de los ejes
     */
    private void drawLabels(Canvas canvas) {
        // Etiquetas del eje Y
        int yLabels = 6;
        for (int i = 0; i <= yLabels; i++) {
            float value = minValue + (maxValue - minValue) * i / yLabels;
            String label = valueFormatter.format(value);
            
            float y = chartBottom - (chartBottom - chartTop) * i / yLabels;
            canvas.drawText(label, chartLeft - 10, y + 8, textPaint);
        }
        
        // Etiqueta del eje Y
        canvas.save();
        canvas.rotate(-90, 20, getHeight() / 2f);
        canvas.drawText(yAxisLabel + " (" + unit + ")", 20, getHeight() / 2f + 8, textPaint);
        canvas.restore();
        
        // Etiquetas del eje X (tiempo)
        if (dataPoints.size() > 0) {
            int xLabels = Math.min(5, dataPoints.size());
            for (int i = 0; i < xLabels; i++) {
                int index = i * (dataPoints.size() - 1) / (xLabels - 1);
                if (index < dataPoints.size()) {
                    DataPoint point = dataPoints.get(index);
                    String timeLabel = formatTime(point.timestamp);
                    
                    float x = getXPosition(point.timestamp);
                    canvas.drawText(timeLabel, x - 30, chartBottom + 30, textPaint);
                }
            }
        }
        
        // Etiqueta del eje X
        canvas.drawText("Tiempo", getWidth() / 2f - 30, getHeight() - 10, textPaint);
    }
    
    /**
     * Dibujar el título del gráfico
     */
    private void drawTitle(Canvas canvas) {
        Paint titlePaint = new Paint(textPaint);
        titlePaint.setTextSize(32f);
        titlePaint.setColor(Color.BLACK);
        
        Rect titleBounds = new Rect();
        titlePaint.getTextBounds(title, 0, title.length(), titleBounds);
        
        float x = (getWidth() - titleBounds.width()) / 2f;
        float y = 40f;
        
        canvas.drawText(title, x, y, titlePaint);
    }
    
    /**
     * Calcular posición X basada en el timestamp
     */
    private float getXPosition(long timestamp) {
        if (maxTime == minTime) return chartLeft;
        
        float ratio = (float) (timestamp - minTime) / (maxTime - minTime);
        return chartLeft + ratio * (chartRight - chartLeft);
    }
    
    /**
     * Calcular posición Y basada en el valor
     */
    private float getYPosition(float value) {
        if (maxValue == minValue) return chartBottom;
        
        float ratio = (value - minValue) / (maxValue - minValue);
        return chartBottom - ratio * (chartBottom - chartTop);
    }
    
    /**
     * Formatear timestamp para mostrar
     */
    private String formatTime(long timestamp) {
        long seconds = (System.currentTimeMillis() - timestamp) / 1000;
        if (seconds < 60) {
            return seconds + "s";
        } else {
            return (seconds / 60) + "m";
        }
    }
    
    /**
     * Agregar un nuevo punto de datos
     */
    public void addDataPoint(float value) {
        addDataPoint(System.currentTimeMillis(), value);
    }
    
    /**
     * Agregar un nuevo punto de datos con timestamp específico
     */
    public void addDataPoint(long timestamp, float value) {
        dataPoints.add(new DataPoint(timestamp, value));
        
        // Mantener solo los últimos MAX_DATA_POINTS puntos
        if (dataPoints.size() > MAX_DATA_POINTS) {
            dataPoints.remove(0);
        }
        
        updateMinMax();
        invalidate(); // Redibujar la vista
    }
    
    /**
     * Limpiar todos los datos
     */
    public void clearData() {
        dataPoints.clear();
        minValue = Float.MAX_VALUE;
        maxValue = Float.MIN_VALUE;
        minTime = Long.MAX_VALUE;
        maxTime = Long.MIN_VALUE;
        invalidate();
    }
    
    /**
     * Actualizar valores mínimos y máximos
     */
    private void updateMinMax() {
        minValue = Float.MAX_VALUE;
        maxValue = Float.MIN_VALUE;
        minTime = Long.MAX_VALUE;
        maxTime = Long.MIN_VALUE;
        
        for (DataPoint point : dataPoints) {
            minValue = Math.min(minValue, point.value);
            maxValue = Math.max(maxValue, point.value);
            minTime = Math.min(minTime, point.timestamp);
            maxTime = Math.max(maxTime, point.timestamp);
        }
        
        // Agregar un pequeño margen para mejor visualización
        if (minValue == maxValue) {
            minValue -= 1;
            maxValue += 1;
        } else {
            float range = maxValue - minValue;
            minValue -= range * 0.1f;
            maxValue += range * 0.1f;
        }
    }
    
    /**
     * Configurar el título del gráfico
     */
    public void setTitle(String title) {
        this.title = title;
        invalidate();
    }
    
    /**
     * Configurar la etiqueta del eje Y
     */
    public void setYAxisLabel(String label) {
        this.yAxisLabel = label;
        invalidate();
    }
    
    /**
     * Configurar la unidad de medida
     */
    public void setUnit(String unit) {
        this.unit = unit;
        invalidate();
    }
    
    /**
     * Configurar el color de la línea
     */
    public void setLineColor(int color) {
        linePaint.setColor(color);
        invalidate();
    }
    
    /**
     * Obtener el último valor agregado
     */
    public float getLastValue() {
        if (dataPoints.isEmpty()) return 0f;
        return dataPoints.get(dataPoints.size() - 1).value;
    }
    
    /**
     * Obtener valor mínimo actual
     */
    public float getMinValue() {
        return minValue;
    }
    
    /**
     * Obtener valor máximo actual
     */
    public float getMaxValue() {
        return maxValue;
    }
    
    /**
     * Obtener promedio de todos los valores
     */
    public float getAverageValue() {
        if (dataPoints.isEmpty()) return 0f;
        
        float sum = 0f;
        for (DataPoint point : dataPoints) {
            sum += point.value;
        }
        return sum / dataPoints.size();
    }
    
    /**
     * Obtener número de puntos de datos
     */
    public int getDataPointCount() {
        return dataPoints.size();
    }
}

