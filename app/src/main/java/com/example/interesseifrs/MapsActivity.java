package com.example.interesseifrs;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
    private static final int LOCATION_REQUEST_CODE = 1001;

    //
    //https://openrouteservice.org/dev/#/signup
    private static final String OPENROUTE_API_KEY = "5b3ce3597851110001cf6248ccb5c9e10b5645cc9fd1239344080ee8";
    private static final String OPENROUTE_BASE_URL = "https://api.openrouteservice.org/v2/directions";

    // Método para verificar se a chave está configurada
    private boolean isApiKeyConfigured() {
        return !OPENROUTE_API_KEY.equals("5b3ce3597851110001cf6248ccb5c9e10b5645cc9fd1239344080ee8") &&
                !OPENROUTE_API_KEY.isEmpty();
    }

    // Campus coordinates
    private static final LatLng IFRS_ROLANTE = new LatLng(-29.65532385, -50.61720449);
    private static final int DEFAULT_ZOOM_LEVEL = 12;
    private static final int POLYLINE_WIDTH = 8;
    private static final int POLYLINE_COLOR = Color.BLUE;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_activity);

        try {
            initializeMapFragment();
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao inicializar: ", e);
            showErrorAndFinish("Erro ao inicializar mapa: " + e.getMessage());
        }
    }

    private void initializeMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            throw new IllegalStateException("Map fragment not found");
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        configureMapSettings();
        addCampusMarker();

        if (checkLocationPermission()) {
            enableMyLocation(); // Obtém a localização e traça rota até IFRS
        } else {
            Toast.makeText(this, "Permissão de localização negada. Não é possível traçar a rota.", Toast.LENGTH_SHORT).show();
        }
    }

    private void configureMapSettings() {
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    private void addCampusMarker() {
        mMap.addMarker(new MarkerOptions()
                .position(IFRS_ROLANTE)
                .title("IFRS Campus Rolante"));
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @RequiresPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    private void enableMyLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                mMap.setMyLocationEnabled(true);
                fetchLastLocation();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Erro de permissão: ", e);
        }
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void fetchLastLocation() {
        Task<Location> locationTask = fusedLocationClient.getLastLocation();
        locationTask.addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d(TAG, "Localização atual: " + currentLocation.latitude + ", " + currentLocation.longitude);

                // Adiciona marker na localização atual
                mMap.addMarker(new MarkerOptions()
                        .position(currentLocation)
                        .title("Sua Localização"));

                // Usar rota de carro como padrão
                fetchRoute(currentLocation, IFRS_ROLANTE, "driving-car");
            } else {
                Log.w(TAG, "Localização atual não disponível");
                Toast.makeText(this, "Não foi possível obter sua localização", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Erro ao obter localização: ", e);
            Toast.makeText(this, "Erro ao obter localização", Toast.LENGTH_SHORT).show();
        });
    }

    // Método principal para buscar rota - agora com suporte a diferentes perfis
    private void fetchRoute(LatLng origin, LatLng destination, String profile) {
        if (!isApiKeyConfigured()) {
            Toast.makeText(this, "Chave de API OpenRoute não configurada. Obtenha gratuitamente em openrouteservice.org", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Chave de API OpenRoute não configurada!");
            return;
        }

        String url = buildOpenRouteUrl(origin, destination, profile);
        Log.d(TAG, "URL da OpenRoute API: " + url);
        new DirectionsDownloadTask().execute(url);
    }

    // Sobrecarga para manter compatibilidade - usa carro como padrão
    private void fetchRoute(LatLng origin, LatLng destination) {
        fetchRoute(origin, destination, "driving-car");
    }

    // Construir URL para OpenRouteService
    private String buildOpenRouteUrl(LatLng origin, LatLng dest, String profile) {
        return OPENROUTE_BASE_URL + "/" + profile + "?" +
                "start=" + origin.longitude + "," + origin.latitude +
                "&end=" + dest.longitude + "," + dest.latitude +
                "&api_key=" + OPENROUTE_API_KEY;
    }

    // Método auxiliar para converter perfis (opcional)
    private String getOpenRouteProfile(String mode) {
        switch (mode.toLowerCase()) {
            case "walking":
            case "foot":
                return "foot-walking";
            case "cycling":
            case "bike":
                return "cycling-regular";
            case "driving":
            case "car":
            default:
                return "driving-car";
        }
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    @RequiresPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // AsyncTask para download dos dados de direções - adaptado para OpenRoute
    private class DirectionsDownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            try {
                String result = downloadUrl(url[0]);
                Log.d(TAG, "Resposta da OpenRoute API: " + (result != null ? result.substring(0, Math.min(200, result.length())) + "..." : "null"));
                return result;
            } catch (Exception e) {
                Log.e(TAG, "Erro no download: ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.isEmpty()) {
                try {
                    // OpenRouteService tem estrutura diferente do Google
                    JSONObject jsonResponse = new JSONObject(result);

                    // Verificar se há features (rotas)
                    if (jsonResponse.has("features")) {
                        JSONArray features = jsonResponse.getJSONArray("features");
                        if (features.length() > 0) {
                            Log.d(TAG, "Rota OpenRoute encontrada com sucesso!");
                            new DirectionsParserTask().execute(result);
                        } else {
                            Log.w(TAG, "Nenhuma rota encontrada na resposta");
                            Toast.makeText(MapsActivity.this, "Nenhuma rota encontrada", Toast.LENGTH_SHORT).show();
                        }
                    } else if (jsonResponse.has("error")) {
                        // Tratamento de erro da OpenRoute
                        JSONObject error = jsonResponse.getJSONObject("error");
                        String errorMessage = "Erro OpenRoute: " + error.optString("message", "Erro desconhecido");
                        Log.e(TAG, errorMessage);
                        Toast.makeText(MapsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e(TAG, "Resposta OpenRoute inválida: sem features ou error");
                        Toast.makeText(MapsActivity.this, "Resposta inválida da API", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Erro ao processar resposta OpenRoute: ", e);
                    Toast.makeText(MapsActivity.this, "Erro ao processar resposta da API", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Resposta vazia da OpenRoute API");
                Toast.makeText(MapsActivity.this, "Resposta vazia da API de direções", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            // Headers importantes para OpenRouteService
            urlConnection.setRequestProperty("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            Log.d(TAG, "Código de resposta HTTP: " + responseCode);

            if (responseCode != HttpURLConnection.HTTP_OK) {
                // Tentar ler stream de erro para OpenRouteService
                InputStream errorStream = urlConnection.getErrorStream();
                if (errorStream != null) {
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                    StringBuilder errorBuffer = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorBuffer.append(errorLine);
                    }
                    Log.e(TAG, "Erro HTTP " + responseCode + ": " + errorBuffer.toString());
                    errorReader.close();
                }
                throw new IOException("HTTP error code: " + responseCode);
            }

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            return buffer.toString();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Erro ao fechar stream", e);
                }
            }
        }
    }

    // AsyncTask para parsing dos dados de direções - adaptado para OpenRoute
    private class DirectionsParserTask extends AsyncTask<String, Void, List<LatLng>> {
        @Override
        protected List<LatLng> doInBackground(String... jsonData) {
            try {
                JSONObject jsonObject = new JSONObject(jsonData[0]);
                return parseOpenRouteResponse(jsonObject);
            } catch (JSONException e) {
                Log.e(TAG, "Erro no parsing JSON OpenRoute: ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<LatLng> points) {
            if (points != null && !points.isEmpty()) {
                Log.d(TAG, "Pontos da rota OpenRoute obtidos: " + points.size());
                drawRoute(points);
                adjustCameraToRoute(points);
            } else {
                Log.w(TAG, "Nenhum ponto da rota OpenRoute obtido");
                Toast.makeText(MapsActivity.this, "Não foi possível obter rota", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Parser específico para resposta do OpenRouteService
    private List<LatLng> parseOpenRouteResponse(JSONObject jsonObject) throws JSONException {
        List<LatLng> points = new ArrayList<>();

        // OpenRouteService retorna "features" ao invés de "routes"
        JSONArray features = jsonObject.getJSONArray("features");
        if (features.length() == 0) {
            Log.w(TAG, "Nenhuma feature/rota encontrada");
            return points;
        }

        JSONObject feature = features.getJSONObject(0);
        JSONObject geometry = feature.getJSONObject("geometry");
        JSONArray coordinates = geometry.getJSONArray("coordinates");

        Log.d(TAG, "Coordenadas OpenRoute obtidas, quantidade: " + coordinates.length());

        // OpenRouteService retorna coordenadas como [longitude, latitude] (diferente do Google)
        for (int i = 0; i < coordinates.length(); i++) {
            JSONArray coord = coordinates.getJSONArray(i);
            double longitude = coord.getDouble(0);
            double latitude = coord.getDouble(1);
            points.add(new LatLng(latitude, longitude));
        }

        Log.d(TAG, "Pontos decodificados OpenRoute: " + points.size());
        return points;
    }

    private void drawRoute(List<LatLng> points) {
        if (mMap == null || points == null || points.isEmpty()) {
            Log.w(TAG, "Não é possível desenhar rota - mapa ou pontos inválidos");
            return;
        }

        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(points)
                .width(POLYLINE_WIDTH)
                .color(POLYLINE_COLOR)
                .geodesic(true);

        mMap.addPolyline(polylineOptions);
        Log.d(TAG, "Rota OpenRoute desenhada com sucesso!");
        Toast.makeText(this, "Rota carregada via OpenRouteService!", Toast.LENGTH_SHORT).show();
    }

    private void adjustCameraToRoute(List<LatLng> points) {
        if (points == null || points.isEmpty()) return;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : points) {
            builder.include(point);
        }

        // Adiciona os pontos de origem e destino
        builder.include(IFRS_ROLANTE);

        LatLngBounds bounds = builder.build();
        int padding = 100; // pixels

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    // Método adicional para usar diferentes perfis de transporte
    public void fetchRouteWithProfile(LatLng origin, LatLng destination, String transportMode) {
        String profile = getOpenRouteProfile(transportMode);
        fetchRoute(origin, destination, profile);
    }

    // NOTA: O método decodePoly não é mais necessário pois OpenRouteService
    // retorna coordenadas diretas, não polyline codificada como o Google
}