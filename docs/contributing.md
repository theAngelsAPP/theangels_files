# Contributing Guide

We welcome improvements to the Angels project. This guide outlines the development workflow, coding standards, and review expectations.

## Code of Conduct

- Treat community members with respect and professionalism.
- Do not upload real patient/user data into the repository or sample datasets.
- Follow the security and privacy requirements of the Angels organisation.

## Getting Started

1. Fork the repository and create a feature branch:
   ```bash
   git checkout -b feature/<short-description>
   ```
2. Install prerequisites listed in [`getting-started.md`](getting-started.md).
3. Configure Firebase/OneSignal credentials in your local environment.

## Development Workflow

1. Sync with `main` regularly:
   ```bash
   git fetch origin
   git rebase origin/main
   ```
2. Implement changes with clear commit messages in imperative mood (e.g. `Add volunteer ETA badge to home feed`).
3. Run relevant checks:
   - `./gradlew lint` and `./gradlew test` for Android modules.
   - `./gradlew assembleDebug` to ensure the app compiles.
   - `npm run lint` / unit tests (if added) inside `functions/`.
4. Update or add documentation in `docs/` when behaviour changes.
5. Open a pull request referencing related issues and describe testing performed.

## Coding Standards

- **Language**: Java for Android sources, Node.js (CommonJS) for Cloud Functions.
- **Style**: Follow Android Studio’s default Java formatting; avoid unused imports and log statements in production code.
- **Nullability**: Use AndroidX annotations (`@NonNull`, `@Nullable`) where applicable.
- **Resources**: Keep Hebrew and English strings in `res/values/strings.xml` and `res/values-en/strings.xml` if localisation is added.
- **Configuration**: Do not hardcode API keys; rely on Gradle resource values and Firebase configs.

## Testing Guidelines

- Prefer instrumented tests for critical flows (event creation, volunteer claim) when feasible.
- Use the Firebase Emulator Suite to validate function triggers without touching production data.
- Document manual testing steps in the PR description when automated coverage is not available.

## Review Process

- At least one maintainer must approve the pull request before merging.
- Address review comments promptly; use follow-up commits or amend existing ones.
- Squash and merge unless a maintainer requests a preserved history.

## Issue Reporting

- Use GitHub Issues with clear reproduction steps, expected vs. actual behaviour, screenshots/logs when relevant.
- Label issues with `bug`, `enhancement`, or `documentation` for triage.

## Security & Privacy

- Report sensitive vulnerabilities privately via the maintainers’ contact channels.
- Do not commit secrets (`google-services.json`, keystores, webhook URLs). Use `.gitignore` entries already provided.

Thank you for contributing to the Angels mission!
