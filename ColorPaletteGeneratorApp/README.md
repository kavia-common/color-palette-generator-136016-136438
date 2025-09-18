# Color Palette Generator (Ocean Professional)

A minimalist Android app that generates visually distinct HSL color palettes (default 5 colors) on launch and on demand. Built with the Gradle Declarative DSL and a View-based UI for broad device compatibility.

Features:
- Random HSL palette generation with visual distinctness enforcement
- Minimalist Ocean Professional theme with light/dark modes
- Copy HSL or HEX values to clipboard
- Smooth crossfade transitions and ripple feedback
- Accessibility: adequate contrast, 48dp touch targets, content descriptions
- Responsive layout for phones and tablets
- Modular, testable palette generation logic
- Unit tests (JVM) and instrumented UI test hooks (Espresso)

Build:
- ./gradlew build

Install & run (device or emulator):
- ./gradlew :app:installDebug
- Launch "Color Palette Generator"

Tests:
- JVM unit tests: ./gradlew test
- Instrumented tests: ./gradlew connectedAndroidTest

Notes:
- No backend or network dependencies
- Uses downloadable fonts (Roboto) via Google Play Services