package arm.nariz.museosdf.model;

import com.google.android.gms.maps.model.LatLng;

import java.net.URL;

/**
 * Created by ACsatillo on 25/11/2015.
 */
public class Museo {
    private LatLng location = null;
    private URL icon = null;
    private String id = "";
    private String name = "";
    private String placeId = "";
    private Double rating = 0.0;
    private String vicinity = "";

    public Museo(LatLng location, URL icon, String id, String name, String placeId, Double rating, String vicinity) {
        this.location = location;
        this.icon = icon;
        this.id = id;
        this.name = name;
        this.placeId = placeId;
        this.rating = rating;
        this.vicinity = vicinity;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public URL getIcon() {
        return icon;
    }

    public void setIcon(URL icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    @Override
    public String toString() {
        return "Museo{" +
                "location=" + location +
                ", icon=" + icon +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", placeId='" + placeId + '\'' +
                ", rating=" + rating +
                ", vicinity='" + vicinity + '\'' +
                '}';
    }
}
