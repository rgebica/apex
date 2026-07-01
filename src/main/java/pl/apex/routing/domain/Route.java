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
    private final AuthorId authorId;
    private String name;
    private final List<GeoPoint> waypoints;
    private RouteStatus status;

    private Route(RouteId id, AuthorId authorId, String name, List<GeoPoint> waypoints, RouteStatus status) {
        this.id = id;
        this.authorId = authorId;
        this.name = name;
        this.waypoints = new ArrayList<>(waypoints);
        this.status = status;
    }

    /** Fabryka wymusza inwarianty juz przy tworzeniu szkicu. */
    public static Route draft(RouteId id, AuthorId authorId, String name, List<GeoPoint> waypoints) {
        if (authorId == null) {
            throw new RouteValidationException("Trasa musi miec autora");
        }
        if (name == null || name.isBlank()) {
            throw new RouteValidationException("Trasa musi miec nazwe");
        }
        if (waypoints == null || waypoints.size() < 2) {
            throw new RouteValidationException("Trasa potrzebuje co najmniej dwoch punktow");
        }
        return new Route(id, authorId, name, waypoints, RouteStatus.DRAFT);
    }

    /**
     * Odtworzenie agregatu z zapisu (persystencja) - dane juz raz przeszly
     * walidacje przy tworzeniu, wiec nie wymuszamy jej ponownie i pozwalamy
     * odtworzyc dowolny status (np. PUBLISHED wczytany z bazy).
     */
    public static Route rehydrate(RouteId id, AuthorId authorId, String name,
                                  List<GeoPoint> waypoints, RouteStatus status) {
        return new Route(id, authorId, name, waypoints, status);
    }

    /** Publikacja - dostepna tylko dla szkicu. Tu w Fazie 2 powstanie event RouteShared. */
    public void publish() {
        if (status != RouteStatus.DRAFT) {
            throw new RouteStateException("Opublikowac mozna tylko szkic trasy");
        }
        this.status = RouteStatus.PUBLISHED;
    }

    public void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new RouteValidationException("Nazwa nie moze byc pusta");
        }
        this.name = newName;
    }

    public RouteId id() {
        return id;
    }

    public AuthorId authorId() {
        return authorId;
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
