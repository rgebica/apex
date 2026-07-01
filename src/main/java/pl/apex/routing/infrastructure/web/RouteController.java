package pl.apex.routing.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.apex.routing.application.RouteAuthoringService;
import pl.apex.routing.domain.AuthorId;
import pl.apex.routing.domain.GeoPoint;
import pl.apex.routing.domain.RouteId;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    // Zastepczy autor uzywany, gdy naglowek X-Author-Id nie przyjdzie.
    // W Fazie 4 naglowek wypelni filtr Keycloaka wartoscia 'sub' z tokenu.
    private static final UUID DEV_AUTHOR = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final RouteAuthoringService service;

    public RouteController(RouteAuthoringService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> create(
            @RequestHeader(value = "X-Author-Id", required = false) UUID authorId,
            @RequestBody CreateRouteRequest request) {

        AuthorId author = new AuthorId(authorId != null ? authorId : DEV_AUTHOR);
        List<GeoPoint> waypoints = request.waypoints().stream()
                .map(w -> new GeoPoint(w.latitude(), w.longitude()))
                .toList();

        RouteId id = service.createDraft(author, request.name(), waypoints);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id.value()));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Void> publish(@PathVariable UUID id) {
        service.publish(new RouteId(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<RouteResponse> list() {
        return service.listAll().stream()
                .map(RouteResponse::from)
                .toList();
    }
}
