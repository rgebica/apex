package pl.apex.routing.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.apex.routing.domain.AuthorId;
import pl.apex.routing.domain.GeoPoint;
import pl.apex.routing.domain.Route;
import pl.apex.routing.domain.RouteId;
import pl.apex.routing.domain.exception.RouteNotFoundException;

import java.util.List;

/**
 * Przypadki uzycia kontekstu Route Authoring. Warstwa cienka: wyznacza granice
 * transakcji i orkiestruje agregat, ale regul biznesowych tu nie ma - te zyja
 * w agregacie Route.
 */
@Service
public class RouteAuthoringService {

    private final RouteRepository routes;

    public RouteAuthoringService(RouteRepository routes) {
        this.routes = routes;
    }

    @Transactional
    public RouteId createDraft(AuthorId author, String name, List<GeoPoint> waypoints) {
        Route route = Route.draft(RouteId.newId(), author, name, waypoints);
        routes.save(route);
        return route.id();
    }

    @Transactional
    public void publish(RouteId id) {
        Route route = routes.findById(id)
                .orElseThrow(() -> new RouteNotFoundException(id));
        route.publish();
        routes.save(route);
    }

    @Transactional(readOnly = true)
    public List<Route> listAll() {
        return routes.findAll();
    }
}
