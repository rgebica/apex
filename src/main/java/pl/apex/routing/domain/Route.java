package pl.apex.routing.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Agregat trasy - jedyne wejscie do modyfikacji trasy.
 * Reguly biznesowe (inwarianty) zyja w metodach agregatu, nie w serwisie.
 */
public class Route {

    private final RouteId id;
    private String name;
    private final List<GeoPoint> waypoints;
    private RouteStatus status;

    private Route(RouteId id, String name, List<GeoPoint> waypoints) {
        this.id = id;
        this.name = name;
        this.waypoints = new ArrayList<>(waypoints);
        this.status = RouteStatus.DRAFT;
    }

    /** Fabryka wymusza inwarianty juz przy tworzeniu szkicu. */
    public static Route draft(RouteId id, String name, List<GeoPoint> waypoints) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Trasa musi miec nazwe");
        }
        if (waypoints == null || waypoints.size() < 2) {
            throw new IllegalArgumentException("Trasa potrzebuje co najmniej dwoch punktow");
        }
        return new Route(id, name, waypoints);
    }

    /** Publikacja - dostepna tylko dla szkicu. Tu w Fazie 2 powstanie event RouteShared. */
    public void publish() {
        if (status != RouteStatus.DRAFT) {
            throw new IllegalStateException("Opublikowac mozna tylko szkic trasy");
        }
        this.status = RouteStatus.PUBLISHED;
    }

    public void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Nazwa nie moze byc pusta");
        }
        this.name = newName;
    }

    public RouteId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public RouteStatus status() {
        return status;
    }

    public List<GeoPoint> waypoints() {
        return Collections.unmodifiableList(waypoints);
    }
}
