package pl.apex.routing.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Spring Data JPA - gotowe CRUD-y na RouteEntity. */
public interface JpaRouteRepository extends JpaRepository<RouteEntity, UUID> {
}
