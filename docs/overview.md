# The Angels App Overview

The Angels App is a mobile-first emergency response platform built for the "Angels" volunteer network. The Android client empowers community members to request immediate assistance, coordinate volunteer dispatch, and access educational content directly from their devices. A complementary Firebase backend with Cloud Functions automates notifications, analytics, and integrations with external tools.

## Who Is It For?

- **Community members** who need to open an incident report, monitor live volunteer status, and stay informed through in-app messages.
- **Volunteers** who accept missions, navigate using live routing, update their availability, and close events once resolved.
- **Coordinators** who rely on automated webhooks, static-map summaries, and analytics exports provided by the Cloud Functions layer.

## Key Features

- **Event lifecycle management** – guided incident creation flows, volunteer claiming, live status transitions, and structured closure forms.
- **Real-time mapping** – Google Maps based map fragments, volunteer tracking overlays, and foreground location updates for ETA calculations.
- **Personalized home feed** – role-aware dashboards, event widgets, targeted announcements, and curated educational resources.
- **Emergency contact tools** – quick access cards, contact CRUD utilities, and configurable notification preferences.
- **Automated messaging** – OneSignal push integration, Firestore-driven message styling, and in-app banner components.
- **Cloud automation** – Firestore triggers that call external webhooks, build static map images, and persist AI analysis artifacts.

## Technology Stack

| Layer | Technologies |
| --- | --- |
| Mobile client | Android (Java), AndroidX, Material Design components, Glide, Retrofit |
| Mapping & location | Google Maps SDK, Places API, Maps Utils, Fused Location Provider |
| Backend | Firebase Authentication, Cloud Firestore, Cloud Functions for Firebase, Axios |
| Messaging | OneSignal SDK, Firebase Cloud Messaging |
| Tooling | Gradle 8.4, Java 17, Android ViewBinding |

## Repository Layout (High Level)

```
app/
  src/main/
    java/co/median/android/a2025_theangels_new/
      data/        # Models, managers, map helpers, application class
      ui/          # Activities, fragments, adapters grouped by feature area
      util/        # Cross-cutting helpers (styling, timers, uploads)
    res/           # Layouts, drawables, strings (Hebrew-first UX)
functions/         # Firebase Cloud Functions (webhook & analytics automation)
```
