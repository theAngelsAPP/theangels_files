# Frequently Asked Questions

## Why is the UI in Hebrew by default?

The Angels network serves Hebrew-speaking volunteers and community members. RTL layout and Hebrew copy are enforced programmatically (e.g. `OnboardingActivity` sets layout direction) to guarantee consistent localisation. You can add English strings in `res/values-en/` and relax the RTL override for international deployments.

## Where do I manage volunteer availability?

Volunteer availability, cities, driver licence status, and specialties are stored on the `users` document. `ProfileActivity` and supporting dialogs allow volunteers to update these fields, which `UserDataManager` persists to Firestore and mirrors in `UserSession`.

## How are open events surfaced on the home screen?

`HomeActivity` registers Firestore listeners via `EventDataManager.listenToActiveEvents`. The results feed `OpenEventsAdapter` and a bottom-sheet style widget that volunteers can open to claim events. Status changes also update the map via `HomeMapFragment` and `MapRouteManager`.

## What powers the live ETA indicator?

`MapRouteManager` uses Retrofit + OkHttp to call the Google Directions API. It draws the returned polyline on the map and updates the `volunteerETA` field in Firestore, which the UI renders next to volunteer cards. `LocationUpdateService` keeps `volunteerLocation` fresh while the volunteer is en route.

## How do automated notifications work?

Cloud Functions trigger on Firestore writes to `events` and `users`. They fetch related records, assemble JSON payloads, and POST them to Make.com webhook URLs. Make.com then orchestrates OneSignal pushes and analytics exports. Environment variables (`MAKE_*_WEBHOOK_URL`) configure the destinations.

## How can I test without touching production data?

- Use the Firebase Emulator Suite for Cloud Functions (`firebase emulators:start --only functions`).
- Point your Android client at a staging Firebase project with its own `google-services.json` and API keys.
- Seed Firestore with mock data (events, users, educations, messages) to exercise each screen.

## Why is my map blank?

Check the following:

1. Ensure `secrets.properties` contains valid `GOOGLE_MAPS_API_KEY` and (optionally) `GOOGLE_PLACES_API_KEY`.
2. Confirm the key is enabled for Android Maps SDK, Places SDK, and Directions API.
3. Verify runtime location permission (`ACCESS_FINE_LOCATION`) is granted; otherwise `HomeMapFragment` hides the map container until the user approves access.

## Can I disable OneSignal?

Yes. Remove the OneSignal initialisation from `MyApplication` and replace push notifications with Firebase Cloud Messaging if preferred. Update `UserSession` and `UserDataManager` to skip `OneSignal.login` and tag assignments. Adjust Cloud Functions/webhooks accordingly to avoid referencing OneSignal payloads.
