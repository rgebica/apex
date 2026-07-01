package pl.apex.routing.domain;

import pl.apex.routing.domain.exception.RouteValidationException;

/** Punkt geograficzny - niemutowalny value object. */
public record GeoPoint(double latitude, double longitude) {

    public GeoPoint {
        if (latitude < -90 || latitude > 90) {
            throw new RouteValidationException("Szerokosc geograficzna poza zakresem: " + latitude);
        }
        if (longitude < -180 || longitude > 180) {
            throw new RouteValidationException("Dlugosc geograficzna poza zakresem: " + longitude);
        }
    }
}
