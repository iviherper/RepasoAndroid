package com.example.repasoandroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AccesoFirebase.IRecuperarDatos {

    private GoogleMap mMap;
    private EditText etnombreRuta;
    private Button btnMostrar;
    private Button btnGrabarParar;
    private Spinner spnRutas;
    private boolean grabar = false;
    PolylineOptions poliline = new PolylineOptions();
    private ArrayList<Punto> puntos = new ArrayList<Punto>();
    ArrayList<Ruta> lista_rutas = new ArrayList<Ruta>();
    LocationManager lm;
    LocationListener listener;
    AccesoFirebase.IRecuperarDatos interfaz_recuperardatos=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializarCampos();
        AccesoFirebase.pedirRutas(interfaz_recuperardatos);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    private void inicializarCampos() {
        etnombreRuta = findViewById(R.id.et_nombre);
        btnGrabarParar = findViewById(R.id.btn_grabar_parar);
        btnMostrar = findViewById(R.id.btn_mostrar);
        spnRutas = findViewById(R.id.spn_rutas);

        View.OnClickListener oyente_mostrar = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ruta ruta_selec = (Ruta) spnRutas.getSelectedItem();
                pintarRuta(ruta_selec);
            }
        };
        View.OnClickListener oyente_grabar = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (grabar) {
                    btnGrabarParar.setText(R.string.grabar);
                    lm.removeUpdates(listener);
                    grabarRutaFirebase();
                } else {
                    btnGrabarParar.setText(R.string.parar);
                    chekearPermiso();
                }
                grabar = !grabar;
            }
        };
        btnGrabarParar.setOnClickListener(oyente_grabar);
        btnMostrar.setOnClickListener(oyente_mostrar);

        listener =new LocationListener(){

            @Override
            public void onLocationChanged(@NonNull Location location) {
                Punto p = new Punto(location.getLatitude(),location.getLongitude());
                mostrarPoly(p);
            }
        };
    }

    private void pintarRuta(Ruta ruta_selec) {
        mMap.clear();
        for (Punto p:ruta_selec.getPuntos()
             ) {
            mostrarPoly(p);
        }
    }

    private void mostrarPoly(Punto p) {
        LatLng lat = new LatLng(p.getLat(),p.getLng());
        poliline.add(lat);
        puntos.add(p);
        mMap.addPolyline(poliline);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat, 12));
        Log.d("bien", String.valueOf(p.getLat()+","+p.getLng()));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void chekearPermiso() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Si no tengo permiso, lo pido
            //Si no tengo permiso lo pido
            String[] permisos = {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permisos, 99);
        } else {
            //Pedir actualizaciones
            pedirActualizaciones();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 99) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pedirActualizaciones();
            }
        }
    }

    private void pedirActualizaciones() {
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, listener);
    }

    private void grabarRutaFirebase(){
        String nombre =etnombreRuta.getText().toString();
        Ruta r = new Ruta();
        r.setNombre(nombre);
        r.setPuntos(puntos);
        AccesoFirebase.grabarRuta(r);
    }

    @Override
    public void recuperarRutas(ArrayList<Ruta> rutas) {
        lista_rutas=rutas;
        rellenarSpinner();
    }

    private void rellenarSpinner() {
        ArrayAdapter<Ruta> adaptadorRutas = new ArrayAdapter<Ruta>(this, R.layout.support_simple_spinner_dropdown_item,lista_rutas);
        spnRutas.setAdapter(adaptadorRutas);
    }
}