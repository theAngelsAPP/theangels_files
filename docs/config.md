# Configuration Guide

This document explains how runtime secrets, build switches, and third-party services are configured within the Angels project.

## Android Build Configuration

| File | Purpose | Key Entries |
| --- | --- | --- |
| `build.gradle` | Top-level Gradle plugins + secrets bootstrap | Loads `GOOGLE_MAPS_API_KEY`, `GOOGLE_PLACES_API_KEY` from `secrets.properties` or environment variables. |
| `app/build.gradle` | Android module configuration | Sets `compileSdk=35`, `minSdk=24`, Java 17 compatibility, ViewBinding enabled, dependency catalog usage, `generateJavadoc` task. |
| `gradle/libs.versions.toml` | Version catalog | Centralises dependency versions with inline Hebrew annotations. |
| `gradle.properties` | Global Gradle flags | Configures `org.gradle.jvmargs`, `android.useAndroidX`, `android.nonTransitiveRClass=true`. |
| `local.properties` | Local SDK path | Generated per developer; should not be committed. |

### Resource Injection

`app/build.gradle` maps secrets into Android resources for use in XML or Java:

```groovy
def mapsKey = project.findProperty("GOOGLE_MAPS_API_KEY") ?: ""
resValue "string", "google_maps_key", mapsKey
```

Use `getString(R.string.google_maps_key)` inside activities/fragments to access the runtime value.

## Secrets & Environment Variables

| Secret | Where Used | Description |
| --- | --- | --- |
| `GOOGLE_MAPS_API_KEY` | Android client + Cloud Functions | Enables Maps SDK, Places API, Static Maps, and Directions API requests. |
| `GOOGLE_PLACES_API_KEY` | Android client | Overrides Maps key for Places Autocomplete (falls back to Maps key if absent). |
| `MAKE_VOLUNTEER_ASSIGNED_WEBHOOK_URL` | Cloud Function `notifyVolunteerAssigned` | Make.com webhook for volunteer dispatch events. |
| `MAKE_NEW_EVENT_WEBHOOK_URL` | Cloud Function `notifyNewEventCreated` | Automation pipeline for new events. |
| `MAKE_VOLUNTEER_REGISTERED_WEBHOOK_URL` | Cloud Function `notifyVolunteerRegistered` | Notifies workflows when a member becomes a volunteer. |

Store the webhook URLs using `firebase functions:config:set make.<name>=...` or `.runtimeconfig.json` when emulating. For local Android builds use `secrets.properties` or environment variables.

## Firebase Project Requirements

Enable the following services in the Firebase Console for the project connected to this codebase:

- **Authentication** – Email/Password (or alternative) for user login.
- **Cloud Firestore** – Native mode database hosting events, users, messages, contacts, and education content.
- **Cloud Functions** – Deploy Node 18 runtime located under `functions/`.
- **Cloud Messaging** – Required by OneSignal and Firebase messaging libraries.
- **Storage (optional)** – Used by `ImageUploadUtils` for profile photos and documents.

Place your `google-services.json` in `app/` to align the Android client with the Firebase project.

## OneSignal Configuration

The OneSignal SDK is initialised in `MyApplication` and updated in `UserDataManager`/`UserSession`:

1. Set the OneSignal App ID via the Firebase Remote Config or embed it in the manifest (check your private build variant).
2. Ensure `OneSignal.login(firebaseUid)` succeeds by calling `UserDataManager.loadUserDetails` after Firebase sign-in.
3. Tags `city` and `role` are applied automatically; confirm they align with your OneSignal segments.

## Maps & Location Services

- `HomeMapFragment`, `MapRouteManager`, and `LocationUpdateService` require runtime `ACCESS_FINE_LOCATION` permission.
- Directions requests consume the Maps/Places key; ensure billing is enabled on your Google Cloud project.
- Foreground service notifications use channel `route_updates` defined in `LocationUpdateService`.

## Widget & Notification Channels

- `MyApplication` registers notification channels for emergency alerts; verify channel IDs when customising.
- Android widgets (`EmergencyWidget`, `MapWidget`) rely on `AppWidgetProvider` updates and may require periodic refresh scheduling.

## Firebase Emulator Suite

When running locally:

```bash
firebase emulators:start --only functions --project <your-project-id>
```

Point the Android client to emulator endpoints using `FirebaseFirestoreSettings.Builder().setHost(...)` if needed (not configured by default). Most development occurs against live Firestore.

## Build Variants & Signing

- The repository includes placeholder `debug/` and `release/` signing configs under `app/` for reference.
- Configure your keystores via Android Studio (**Build > Generate Signed Bundle / APK**) and update `signingConfigs` if you introduce CI/CD builds.

Keep sensitive keys out of version control and rotate them regularly.
