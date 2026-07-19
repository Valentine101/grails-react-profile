# grails-react

A Grails 8 application profile that generates a ready-to-run **Grails 8 + React 19 + Vite 6 + TypeScript + Tailwind v4** SPA.

> Grails 8.0.0-M3 is a milestone release. Use this profile for evaluation and early migration work, not production deployments that require a stable Grails release.

```bash
sdk install java 21.0.11-amzn
sdk install grails 8.0.0-M3
sdk use java 21.0.11-amzn
sdk use grails 8.0.0-M3

PROFILE_VERSION=2.0.0-M3
grails create-app my-app --profile io.github.valentine101:grails-react:$PROFILE_VERSION
cd my-app
./gradlew build
./gradlew bootRun       # http://localhost:8080
```

The generated app:

- Builds React via Vite into `src/main/resources/public/`
- Serves the SPA and backend from one Grails/Spring Boot process
- Uses a Java 21 Gradle toolchain and downloads the pinned Node/npm toolchain
- Supports two-process development with Vite HMR and an `/api` proxy
- Exposes only Actuator health and info endpoints in development

## Build and publish the profile locally

```bash
git clone https://github.com/Valentine101/grails-react-profile
cd grails-react-profile
sdk use java 21.0.11-amzn
sdk use grails 8.0.0-M3
./gradlew publishToMavenLocal
```

Then create an application with the command in the opening example. No post-create script or manual file replacement is required.

## Stack

| Layer | Versions / details |
|---|---|
| Backend | Grails 8.0.0-M3, Spring Boot 4.1, Java 21 |
| Runtime | Tomcat, Jackson 3 JSON, validation, Actuator, devtools |
| Frontend | React 19, React Router 7 (HashRouter), Vite 6, TypeScript 5.9 |
| Styling | Tailwind v4 via `@tailwindcss/vite` |
| Build | Gradle 9.6 and `node-gradle-plugin` 7.1.0; `processResources` depends on `buildFrontend` |
| Assets | Vite emits `public/_app/`; Spring serves immutable fingerprinted assets under `/_app/**` |
| Routing | Grails serves `index.html` at `/`; HashRouter handles browser-side routes |

The profile is self-contained and uses the official Grails 8 M3 Gradle/Grails wrappers. Its generated build applies the Grails web plugin and dependencies directly without pulling in GSP or Asset Pipeline, which are unnecessary for a Vite-built SPA. This also avoids Grails 8.0.0-M3's parent-skeleton merge path, which currently concatenates replacement build files.

## Template parameters

`grails create-app` substitutes:

- `@grails.app.name@` — application name
- `@grails.codegen.defaultPackage@` — Java/Groovy package
- `@grails.codegen.defaultPackage.path@` — package as a directory path

Profile artifact versions are in the root `gradle.properties`. Generated application toolchain versions are in `skeleton/gradle.properties`; frontend versions are in `skeleton/frontend/package.json` and its lockfile.

## Development

```bash
./gradlew test
./gradlew build
./gradlew publishToMavenLocal
./gradlew profileBundle
```

`ProfileSmokeTest` checks the packaged skeleton, version pins, substitution tokens, hardened configuration, and frontend build wiring. Release validation should also publish locally, create a real application with Grails 8, build it, and exercise its HTTP endpoints.

## Release history

- `1.0.0`–`1.0.4`: Grails 7 profile and security/build hardening
- `2.0.0-M3`: Grails 8.0.0-M3 and Java 21 migration; self-contained Gradle 9.6/Grails wrappers; no post-create step

## License

No license has been selected yet. Add one before encouraging third-party redistribution.
