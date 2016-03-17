package arm.nariz.museosdf;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

import arm.nariz.museosdf.controller.Utils;

/**
 * An activity representing a single Museum detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MuseumListActivity}.
 */
public class MuseumDetailActivity extends AppCompatActivity {

    private String TAG = "MuseumDetailActivity";
    private String destino = "";
    private String ubicacion = "";
    private LatLng ubnLatLng = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Destino: " + destino, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                mainIntent.putExtra("museodestino", destino);
                startActivity(mainIntent);
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ubnLatLng = Utils.actualizarPosicion(getApplicationContext(), this);

        Bundle b = getIntent().getExtras();
        if( b != null ){
            destino = b.getString("destino");
            if(destino!=null) Log.e( TAG + ": destino", destino);
        } else Log.e( TAG + ": Extras", "No se encontraro destino");

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(MuseumDetailFragment.ARG_ITEM_ID, getIntent().getStringExtra(MuseumDetailFragment.ARG_ITEM_ID));
            arguments.putString( "latlong", ubnLatLng.latitude + "," + ubnLatLng.longitude );
            arguments.putString( "destino", destino );

            MuseumDetailFragment fragment = new MuseumDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.museum_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
