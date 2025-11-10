# Data & API Reference

The Angels platform relies on Firebase Authentication for identity, Cloud Firestore for state, and Cloud Functions for automation. This document outlines the key data structures and backend endpoints consumed by the Android client.

## Firestore Collections

### `users`

Represents registered members and volunteers. Documents are addressed by Firebase Auth UID.

| Field | Type | Notes |
| --- | --- | --- |
| `firstName`, `lastName` | `string` | Rendered in greetings and volunteer dashboards. |
| `Email`, `Phone` | `string` | Mixed casing handled in `UserDataManager`. |
| `birthDate` | `timestamp` | Optional, surfaced in profile details. |
| `city` | `string` | Used for proximity filtering and OneSignal tagging. |
| `haveGunLicense` | `boolean` | Volunteer qualification flag. |
| `idNumber` | `string` | Volunteer verification. |
| `imageURL` | `string` | Stored in `UserSession` for avatars. |
| `medicalDetails` | `array<string>` | Displayed as comma-separated string. |
| `role` | `string` | One of `"משתמש"`, `"מתנדב"`, etc. Drives UI branches. |
| `volAvailable` | `array<string>` | Volunteer availability schedule. |
| `volCities` | `array<string>` | Cities the volunteer can cover. |
| `volHaveDriverLicense` | `boolean` | Eligibility for driving roles. |
| `volVerification` | `string` | Back-office approval status. |
| `volSpecialty` | `array<string>` | Skills for targeted dispatch. |

### `events`

Incident records created through the in-app wizard.

| Field | Type | Notes |
| --- | --- | --- |
| `eventType` | `string` | Selected from Firestore-driven taxonomy. |
| `eventForm` | `map<string, boolean>` | Answers collected from `QuestionnaireFragment`. |
| `eventLocation` | `GeoPoint` | Incident coordinates used by map fragments. |
| `eventCreatedBy` | `string` | UID of requester. |
| `eventHandleBy` | `string` | UID of assigned volunteer. |
| `eventStatus` | `string` | Matches values from `UserEventStatus` enum (Hebrew strings). |
| `eventTimeStarted`, `eventTimeEnded` | `timestamp` | Lifecycle timestamps. |
| `eventQuestionChoice` | `string` | Summary of the selected questionnaire branch. |
| `eventRating`, `eventRatingText` | `number`, `string` | Closure feedback. |
| `eventCloseReason` | `string` | Provided when closing an event. |
| `volunteerLocation` | `GeoPoint` | Updated by `LocationUpdateService`. |
| `volunteerETA` | `number` | Minutes until arrival, set via `MapRouteManager`. |
| `eventAnalysis`, `personAnalysis`, `guidanceAnalysis` | `string` | AI enrichment stored by `saveEventAnalysis`. |

### `educations`

Curated learning resources surfaced in `EducationActivity`.

| Field | Type | Notes |
| --- | --- | --- |
| `eduTitle` | `string` | Card title. |
| `eduData` | `string` | Markdown / HTML content. |
| `eduImageURL` | `string` | Thumbnail loaded by Glide. |
| `eduType` | `string` | Category filtering. |

### `messages`

Announcements and guidance displayed on the home feed.

| Field | Type | Notes |
| --- | --- | --- |
| `messageTitle` | `string` | Card headline. |
| `messageData` | `string` | Body copy. |
| `messageType` | `string` | Joined with `messageTypes` for styling. |
| `messageRef` | `string` | Optional deep-link target. |

### `messageTypes`

Defines visual treatments for messages.

| Field | Type | Notes |
| --- | --- | --- |
| `typeName` | `string` | Unique identifier referenced by `messageType`. |
| `color` | `string` | Hex string consumed by `MessageStyleMapper`. |
| `iconURL` | `string` | Optional remote icon. |

### `contacts`

Emergency contacts maintained per user.

| Field | Type | Notes |
| --- | --- | --- |
| `contactUserUID` | `string` | Owner UID. |
| `contactName` | `string` | Display name. |
| `contactPhone` | `string` | Dial target for quick actions. |
| `contactRelationship` | `string` | Label shown in profile. |

## Cloud Functions

All functions are exported from `functions/index.js` and deployed under the default Firebase codebase.

### `notifyVolunteerAssigned`

- **Trigger**: Firestore document update on `events/{eventId}`.
- **Guard**: Fires only when `eventStatus` transitions to `"מתנדב בדרך"`.
- **Action**: Loads creator + volunteer profiles, assembles payload, and POSTs to `MAKE_VOLUNTEER_ASSIGNED_WEBHOOK_URL`.
- **Payload excerpt**:

```json
{
  "event_id": "123",
  "event_type": "חונכות",
  "creator_uid": "uidA",
  "creator_firstName": "Dana",
  "volunteer_uid": "987654321",
  "volunteer_firstName": "Noam",
  "volunteer_lastName": "Levi",
  "volunteer_image_url": "https://..."
}
```

### `notifyNewEventCreated`

- **Trigger**: Firestore document create on `events/{eventId}`.
- **Action**: Fetches creator details, optional emergency contact, builds a Static Maps URL, then POSTs to `MAKE_NEW_EVENT_WEBHOOK_URL` with enriched data and OneSignal routing metadata.
- **Requires**: `GOOGLE_MAPS_API_KEY` environment variable for Static Map.

### `saveEventAnalysis`

- **Trigger**: HTTPS request (POST only).
- **URL**: `https://<region>-<project>.cloudfunctions.net/saveEventAnalysis` (depending on deployment region).
- **Body**:

```json
{
  "eventId": "123",
  "eventAnalysis": "Summary from AI",
  "personAnalysis": "Volunteer insights",
  "guidanceAnalysis": "Suggested follow-up"
}
```

Merges payload fields into the `events/{eventId}` document.

### `notifyVolunteerRegistered`

- **Trigger**: Firestore document update on `users/{userId}` when `role` changes to `"מתנדב"`.
- **Action**: Sends volunteer profile snapshot to `MAKE_VOLUNTEER_REGISTERED_WEBHOOK_URL` for onboarding workflows.

## REST / Retrofit Consumption

Most network calls originate from Firebase SDKs. When REST is required:

- `DirectionsApiClient` issues HTTPS requests to the Google Directions API using Retrofit + OkHttp to compute polylines and ETA.
- Webhooks are exclusively server-side; the mobile client does not call Make.com endpoints directly.

## Authentication Flow

- Firebase Authentication handles identity (typically email/password).
- On login, `UserDataManager.loadUserDetails` synchronises the `UserSession` singleton and registers OneSignal tags (`city`, `role`).
- `UserSession.clear()` logs out from OneSignal and purges cached data when a user signs out.

Refer to the `architecture.md` document for component-level interactions between these APIs and the Android UI.
