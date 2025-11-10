# Getting Started

This guide walks you through setting up the Angels App Android project and the accompanying Firebase Cloud Functions for local development.

## Prerequisites

| Component | Version / Notes |
| --- | --- |
| **Android Studio** | Ladybug (2024.2) or newer with Android SDK 24–35 installed |
| **Java** | JDK 17 (Gradle toolchain targets Java 17) |
| **Android SDK** | Ensure `ANDROID_HOME` or `local.properties` points to a valid SDK path |
| **Node.js** | v18 LTS (Cloud Functions `package.json` pins Node 18) |
| **Firebase CLI** | `firebase-tools` ≥ 13 for serving and deploying functions |
| **Google APIs** | Maps & Places API keys with Android + Web Static Map access |

## 1. Clone the Repository

```bash
git clone https://github.com/<your-org>/theangels.git
cd theangels
```

## 2. Configure Secrets

The build reads API keys from `secrets.properties` or environment variables:

```properties
GOOGLE_MAPS_API_KEY=your_android_maps_key
GOOGLE_PLACES_API_KEY=your_places_sdk_key
```

- Copy `secrets.properties` from the template provided in the repo and fill in your keys.
- Alternatively set `GOOGLE_MAPS_API_KEY` and `GOOGLE_PLACES_API_KEY` in your shell before building.

### Firebase Configuration

- Replace `app/google-services.json` with your Firebase project's file.
- Ensure Authentication, Firestore, Cloud Messaging, and Storage are enabled in Firebase Console.

## 3. Open the Android Project

1. Launch Android Studio and select **Open an Existing Project**.
2. Choose the repository root (`theangels`).
3. Allow Gradle sync to finish; install any missing SDK packages that Studio prompts for.
4. Select an emulator or physical device running Android 7.0 (API 24) or newer.

### Running the App

- Use **Run > Run 'app'** or click the green play button.
- The default launch activity is a splash screen that forwards to onboarding or the main dashboard depending on onboarding completion.

## 4. Set Up Cloud Functions

```bash
cd functions
npm install
```

### Emulate Locally

```bash
firebase emulators:start --only functions
```

### Deploy to Firebase

```bash
npm run deploy
```

Environment variables for the functions (webhook URLs, Google Maps Static key) should be provisioned through Firebase environment configuration or `.env` when using emulators:

```bash
firebase functions:config:set make.volunteer_assigned_webhook_url="https://..." \
                           make.new_event_webhook_url="https://..."
```

## 5. Sign In During Development

The application expects users to authenticate via Firebase Authentication. Use one of the following strategies:

- Enable Email/Password sign-in in Firebase and create accounts through the Firebase Console.
- Seed Firestore with the required documents (`users`, `events`, `educations`, `messages`, `contacts`) to experience the full UI.

## 6. Optional Tooling

- `./gradlew generateJavadoc` builds API documentation for Java sources.
- `npm run logs` tails Cloud Function logs after deployment.

Once these steps are complete you can iterate on the Android client, verify webhook automation via the emulators, and contribute changes confidently.
