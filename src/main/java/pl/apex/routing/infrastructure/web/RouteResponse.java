package pl.apex.routing.infrastructure.web;

import pl.apex.routing.domain.Route;

import java.util.List;
import java.util.UUID;

/** Reprezentacja trasy zwracana przez API. */
public record RouteResponse(
        UUID id,
        UUID authorId,
        String name,
        String status,
        List<WaypointDto> waypoints) {

    public static RouteResponse from(Route route) {
        List<WaypointDto> points = route.waypoints().stream()
                .map(p -> new WaypointDto(p.latitude(), p.longitude()))
                .toList();
        return new RouteResponse(
                route.id().value(),
                route.authorId().value(),
                route.name(),
                route.status().name(),
                points);
    }
}
