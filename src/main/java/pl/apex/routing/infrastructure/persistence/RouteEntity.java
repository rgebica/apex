package pl.apex.routing.infrastructure.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import pl.apex.routing.domain.RouteStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Model zapisu (JPA) - osobny od agregatu domenowego. Domena nie wie o adnotacjach
 * persystencji; mapowanie miedzy nimi robi RouteRepositoryAdapter.
 */
@Entity
@Table(name = "routes")
public class RouteEntity {

    @Id
    private UUID id;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RouteStatus status;

    // Waypointy to kolekcja nalezaca do agregatu - osobna tabela route_waypoints,
    // kolejnosc utrwalona kolumna seq.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "route_waypoints", joinColumns = @JoinColumn(name = "route_id"))
    @OrderColumn(name = "seq")
    private List<WaypointEmbeddable> waypoints = new ArrayList<>();

    protected RouteEntity() {
        // wymagane przez JPA
    }

    public RouteEntity(UUID id, UUID authorId, String name, RouteStatus status,
                       List<WaypointEmbeddable> waypoints) {
        this.id = id;
        this.authorId = authorId;
        this.name = name;
        this.status = status;
        this.waypoints = new ArrayList<>(waypoints);
    }

    public UUID getId() {
        return id;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public String getName() {
        return name;
    }

    public RouteStatus getStatus() {
        return status;
    }

    public List<WaypointEmbeddable> getWaypoints() {
        return waypoints;
    }
}
