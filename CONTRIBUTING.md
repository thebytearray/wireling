# Contributing to WGAndroidLib

Thank you for your interest in contributing to WGAndroidLib.

## Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/thebytearray/WGAndroidLib.git
   ```

2. Open the project in Android Studio

3. Sync Gradle and build:
   ```bash
   ./gradlew :wireguard:assembleRelease
   ```

## Versioning

This project uses semantic versioning. Version information is stored in `version.properties`:

```properties
VERSION_NAME=1.2.0
VERSION_CODE=4
```

- **VERSION_NAME**: Semantic version (MAJOR.MINOR.PATCH)
- **VERSION_CODE**: Incremental integer for Android

Do not manually edit `version.properties` unless necessary. Version bumps are handled automatically by the release workflow.

## Release Process

Releases are automated via GitHub Actions. To create a new release:

1. Navigate to **Actions** tab in the GitHub repository
2. Select **Release** workflow
3. Click **Run workflow**
4. Choose the version bump type:
   - **patch**: Bug fixes (1.2.0 -> 1.2.1)
   - **minor**: New features (1.2.0 -> 1.3.0)
   - **major**: Breaking changes (1.2.0 -> 2.0.0)
5. Click **Run workflow**

The workflow will:
- Bump the version in `version.properties`
- Build the AAR
- Generate changelog from commits since last release
- Create a GitHub release with the AAR attached
- Tag the release

## Commit Guidelines

Use clear, descriptive commit messages. Recommended prefixes:

- `feat:` New features
- `fix:` Bug fixes
- `docs:` Documentation changes
- `refactor:` Code refactoring
- `chore:` Maintenance tasks
- `test:` Test additions or modifications

Examples:
```
feat: add DNS configuration support
fix: resolve connection timeout issue
docs: update API reference for TunnelConfig
```

## Pull Requests

1. Fork the repository
2. Create a feature branch from `master`
3. Make your changes
4. Ensure the build passes: `./gradlew :wireguard:assembleRelease`
5. Submit a pull request

## Code Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions focused and concise

## License

By contributing, you agree that your contributions will be licensed under the GPLv3 license.

