package pl.apex.routing.domain;

import org.junit.jupiter.api.Test;
import pl.apex.routing.domain.exception.RouteStateException;
import pl.apex.routing.domain.exception.RouteValidationException;

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
        assertThrows(RouteStateException.class, route::publish);
    }

    @Test
    void routeNeedsAuthor() {
        assertThrows(RouteValidationException.class, () ->
                Route.draft(RouteId.newId(), null, "Bez autora",
                        List.of(new GeoPoint(49.4, 20.1), new GeoPoint(49.5, 20.2))));
    }

    @Test
    void routeNeedsAtLeastTwoWaypoints() {
        assertThrows(RouteValidationException.class, () ->
                Route.draft(RouteId.newId(), AUTHOR, "Za krotka", List.of(new GeoPoint(49.4, 20.1))));
    }
}
