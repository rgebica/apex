package pl.apex.routing.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Pojedynczy punkt trasy w zapisie - wiersz tabeli route_waypoints. */
@Embeddable
public class WaypointEmbeddable {

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    protected WaypointEmbeddable() {
        // wymagane przez JPA
    }

    public WaypointEmbeddable(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
