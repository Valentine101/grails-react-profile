# grails-react

A Grails 7 application profile that scaffolds a fully-wired **Grails 7 + React 19 + Vite 6 + TypeScript + Tailwind v4** SPA with one command.

```bash
grails create-app my-app --profile io.github.valentine101:grails-react:1.0.1
cd my-app && bash post-create.sh
./gradlew bootRun       # → http://localhost:8080
```

The generated app:

- Builds the React frontend via Vite into `src/main/resources/public/`
- Serves it from a single Grails (Spring Boot) process — one JAR/WAR in production
- Has a Tailwind-styled welcome page out of the box
- Supports two-process dev with Vite HMR (`/api` proxied to Grails on `:8080`)

## Quick start

### 1. Build & publish the profile to Maven Local

```bash
git clone https://github.com/Valentine101/grails-react-profile
cd grails-react-profile
./gradlew publishToMavenLocal
```

### 2. Spawn a new app

```bash
cd ~/wherever
grails create-app my-app --profile io.github.valentine101:grails-react:1.0.1
cd my-app
bash post-create.sh        # one-time cleanup (see "Why post-create" below)
./gradlew build && ./gradlew bootRun
```

Open `http://localhost:8080/` — you should see the styled welcome page.

### Alternative: install from a GitHub release zip

If you don't want to build from source (e.g. on a fresh machine):

```bash
# Download the release zip
curl -LO https://github.com/Valentine101/grails-react-profile/releases/download/v1.0.1/grails-react-profile-1.0.1.zip
unzip grails-react-profile-1.0.1.zip
cd grails-react-profile-1.0.0
bash install.sh            # installs the JAR into ~/.m2 via mvn install:install-file

# Then create your app as in step 2 above
```

## What's in the profile

| Layer | Versions / details |
|---|---|
| Grails | 7.0.4 on Java 17 |
| Spring Boot | starter, actuator, tomcat, validation, devtools |
| Frontend | React 19, React Router 7 (HashRouter), Vite 6, TypeScript 5.8 |
| Styling | Tailwind v4 via `@tailwindcss/vite` |
| Build | `node-gradle-plugin` 7.1.0 wires `npmCiInstall` → `buildFrontend` into `processResources` |
| Asset serving | Vite outputs to `src/main/resources/public/_app/`; a Spring `ResourceHttpRequestHandler` (in `grails-app/conf/spring/resources.groovy`) serves `/_app/**` ahead of Grails URL mappings |
| Routing | `AppController` returns `index.html` from the classpath at `/`; HashRouter handles client-side routes |

## Parameters

`grails create-app` substitutes these tokens in skeleton files:

- `@grails.app.name@` — your app name (e.g. `my-app`); used in `package.json`, `index.html`, `App.tsx`, `README`
- `@grails.codegen.defaultPackage@` — Java package (e.g. `my.app`); used in `package` declarations
- `@grails.codegen.defaultPackage.path@` — same as a slashed path; used in directory names

Versions (Grails, Java, Node, npm, plus the React stack) are pinned in `gradle.properties` and `frontend/package.json` and easy to bump after spawn.

## Why the post-create step

Grails 7's profile system concatenates skeleton files from the inherited web profile + this profile, producing a duplicated `build.gradle` that has a `mavenCentral` typo (from the base profile) and the wrong plugin set. To work around this, the profile ships its build configuration as `build.gradle.react`. The `post-create.sh` script swaps it into place and removes itself. One command, then forget it ever existed.

If/when Grails profiles gain proper file-replace semantics (rather than concatenation), this step will be removed.

## Roadmap

- **v1.0.0:** initial release
- **v1.0.1 (this release):** removed `"404"(view:'/notFound')` / `"500"(view:'/error')` mappings from `UrlMappings.groovy` — they referenced GSP views that this profile doesn't ship a runtime for, causing `ServletException` on every 404 (e.g. `/favicon.ico`)
- **v2 (future):** custom `grails set-versions` command for spawn-time version injection
- **v2 (future):** modular `--features` (e.g. `--features=tailwind,gorm-hibernate5`)
- **v2 (future):** automated end-to-end smoke test in CI

## Development

```bash
./gradlew build              # compiles, runs ProfileSmokeSpec, builds the JAR
./gradlew test               # runs ProfileSmokeSpec only
./gradlew publishToMavenLocal # installs to ~/.m2
./gradlew profileBundle      # produces build/distributions/grails-react-profile-1.0.1.zip
```

`ProfileSmokeSpec` is a structural check — it verifies skeleton files exist with the right substitution tokens, but does NOT exercise `grails create-app` end-to-end. The full smoke test lives in the README's Quick Start section above and should be run manually after any change to the profile.

## License

(unspecified — adjust to taste)
