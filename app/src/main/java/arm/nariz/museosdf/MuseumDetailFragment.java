package arm.nariz.museosdf;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import arm.nariz.museosdf.model.MuseumsContent;

/**
 * A fragment representing a single Museum detail screen.
 * This fragment is either contained in a {@link MuseumListActivity}
 * in two-pane mode (on tablets) or a {@link MuseumDetailActivity}
 * on handsets.
 */
public class MuseumDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private String TAG = "MuseumDetailFragment";
    private String loc = "";

    /**
     * The dummy content this fragment is presenting.
     */
    private MuseumsContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MuseumDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {

            Bundle b = getArguments();
            if( b != null ){
                loc = b.getString("latlong");
                if(loc!=null) Log.e( TAG + ": LatLng", loc);
            } else Log.e( TAG + ": Extras", "No se encontraron Extras");

            MuseumsContent myD = new MuseumsContent( loc ); //Buscar en SQLite
            try{Thread.sleep(3000);}catch (InterruptedException ie){ ie.printStackTrace();}
            mItem = myD.getITEM_MAP().get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.title);
            }
        } else Log.e(TAG, "No cintiene " + ARG_ITEM_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.museum_detail, container, false);
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.museum_detail)).setText(mItem.details);
        }
        return rootView;
    }
}
