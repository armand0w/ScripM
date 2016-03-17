package arm.nariz.museosdf;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import arm.nariz.museosdf.controller.Utils;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final String TAG = "MapsActivity";
    private LatLng myLoc = null;
    private String mode = "";
    private Double ori_lat = 0.0;
    private Double ori_lon = 0.0;
    private String destino = "";
    private View view = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        view = findViewById(android.R.id.content);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle b = getIntent().getExtras();
        if( b != null ){
            mode = b.getString("mode", "driving");
            ori_lat = b.getDouble("ori_lat", 19.541902);
            ori_lon = b.getDouble("ori_lon", -99.203731);
            myLoc = new LatLng(ori_lat, ori_lon);
            destino = b.getString("destino", "19.434740,-99.140809");
        } else Log.e("Extras", "No se encontraron Extras");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        if(myLoc!=null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14.395f), 2000, null);
        }

        if( ori_lon!=0.0 && ori_lat!=0.0 ){
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(ori_lat, ori_lon))
                    .title("Inicio")
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        }

        if( destino.length()>0 ){
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(destino.split(",")[0]),
                            Double.parseDouble(destino.split(",")[1])))
                    .title("Museo")
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        }

        if( view != null )
            Snackbar.make(view, "Esperando ruta", Snackbar.LENGTH_LONG).setAction("Cancelar", null).show();

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                String ruta = "";
                if( Utils.isConnectingToInternet(getApplicationContext()) ) {
                    if(ori_lon!=0.0 && ori_lat!=0.0) {
                        try {
                            ruta = new GetRuta().execute().get();
                        } catch (InterruptedException | ExecutionException ie) {
                            ie.printStackTrace();
                        }

                        if (ruta.length() > 0) {
                            List<LatLng> lines = Utils.decode(ruta);
                            mMap.addPolyline(new PolylineOptions().addAll(lines).color(Color.BLUE).width(3.7f));

                            for( LatLng l: Utils.decode(ruta) ){
                                mMap.addCircle(new CircleOptions()
                                        .center(l)
                                        .radius(0.3) // In meters
                                        .fillColor(Color.BLACK)
                                        .strokeColor(Color.MAGENTA));
                            }
                        } else Log.e("Points", "No se encontro ruta");
                    } else Log.e("GPS", "Sin coordenadas");
                } else Log.e("Conexion", "Sin conexion");
            }
        });
    }


    protected class GetRuta extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            String response = "";

            String urlDirections = "https://maps.googleapis.com/maps/api/directions/json?";
            urlDirections += "origin=" + ori_lat + "," + ori_lon ;
            urlDirections += "&destination=" + destino;
            urlDirections += "&sensor=false&units=metric&languaje=es";
            urlDirections += "&mode=" + mode;
            Log.i(TAG + ": URL", urlDirections);

            try {
                JSONObject jsonObject = Utils.getJSONFromURL( urlDirections );
                if( jsonObject != null && jsonObject.getString("status").equals("OK")){
                    Log.e("JSON", jsonObject.toString());
                    JSONArray routes = jsonObject.getJSONArray("routes");
                    JSONObject poly = routes.getJSONObject(0).getJSONObject("overview_polyline");
                    response = poly.getString("points");
                }
            } catch ( JSONException jse ){
                jse.printStackTrace();
            }

            return response;
        }
    }

}
