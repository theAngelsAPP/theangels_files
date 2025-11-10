# The Angels App

The Angels App is a mobile-first emergency response platform that empowers the Angels volunteer network to coordinate incidents, dispatch volunteers, and deliver educational resources through a dedicated Android client backed by Firebase automation. The repository combines the Android application, Firebase Cloud Functions, and comprehensive documentation so new contributors can get productive quickly.

## Table of Contents
- [Project Title & Description](#the-angels-app)
- [Installation](#installation)
- [Usage](#usage)
- [Project Structure & Architecture](#project-structure--architecture)
- [API Reference](#api-reference)
- [Configuration](#configuration)
- [Contributing](#contributing)
- [Deployment](#deployment)
- [License](#license)
- [Additional Resources](#additional-resources)

## Installation
1. Clone the repository and open it in Android Studio (Ladybug 2024.2 or newer).
2. Follow the Android and Firebase setup prerequisites outlined in the [Getting Started guide](docs/getting-started.md).
3. Populate `secrets.properties` with your Maps and Places API keys, and replace `app/google-services.json` with credentials from your Firebase project.

## Usage
- Run the Android client from Android Studio via **Run > Run 'app'** or execute `./gradlew assembleDebug` to build the debug variant.
- To work on backend automation locally, install dependencies under `functions/` with `npm install` and start the Firebase Functions emulator using `firebase emulators:start --only functions`.

## Project Structure & Architecture
The repository is organised into feature-focused modules:
- `app/` – Android source grouped into `ui`, `data`, and `util` layers with ViewBinding, Google Maps integrations, and volunteer workflows.
- `functions/` – Node 18 Firebase Cloud Functions that send webhook notifications, enrich events, and persist AI analysis.
- `docs/` – Reference documentation covering setup, architecture, APIs, configuration, and deployment.

A deeper look at the mobile and backend architecture is available in [docs/architecture.md](docs/architecture.md).

## API Reference
The app relies on Firebase Authentication, Cloud Firestore, and Cloud Functions triggers for event lifecycle automation. Detailed collection schemas, callable endpoints, and payload examples live in the [API reference](docs/api.md).

## Configuration
Runtime secrets (Maps & Places keys, Make.com webhooks) are injected via Gradle properties and Firebase runtime config. Review the [configuration guide](docs/config.md) for the full list of environment variables, resource injection patterns, and service prerequisites.

## Contributing
We welcome contributions of all sizes. Please read the [contributing guide](docs/contributing.md) for branching strategy, coding standards, and testing expectations before opening a pull request.

## Deployment
Instructions for signing Android release builds, deploying Firebase Functions, and monitoring production rollouts are covered in the [deployment guide](docs/deployment.md).

## License
A license has not been provided. Please contact the maintainers for usage terms before distributing or modifying the code.

## Additional Resources
- [Project overview](docs/overview.md)
- [Documentation index](docs/index.md)
- [FAQ](docs/faq.md)

For further questions about the Angels initiative or partnership opportunities, reach out through the contact details in project documentation or organisational channels.
