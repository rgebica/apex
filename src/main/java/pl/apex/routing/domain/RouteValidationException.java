package pl.apex.routing.domain;

/** Zlamany inwariant trasy lub bledne dane (np. brak nazwy, za malo punktow). Mapowane na HTTP 400. */
public class RouteValidationException extends RoutingException {

    public RouteValidationException(String message) {
        super(message);
    }
}
