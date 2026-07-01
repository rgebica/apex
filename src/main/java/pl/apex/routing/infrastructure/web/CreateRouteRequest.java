package pl.apex.routing.infrastructure.web;

import java.util.List;

/** Cialo zadania tworzenia trasy. Autor nie jest tu podawany - bierze sie z naglowka. */
public record CreateRouteRequest(String name, List<WaypointDto> waypoints) {
}
