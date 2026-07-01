package pl.apex.routing.domain.exception;

import pl.apex.routing.domain.RouteId;

/** Trasa o danym id nie istnieje. Mapowane na HTTP 404. */
public class RouteNotFoundException extends RoutingException {

    public RouteNotFoundException(RouteId id) {
        super("Trasa nie istnieje: " + id.value());
    }
}
