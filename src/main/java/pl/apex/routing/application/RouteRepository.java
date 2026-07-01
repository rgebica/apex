package pl.apex.routing.application;

import pl.apex.routing.domain.Route;
import pl.apex.routing.domain.RouteId;

import java.util.List;
import java.util.Optional;

/**
 * Port (wyjscie) - kontrakt persystencji zdefiniowany po stronie domeny/aplikacji.
 * Implementacja (adapter JPA) zyje w infrastrukturze - zaleznosc wskazuje do wewnatrz.
 */
public interface RouteRepository {

    void save(Route route);

    Optional<Route> findById(RouteId id);

    List<Route> findAll();
}
