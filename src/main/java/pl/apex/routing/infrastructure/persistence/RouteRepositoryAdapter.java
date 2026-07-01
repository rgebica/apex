package pl.apex.routing.infrastructure.persistence;

import org.springframework.stereotype.Component;
import pl.apex.routing.application.RouteRepository;
import pl.apex.routing.domain.AuthorId;
import pl.apex.routing.domain.GeoPoint;
import pl.apex.routing.domain.Route;
import pl.apex.routing.domain.RouteId;

import java.util.List;
import java.util.Optional;

/**
 * Adapter - laczy port domenowy (RouteRepository) z JPA. Tlumaczy w obie strony
 * miedzy agregatem Route a RouteEntity, dzieki czemu domena zostaje czysta.
 */
@Component
public class RouteRepositoryAdapter implements RouteRepository {

    private final JpaRouteRepository jpa;

    public RouteRepositoryAdapter(JpaRouteRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Route route) {
        jpa.save(toEntity(route));
    }

    @Override
    public Optional<Route> findById(RouteId id) {
        return jpa.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<Route> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    private RouteEntity toEntity(Route route) {
        List<WaypointEmbeddable> points = route.waypoints().stream()
                .map(p -> new WaypointEmbeddable(p.latitude(), p.longitude()))
                .toList();
        return new RouteEntity(
                route.id().value(),
                route.authorId().value(),
                route.name(),
                route.status(),
                points);
    }

    private Route toDomain(RouteEntity entity) {
        List<GeoPoint> points = entity.getWaypoints().stream()
                .map(w -> new GeoPoint(w.getLatitude(), w.getLongitude()))
                .toList();
        return Route.rehydrate(
                new RouteId(entity.getId()),
                new AuthorId(entity.getAuthorId()),
                entity.getName(),
                points,
                entity.getStatus());
    }
}
