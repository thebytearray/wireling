# Contributing to WireLing

Thank you for your interest in contributing to WireLing.

## Development setup

1. Clone the repository:
   ```bash
   git clone https://github.com/thebytearray/WireLing.git
   ```

2. Open the project in Android Studio.

3. Sync Gradle and build:
   ```bash
   ./gradlew :wireling:assembleRelease :app:assembleDebug
   ```

## Project layout

- **`wireling`** — Android library (`org.thebytearray.wireling.sdk`), published via JitPack.
- **`app`** — Sample application (`org.thebytearray.wireling.sample`).

## Versioning

Version metadata lives in `version.properties` at the repository root. Bump versions as part of a deliberate release, not drive-by edits.

## Commit guidelines

Use clear commit messages. Suggested prefixes:

- `feat:` — New features
- `fix:` — Bug fixes
- `docs:` — Documentation
- `refactor:` — Refactors without behavior change
- `chore:` — Maintenance
- `test:` — Tests

## License

By contributing, you agree that your contributions will be licensed under the same license as the project (GNU General Public License v3.0).
