package arm.nariz.museosdf;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import arm.nariz.museosdf.controller.Utils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int PLACE_PICKER_REQUEST = 1;
    private final String TAG = "MainActivity";
    private static final int REQUEST_ERROR = 0;
    private TextView tvLocation = null;
    private Button btnMuse = null;
    private Button btnNear = null;
    private ToggleButton tgBtn = null;
    private String mode = "driving";
    private LatLng mLoc = null;
    private Double ori_lat = 0.0;
    private Double ori_lon = 0.0;
    private Activity activity = null;
    private SharedPreferences pref = null;
    private String museoDestino = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView tv = (TextView)headerView.findViewById(R.id.txtMainName);
        pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        tv.setText( pref.getString("example_text", "Cambiar nombre en ajustes") );
        activity = this;

        Bundle b = getIntent().getExtras();
        if( b != null ){
            museoDestino = b.getString("museodestino");
            if(museoDestino!=null) Log.e( TAG + ": destino", museoDestino);
        } else Log.e(TAG + ": Extras", "No se encontro museodestino");

        tvLocation = (TextView) findViewById(R.id.txtFrom);
        btnMuse = (Button) findViewById(R.id.btn_museum);
        btnNear = (Button) findViewById(R.id.btn_mus_near);
        tgBtn = (ToggleButton) findViewById(R.id.tgbtnLocation);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inte = new Intent( getApplicationContext(), MapsActivity.class );
                inte.putExtra("mode", mode);
                if(ori_lat==0.0 && ori_lon==0.0) {
                    mLoc = Utils.actualizarPosicion(getApplicationContext(), activity);
                    if(mLoc!=null){
                        ori_lat = mLoc.latitude;
                        ori_lon = mLoc.longitude;
                    }
                }
                inte.putExtra("ori_lat", ori_lat);
                inte.putExtra("ori_lon", ori_lon);
                inte.putExtra("destino", museoDestino);
                startActivity( inte );
            }
        });

        btnMuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });

        btnNear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent( getApplicationContext(), MuseumListActivity.class ));

                Intent inte = new Intent( getApplicationContext(), MuseumListActivity.class );
                if(ori_lat==0.0 && ori_lon==0.0) {
                    mLoc = Utils.actualizarPosicion(getApplicationContext(), activity);
                    inte.putExtra("location", mLoc);

                    /*pref = activity.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("latitud", String.valueOf(mLoc.latitude) );
                    editor.putString("longitud", String.valueOf(mLoc.longitude) );
                    editor.apply();*/
                }
                startActivity( inte );
            }
        });

        tgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tgBtn.isChecked()) {
                    mLoc = Utils.actualizarPosicion(getApplicationContext(), activity);
                    if(tvLocation!=null && mLoc!=null) {
                        tvLocation.setText(mLoc.toString());
                        ori_lat = mLoc.latitude;
                        ori_lon = mLoc.longitude;
                    }
                } else {
                    mLoc = null;
                    if(tvLocation!=null) tvLocation.setText("Selecciona un lugar.");
                }
            }
        });
    }

    public void onModeClicked(View view) {
        switch (view.getId()) {
            case R.id.radio_auto:
                mode = "driving";
                break;
            case R.id.radio_walk:
                mode = "walking";
                break;
            case R.id.radio_bicy:
                mode = "bicycling";
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPickButtonClick(View v) {
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            final Place place = PlacePicker.getPlace(data, this);
            final CharSequence name = place.getName();
            //Log.e("Place", place.toString());
            if(tvLocation!=null) tvLocation.setText(name);
            if(tgBtn!=null) tgBtn.setEnabled(false);

            ori_lat = place.getLatLng().latitude;
            ori_lon = place.getLatLng().longitude;
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = GooglePlayServicesUtil
                    .getErrorDialog(errorCode, this, REQUEST_ERROR,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog){
                                    // Leave if services are unavailable.
                                    finish();
                                }
                            });
            errorDialog.show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_near) {
            startActivity(new Intent(getApplicationContext(), MuseumListActivity.class));
        } else if (id == R.id.nav_find) {
            //Mostrar los guardados en local
        } else if (id == R.id.nav_map) {

        } else if (id == R.id.nav_manage) {
            //startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
