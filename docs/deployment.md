# Deployment Guide

This document summarises the steps required to ship the Angels mobile app and backend infrastructure to production environments.

## Android Application

### 1. Prepare Release Keystore

1. Generate a keystore (once per project):
   ```bash
   keytool -genkeypair -v -keystore angels-release.keystore -alias angels \
     -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Place the keystore in a secure location outside the repo.
3. Update `app/build.gradle` with a `signingConfigs` block referencing environment variables, or configure via Android Studio (**Build > Generate Signed Bundle / APK**).

### 2. Configure Versioning

- Increment `versionCode` and `versionName` in `app/build.gradle` prior to each release.
- Maintain a changelog aligned with the Play Console release notes.

### 3. Build Release Artifact

```bash
./gradlew assembleRelease
```

The signed APK/AAB is emitted under `app/build/outputs/`.

### 4. Validate Before Publishing

- Run instrumentation tests on a physical device or Test Lab.
- Verify Maps, authentication, push notifications, and foreground services using production API keys.
- Confirm that onboarding, event creation, volunteer claiming, and education modules function end-to-end with production Firestore data.

### 5. Distribute

- Upload the generated AAB to Google Play Console (Internal Testing → Closed → Production).
- Attach release notes summarising volunteer-facing changes.
- Monitor the Play Console vitals after rollout.

## Firebase Backend

### 1. Cloud Functions Deployment

From the `functions/` directory:

```bash
npm install
npm run deploy
```

This runs `firebase deploy --only functions`, uploading:

- `notifyVolunteerAssigned`
- `notifyNewEventCreated`
- `saveEventAnalysis`
- `notifyVolunteerRegistered`

Ensure runtime configs are in place:

```bash
firebase functions:config:set \
  make.volunteer_assigned_webhook_url="https://hooks.make.com/..." \
  make.new_event_webhook_url="https://hooks.make.com/..." \
  make.volunteer_registered_webhook_url="https://hooks.make.com/..."
```

### 2. Firestore & Authentication Rules

- Publish security rules that restrict access to authenticated users and enforce role-based access on events/contacts.
- Back up Firestore before major schema changes.

### 3. OneSignal & Push Notifications

- Configure production keys in the OneSignal dashboard and associate the Firebase Server Key.
- Update in-app messaging segments if new cities or roles are introduced.

### 4. Monitoring & Logging

- Use `npm run logs` to tail function logs post-deployment.
- Set up Firebase Alerts for function failures and high latency.
- Configure Crashlytics (if enabled) to monitor app stability after rollout.

## Continuous Integration (Optional)

- Set up GitHub Actions to run `./gradlew lint test` and `npm test` (if unit tests are added) on pull requests.
- Integrate Play Console’s Publishing API for automated internal track uploads.

## Rollback Strategy

- Maintain previous release artifacts and tagged Git commits.
- Use `firebase functions:delete <name>` to disable problematic functions quickly.
- Lower the Play Console rollout percentage or unpublish the release if critical issues arise.

Following these steps ensures a consistent deployment pipeline across mobile and server components.
