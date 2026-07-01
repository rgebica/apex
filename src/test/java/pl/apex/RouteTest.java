package pl.apex;

import org.junit.jupiter.api.Test;
import pl.apex.routing.domain.AuthorId;
import pl.apex.routing.domain.GeoPoint;
import pl.apex.routing.domain.Route;
import pl.apex.routing.domain.RouteId;
import pl.apex.routing.domain.RouteStatus;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RouteTest {

    private static final AuthorId AUTHOR = AuthorId.of(UUID.randomUUID());

    private Route sampleDraft() {
        return Route.draft(
                RouteId.newId(),
                AUTHOR,
                "Przelecz nad Lapszanka",
                List.of(new GeoPoint(49.41, 20.19), new GeoPoint(49.44, 20.29))
        );
    }

    @Test
    void draftStartsAsDraft() {
        assertEquals(RouteStatus.DRAFT, sampleDraft().status());
    }

    @Test
    void draftRemembersAuthor() {
        assertEquals(AUTHOR, sampleDraft().authorId());
    }

    @Test
    void publishMovesDraftToPublished() {
        Route route = sampleDraft();
        route.publish();
        assertEquals(RouteStatus.PUBLISHED, route.status());
    }

    @Test
    void cannotPublishTwice() {
        Route route = sampleDraft();
        route.publish();
        assertThrows(IllegalStateException.class, route::publish);
    }

    @Test
    void routeNeedsAuthor() {
        assertThrows(IllegalArgumentException.class, () ->
                Route.draft(RouteId.newId(), null, "Bez autora",
                        List.of(new GeoPoint(49.4, 20.1), new GeoPoint(49.5, 20.2))));
    }

    @Test
    void routeNeedsAtLeastTwoWaypoints() {
        assertThrows(IllegalArgumentException.class, () ->
                Route.draft(RouteId.newId(), AUTHOR, "Za krotka", List.of(new GeoPoint(49.4, 20.1))));
    }
}
