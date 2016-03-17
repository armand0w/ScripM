package arm.nariz.museosdf.model;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arm.nariz.museosdf.controller.Utils;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class MuseumsContent {

    private static String TAG = "MuseumsContent";
    public final List<DummyItem> ITEMS = new ArrayList<DummyItem>();//An array of sample (dummy) items.
    public final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>(); //A map of sample (dummy) items, by ID.
    public static final String apiKey = "AIzaSyBXkD485Luv8zOVWRVTGXFv0eLGsBNtQhQ";
    private static JSONArray jsonarr = null;
    private String latlong = "";

    public MuseumsContent(String ll){
        this.latlong = ll;
        new GetNearPlaces().execute();
    }

    public List<DummyItem> getITEMS() { return ITEMS; }

    public Map<String, DummyItem> getITEM_MAP() { return ITEM_MAP; }

    private void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position) {
        String name = "";
        String destiny = "";
        try {
            name = jsonarr.getJSONObject(position).getString("name") + " ";
            destiny = jsonarr.getJSONObject(position).getJSONObject("geometry").getJSONObject("location").getString("lat")
                    + "," + jsonarr.getJSONObject(position).getJSONObject("geometry").getJSONObject("location").getString("lng");
        }catch (JSONException j){
            j.printStackTrace();
        }
        return new DummyItem(String.valueOf(position+1), name, makeDetails(position), destiny);
    }

    private static String makeDetails(int position) {
        String vicinity = "";
        try{
            vicinity = jsonarr.getJSONObject(position).getString("vicinity");
        }catch (JSONException je){
            je.printStackTrace();
        }
        return vicinity;
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String title;
        public final String details;
        public final String dest;

        public DummyItem(String id, String content, String details, String des) {
            this.id = id;
            this.title = content;
            this.details = details;
            this.dest = des;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    protected class GetNearPlaces extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings){
            String urlPlaces = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
            urlPlaces += "location=" + latlong;
            urlPlaces += "&radius=1300" +
                    "&types=museum" +
                    "&sensor=true" +
                    "&key=" + apiKey;
            Log.e( TAG + ": URL = ", urlPlaces);
            try {
                JSONObject jsonObject = Utils.getJSONFromURL( urlPlaces );
                if( jsonObject != null && jsonObject.getString("status").equals("OK") ){
                    jsonarr = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonarr.length(); i++) {
                        addItem(createDummyItem(i));
                    }
                }
            } catch ( JSONException jse ){
                jse.printStackTrace();
            }
            return null;
        }
    }

}
