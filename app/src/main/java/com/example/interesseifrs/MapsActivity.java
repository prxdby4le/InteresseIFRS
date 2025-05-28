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

    // IMPORTANTE: Substitua pela sua chave de API válida
    // Também pode ser movida para strings.xml ou BuildConfig para maior segurança
    private static final String DIRECTIONS_API_KEY = "AIzaSyAcCbOa1KEmiLboD20IT9pdw0JzFLCWet4";

    // Método para verificar se a chave está configurada
    private boolean isApiKeyConfigured() {
        return !DIRECTIONS_API_KEY.equals("AIzaSyAcCbOa1KEmiLboD20IT9pdw0JzFLCWet4") &&
                !DIRECTIONS_API_KEY.isEmpty();
    }

    // Campus coordinates
    private static final LatLng IFRS_ROLANTE = new LatLng(-29.6505, -50.5770);
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
            enableMyLocation();
        } else {
            // Se não tiver permissão, ainda pode mostrar rota do campus para um ponto fixo (para teste)
            LatLng pontoTeste = new LatLng(-29.6405, -50.5670); // Ponto próximo para teste
            fetchRoute(pontoTeste, IFRS_ROLANTE);
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

                fetchRoute(currentLocation, IFRS_ROLANTE);
            } else {
                Log.w(TAG, "Localização atual não disponível");
                Toast.makeText(this, "Não foi possível obter sua localização", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Erro ao obter localização: ", e);
            Toast.makeText(this, "Erro ao obter localização", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchRoute(LatLng origin, LatLng destination) {
        if (!isApiKeyConfigured()) {
            Toast.makeText(this, "Chave de API não configurada. Verifique DIRECTIONS_API_KEY", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Chave de API não configurada!");
            return;
        }

        String url = buildDirectionsUrl(origin, destination);
        Log.d(TAG, "URL da API: " + url);
        new DirectionsDownloadTask().execute(url);
    }

    private String buildDirectionsUrl(LatLng origin, LatLng dest) {
        return "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + dest.latitude + "," + dest.longitude +
                "&sensor=false" +
                "&mode=driving" +
                "&key=" + DIRECTIONS_API_KEY;
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

    // AsyncTask para download dos dados de direções
    private class DirectionsDownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            try {
                String result = downloadUrl(url[0]);
                Log.d(TAG, "Resposta da API: " + (result != null ? result.substring(0, Math.min(200, result.length())) + "..." : "null"));
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
                    // Verifica se a resposta contém erro
                    JSONObject jsonResponse = new JSONObject(result);
                    String status = jsonResponse.getString("status");
                    Log.d(TAG, "Status da API: " + status);

                    if ("OK".equals(status)) {
                        new DirectionsParserTask().execute(result);
                    } else {
                        String errorMessage = "Erro na API: " + status;
                        if (jsonResponse.has("error_message")) {
                            errorMessage += " - " + jsonResponse.getString("error_message");
                        }
                        Log.e(TAG, errorMessage);
                        Toast.makeText(MapsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Erro ao verificar status da resposta: ", e);
                    Toast.makeText(MapsActivity.this, "Erro ao processar resposta da API", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Resposta vazia da API");
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
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            Log.d(TAG, "Código de resposta HTTP: " + responseCode);

            if (responseCode != HttpURLConnection.HTTP_OK) {
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

    // AsyncTask para parsing dos dados de direções
    private class DirectionsParserTask extends AsyncTask<String, Void, List<LatLng>> {
        @Override
        protected List<LatLng> doInBackground(String... jsonData) {
            try {
                JSONObject jsonObject = new JSONObject(jsonData[0]);
                return parseDirectionsResponse(jsonObject);
            } catch (JSONException e) {
                Log.e(TAG, "Erro no parsing JSON: ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<LatLng> points) {
            if (points != null && !points.isEmpty()) {
                Log.d(TAG, "Pontos da rota obtidos: " + points.size());
                drawRoute(points);
                adjustCameraToRoute(points);
            } else {
                Log.w(TAG, "Nenhum ponto da rota obtido");
                Toast.makeText(MapsActivity.this, "Não foi possível obter rota", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private List<LatLng> parseDirectionsResponse(JSONObject jsonObject) throws JSONException {
        List<LatLng> points = new ArrayList<>();

        JSONArray routes = jsonObject.getJSONArray("routes");
        if (routes.length() == 0) {
            Log.w(TAG, "Nenhuma rota encontrada");
            return points;
        }

        JSONObject route = routes.getJSONObject(0);
        JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
        String encodedPolyline = overviewPolyline.getString("points");

        Log.d(TAG, "Polyline codificada obtida, tamanho: " + encodedPolyline.length());

        points = decodePoly(encodedPolyline);
        Log.d(TAG, "Pontos decodificados: " + points.size());

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
        Log.d(TAG, "Rota desenhada com sucesso!");
        Toast.makeText(this, "Rota carregada!", Toast.LENGTH_SHORT).show();
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

    // Decodificador de polyline melhorado
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                if (index >= len) break;
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                if (index >= len) break;
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng point = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(point);
        }

        return poly;
    }
}