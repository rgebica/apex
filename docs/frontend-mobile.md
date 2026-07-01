# Apex — Mobile frontend spec (Flutter)

Working reference for building the Apex mobile app. Covers the stack, design
system, screens, reusable widgets, and — importantly — the **real backend API
contracts** already implemented, so screens can be wired against them today.

> This revises section 7 of the founding doc (`apex-zalozenia.md`), which
> originally named Angular. The frontend is a **native cross-platform mobile
> app**, not a web app. README's "Planned" line should be updated accordingly.

---

## 1. Stack

| Concern | Choice | Notes |
|---------|--------|-------|
| Framework | **Flutter** (Dart) | One codebase, iOS + Android, native-compiled |
| State mgmt | **Riverpod** | Compile-safe, testable; alternative: Bloc |
| HTTP | **dio** | Interceptors (auth header, logging, error mapping) |
| Models / JSON | **freezed** + **json_serializable** | Immutable models, generated (de)serialization |
| Maps | **flutter_map** | Leaflet-like, any tile server incl. self-hosted MapLibre; custom polyline for the hero route line |
| Location | **geolocator** (foreground) + background geolocation plugin | Live ride recording, GPS pings |
| Camera / EXIF | **image_picker** / **camera** + **exif** | Photo upload with GPS from EXIF |
| Realtime | **web_socket_channel** | Live tracking / "who's following you" |
| Auth (later) | **flutter_appauth** | Keycloak OIDC — Faza 4, not now |

**Why Flutter over React Native:** Apex leans on a bespoke branded UI
(curviness as the hero, immersive recording overlay). Flutter renders its own
widgets, making custom design cheaper and more consistent across platforms than
RN's native-component approach.

---

## 2. Design system

From founding doc §9. Flat surfaces, no gradients, sentence case everywhere,
two font weights only.

### Tokens

| Token | Value | Use |
|-------|-------|-----|
| `brand/coral` | `#D85A30` | Curviness score, primary actions, route line, active nav |
| `surface/0` | `#FFFFFF` | App background |
| `surface/1` | `#F5F3F1` | Cards, sheets |
| `surface/2` | `#ECE8E4` | Insets, pressed states |
| `text/primary` | `#1C1B1A` | Headings, values |
| `text/secondary` | `#6B6560` | Labels, metadata |
| `border` | `#E0DBD6` | Hairlines |
| `success` | `#3E8E5A` | Good conditions (weather ok) |
| `warning` | `#C9922B` | Sharp-corner warnings |

- **Typography:** one family, weights **400 (regular)** and **600 (semibold)**.
  Display sizes for the curviness number; body for everything else.
- **Curviness is the hero:** the score (e.g. `Krętość 9.4`) is rendered large,
  in coral, on every card, thumbnail, and detail header. It is the single most
  emphasised element in any layout.
- **Maps:** schematic, thick route line always visually on top; the corner is
  the star. Same treatment in list thumbnails (simplified) and detail (full).
- **Flat:** minimal elevation, hairline borders over shadows.

---

## 3. Navigation

- **Bottom tab bar** with 4 tabs + a **central protruding "Record" button**
  (thumb reach) that starts a ride.
  - Tabs: **Discover**, **Routes** (mine), **Record** (center), **Profile**, (4th slot reserved, e.g. Notifications later).
- **Immersive recording mode:** when recording, the map goes full-screen; stats
  and corner warnings are overlays; **the tab bar is hidden**.
- Routing: `go_router` with a shell route for the tab scaffold and a separate
  full-screen route for the recording session.

---

## 4. Screens

Numbered per founding doc §10. "Backend" column marks what can be built today
against the live API vs. what waits on a later backend phase.

### 4.1 Discover  *(buildable now)*
- **Purpose:** feed of published community routes.
- **Layout:** vertical list of `RouteCard`s; sort chips (area, popularity, saved).
- **Data:** `GET /api/routes` → filter `status == PUBLISHED` client-side for now.
- **Card shows:** schematic thumbnail, **curviness (hero)**, length, rating, author.
- **States:** loading (skeleton cards), empty ("no routes yet"), error (retry).
- **Backend:** list endpoint ✅. Curviness/length/rating/thumbnail — *planned*
  (Route Analysis, Ratings, Media). Reserve space in the card now.

### 4.2 Route details  *(partially buildable now)*
- **Purpose:** everything about one route + entry to navigation.
- **Layout:** full map with route polyline; stat row (curviness, length, ascent,
  surface); weather-for-planned-time strip; ratings; photos; nav button.
- **Data:** route + waypoints from the list item (`RouteResponse`).
- **Backend:** name + waypoints ✅. Curviness/ascent/surface — *planned*
  (Analysis). Weather — *planned*. Ratings/photos — *planned*.

### 4.3 Create & share route  *(buildable now)*
- **Purpose:** author a route and publish it.
- **Flow:** name → add waypoints (tap on map) → (auto stats later) → visibility →
  publish.
- **Data:**
  - `POST /api/routes` with `{ name, waypoints }` → `{ id }` (creates a DRAFT).
  - `POST /api/routes/{id}/publish` → 204 (DRAFT → PUBLISHED).
- **States:** validation (needs name + ≥2 waypoints — mirrors domain rules),
  submitting, success, conflict (already published → 409).
