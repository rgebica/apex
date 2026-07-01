package pl.apex.routing.domain;

import java.util.UUID;

/** Identyfikator trasy - value object owijajacy UUID. */
public record RouteId(UUID value) {

    public RouteId {
        if (value == null) {
            throw new IllegalArgumentException("RouteId nie moze byc null");
        }
    }

    public static RouteId newId() {
        return new RouteId(UUID.randomUUID());
    }
}
