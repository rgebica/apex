package pl.apex.routing.domain.exception;

/** Niedozwolona zmiana stanu trasy (np. publikacja czegos, co nie jest szkicem). Mapowane na HTTP 409. */
public class RouteStateException extends RoutingException {

    public RouteStateException(String message) {
        super(message);
    }
}