- **Backend:** create + publish ✅. Surface/description/photos — *planned*.

### 4.4 Profile  *(waits on backend)*
- Avatar, motorcycle, stats, badges, own routes.
- **Backend:** Profile context — *not built* (Faza 4). Author is currently just
  an `authorId` (UUID); no profile data yet.

### 4.5 Live ride recording  *(waits on backend)*
- Full-screen map with live position, real-time stats, sharp-corner warnings,
  followers overlay. Immersive mode (no tab bar).
- **Backend:** Tracking context (GPS ingest, live push, ride recording) —
  *not built*. Needs WebSocket/SSE + likely Kafka (per founding doc §6).

---

## 5. Reusable widgets

| Widget | Purpose |
|--------|---------|
| `CurvinessBadge` | The hero score — large coral number + label. Used on cards, thumbnails, detail header |
| `RouteCard` | Discover feed item: thumbnail + curviness + meta + author |
| `RouteMap` | flutter_map wrapper; schematic style, thick coral polyline, waypoint markers |
| `StatPill` | Single stat (length, ascent, surface) — icon + value |
| `WeatherStrip` | Per-segment forecast for the planned ride time |
| `SortChips` | Discover sort selector (area / popularity / saved) |
| `RecordButton` | Central protruding FAB in the bottom bar |

---

## 6. API integration

Base URL (local dev):
- Android emulator → `http://10.0.2.2:8080`
- iOS simulator → `http://localhost:8080`
- Physical device → host LAN IP, e.g. `http://192.168.x.x:8080`

### Author header
Until Keycloak (Faza 4), the backend takes the author from an **`X-Author-Id`**
header (UUID). The app should send a fixed dev UUID via a dio interceptor; that
exact header is where the Keycloak token subject will plug in later.

### Endpoints implemented today

| Method | Path | Body / Header | Response |
|--------|------|---------------|----------|
| `GET` | `/api/routes` | — | `200` `RouteResponse[]` |
| `POST` | `/api/routes` | `X-Author-Id`; `{ name, waypoints[] }` | `201` `{ id }` |
| `POST` | `/api/routes/{id}/publish` | — | `204` |

Errors: `400 { error }` (bad input / broken invariant), `409 { error }`
(illegal state, e.g. publishing an already-published route).

### DTO shapes (match backend records)

```jsonc
// RouteResponse
{
  "id": "uuid",
  "authorId": "uuid",
  "name": "string",
  "status": "DRAFT" | "PUBLISHED",
  "waypoints": [ { "latitude": 49.41, "longitude": 20.19 } ]
}

// CreateRouteRequest
{
  "name": "string",
  "waypoints": [ { "latitude": 49.41, "longitude": 20.19 } ]
}
```

### Dart model sketch (freezed)

```dart
@freezed
class Route with _$Route {
  const factory Route({
    required String id,
    required String authorId,
    required String name,
    required RouteStatus status,
    required List<Waypoint> waypoints,
    // planned (Route Analysis): curviness, lengthKm, ascentM, surface
  }) = _Route;
  factory Route.fromJson(Map<String, dynamic> json) => _$RouteFromJson(json);
}

@freezed
class Waypoint with _$Waypoint {
  const factory Waypoint({ required double latitude, required double longitude }) = _Waypoint;
  factory Waypoint.fromJson(Map<String, dynamic> json) => _$WaypointFromJson(json);
}

enum RouteStatus { draft, published }
```

### Planned endpoints (backend not built yet — do not call)
Curviness/length/ascent (Analysis), weather, ratings, photos (Media), profiles,
live tracking, auth. Design UI with placeholders; wire as each backend phase lands.

---

## 7. Project structure

Feature-first, mirroring the backend bounded contexts. Each feature keeps the
ports-&-adapters spirit: `data` (API + models) → `domain` (entities + logic) →
`presentation` (widgets + controllers).

```
lib/
├── core/
│   ├── theme/           # tokens, ThemeData
│   ├── network/         # dio client, interceptors (X-Author-Id), error mapping
│   └── router/          # go_router config
├── features/
│   ├── routing/         # Discover, Route details, Create/share  (maps to backend `routing`)
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   ├── analysis/        # curviness display (later)
│   ├── weather/         # forecast strip (later)
│   ├── profile/         # (later)
│   └── tracking/        # live recording (later)
└── main.dart
```

---

## 8. Build order (aligned to backend reality)

1. **Now:** Discover + Create/share + Route details (basic) — the `routing`
   endpoints are live. This gives a working app against the real API immediately.
2. **After Route Analysis:** real curviness/length on cards and detail.
3. **After Weather / Ratings / Media / Profile:** those screens/sections.
4. **After Tracking + Keycloak:** live recording (immersive) + real auth
   (replace the `X-Author-Id` dev header with the Keycloak token subject).

---

## 9. Deferred / open questions

- **Maps:** confirm tile source — self-hosted MapLibre vs. a free raster
  provider. Founding doc wants open-source, no limits → likely self-hosted.
- **Auth:** Keycloak OIDC via `flutter_appauth`, deep-link redirect. Faza 4.
- **Background GPS:** iOS background location needs entitlements + review care;
  pick the background plugin early and test on real devices.
- **Offline:** riding areas have poor signal — cache routes for offline viewing
  (later; consider from the start in the data layer).
