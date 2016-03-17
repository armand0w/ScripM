package arm.nariz.museosdf.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import arm.nariz.museosdf.model.MuseumsContent;

/**
 * Created by ACastillo on 25/11/2015.
 */
public abstract class Utils {

    public static final LatLng ubicacion = null;
    public static final MuseumsContent museos = null;

    public static JSONObject getJSONFromURL(String url){
        JSONObject json = null;
        URL url_route;
        HttpURLConnection connection = null;
        StringBuilder sb = null;
        BufferedReader rd = null;

        try {
            url_route = new URL( url );

            connection = (HttpURLConnection)url_route.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setReadTimeout(5000);

            connection.connect();

            rd  = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            sb = new StringBuilder();

            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
        }catch( MalformedURLException mue){
            Log.e("URLError", mue.getLocalizedMessage());
            mue.printStackTrace();
        }catch(IOException ioe){
            Log.e("IOError", ioe.getLocalizedMessage());
            ioe.printStackTrace();
        } finally {
            if(connection!=null) connection.disconnect();
            try{ if(rd!=null)rd.close(); } catch (IOException io){ io.printStackTrace();}
        }

        if(sb!=null) {
            try {
                json = new JSONObject(sb.toString());
            } catch (JSONException je) {
                je.printStackTrace();
            }
        } else { try{json = new JSONObject("{}"); }catch (JSONException je){je.printStackTrace();}}

        return json;
    }

    public static boolean isConnectingToInternet(Context con){
        ConnectivityManager connectivity = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo nInfo = connectivity.getActiveNetworkInfo();
            if( nInfo!=null && nInfo.getState() == NetworkInfo.State.CONNECTED ){
                return true;
            }
        }
        return false;
    }

    public static LatLng actualizarPosicion(Context mContext, Activity act) {
        Location location = null;
        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        LocationManager locationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        try {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }catch (SecurityException se){
            se.printStackTrace();
        }

        if( location!=null ) return new LatLng(location.getLatitude(), location.getLongitude());
        else return null;
    }

    public static List<LatLng> decode(final String encodedPath) {
        int len = encodedPath.length();

        final List<LatLng> path = new ArrayList<LatLng>();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;
    }

}
