# Architecture

The Angels platform couples an Android client with Firebase-managed backend services. The codebase is organised into layered packages that separate presentation logic, data access, and platform integrations.

## High-Level Diagram

```
+-----------------------+       +-------------------+      +----------------------------+
| Android UI Layer      |       | Data / Service    |      | Firebase Backend           |
| (Activities, Fragments| <---> | Managers & Models | <--> | Auth • Firestore • Storage |
|  Adapters)            |       | (Firestore, Maps) |      | Cloud Functions • FCM      |
+-----------------------+       +-------------------+      +----------------------------+
        |                                                        |
        v                                                        v
  Google Maps SDK,                                        Make.com automations,
  OneSignal SDK                                           OneSignal push delivery
```

## Mobile Client Layers

### `ui/`

- Feature-specific packages (`home`, `events`, `educations`, `profile`, `registration`, `onboarding`, `main`).
- Each screen extends `BaseActivity`, which provides immersive UI mode, top/bottom bars, and emergency FAB behaviour.
- RecyclerView adapters (e.g. `MessagesAdapter`, `OpenEventsAdapter`, `RecentEventsAdapter`) render dynamic content sourced from Firestore.

### `data/`

- **Models**: Plain Java objects annotated with `@DocumentId` map Firestore documents (`Event`, `Education`, `Message`, `UserSession`, etc.).
- **Services**: Coordinators that wrap Firestore queries and updates (`EventDataManager`, `UserDataManager`, `EducationDataManager`, `EmergencyContactManager`, `MessageDataManager`).
- **Map utilities**: Custom fragments (`HomeMapFragment`, `StaticMapFragment`), helpers (`MapRouteManager`, `MapHelper`, `DirectionsApiClient`), and background services (`LocationUpdateService`) built on Google Maps and Fused Location Provider.
- **Widgets**: App widgets like `EmergencyWidget` and `MapWidget` expose quick actions.
- `MyApplication` bootstraps Firebase, OneSignal logging, and notification channels at app start.

### `util/`

Cross-cutting helpers for styling (`MessageStyleMapper`), animations (`TimerUtils`), and storage uploads (`ImageUploadUtils`).

## Navigation & State

- `SplashActivity` routes users to onboarding or authentication depending on SharedPreferences flags.
- `MainActivity` hosts the primary navigation shell and delegates to feature activities via the bottom navigation bar.
- `ActiveEventManager` listens for Firestore changes to highlight ongoing events and toggle the emergency FAB state across screens.
- `UserSession` stores the logged-in user profile, synchronises OneSignal tags, and exposes convenience getters for the UI.

## Event Lifecycle Flow

1. **Creation** – `NewEventActivity` orchestrates a multi-step wizard (`EventTypeFragment`, `QuestionnaireFragment`, `LocationFragment`, `SummaryFragment`). `NewEventViewModel` retains form state and calls `EventDataManager.createNewEvent` when submitted.
2. **Dispatch** – `EventDataManager` transitions `eventStatus` as volunteers claim (`claimEvent`), travel (`updateVolunteerLocation`, `updateVolunteerETA`), and close events.
3. **Monitoring** – `HomeActivity` listens for open events (`listenToActiveEvents`) and renders them via cards and a map overlay (`HomeMapFragment`, `MapRouteManager`).
4. **Closure** – Volunteer-facing fragments (`VolStatusFragment`, `VolCloseFragment`) update Firestore with ratings, closure reasons, and analysis text.

## Cloud Functions Module

Located under `functions/` with a Node 18 runtime:

- `notifyVolunteerAssigned` – Firestore `events/{eventId}` update trigger that pushes volunteer assignment payloads to Make.com via webhook.
- `notifyNewEventCreated` – Firestore create trigger that enriches event details with Static Map imagery and OneSignal metadata before calling Make.com.
- `saveEventAnalysis` – HTTPS callable endpoint that persists AI-generated analysis fields onto an event document (uses `merge: true`).
- `notifyVolunteerRegistered` – Firestore user update trigger that fires when a profile switches to the volunteer role.

Functions reuse a single `admin.initializeApp()` guard to avoid duplicate initialisation inside the emulator.

## External Integrations

- **Google Maps Platform**: Keys injected via Gradle resource values feed Maps SDK, Places Autocomplete, Static Maps, and Directions API client.
- **OneSignal**: Configured in `UserDataManager`/`UserSession` for login, tag assignment (`city`, `role`), and logout cleanup.
- **Make.com Webhooks**: Cloud Functions post JSON payloads for downstream automation (notifications, analytics exports).

## Build & Tooling

- Gradle version catalog (`gradle/libs.versions.toml`) centralises dependency versions with Hebrew inline documentation.
- The `generateJavadoc` Gradle task compiles JavaDoc into `app/build/docs/javadoc` for API browsing.
- Firebase CLI commands encapsulated in `functions/package.json` (`serve`, `deploy`, `logs`) streamline backend operations.
