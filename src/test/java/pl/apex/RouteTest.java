package pl.apex;

import org.junit.jupiter.api.Test;
import pl.apex.routing.domain.GeoPoint;
import pl.apex.routing.domain.Route;
import pl.apex.routing.domain.RouteId;
import pl.apex.routing.domain.RouteStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteTest {

    private Route sampleDraft() {
        return Route.draft(
                RouteId.newId(),
                "Przelecz nad Lapszanka",
                List.of(new GeoPoint(49.41, 20.19), new GeoPoint(49.44, 20.29))
        );
    }

    @Test
    void draftStartsAsDraft() {
        assertEquals(RouteStatus.DRAFT, sampleDraft().status());
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
    void routeNeedsAtLeastTwoWaypoints() {
        assertThrows(IllegalArgumentException.class, () ->
                Route.draft(RouteId.newId(), "Za krotka", List.of(new GeoPoint(49.4, 20.1))));
    }
}
