# Contributing to WireLing

Thank you for your interest in contributing to WireLing.

## Development setup

1. Clone the repository:
   ```bash
   git clone https://github.com/thebytearray/wireling.git
   ```

2. Open the project in Android Studio.

3. Sync Gradle and build:
   ```bash
   ./gradlew :wireling:assembleRelease :app:assembleDebug
   ```

## Project layout

- **`wireling`**: Android library (`org.thebytearray.wireling`), published to Maven Central.
- **`app`**: Sample application (`org.thebytearray.wireling.sample`).

## Versioning

Version metadata lives in `version.properties` at the repository root. Bump versions via the Release workflow, not ad hoc edits.

## Release process

Repository secrets for Maven Central are listed in [docs/MavenCentral-Publishing.md](docs/MavenCentral-Publishing.md).

1. Open **Actions → Release → Run workflow**
2. Choose **patch**, **minor**, or **major**, then run it

The workflow bumps `version.properties`, builds, publishes to Maven Central, commits, tags, and creates a GitHub release with the AAR.

## Commit guidelines

Use clear commit messages. Suggested prefixes:

- `feat:` New features
- `fix:` Bug fixes
- `docs:` Documentation
- `refactor:` Refactors without behavior change
- `chore:` Maintenance
- `test:` Tests

## Pull requests

1. Fork the repository
2. Create a feature branch from `master`
3. Make your changes
4. Ensure `./gradlew :wireling:assembleRelease :app:assembleDebug` passes
5. Open a pull request

## License

By contributing, you agree that your contributions will be licensed under the GNU General Public License v3.0.
