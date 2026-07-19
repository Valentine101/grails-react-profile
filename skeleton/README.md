# @grails.app.name@

Generated from the **@grails.profile@** profile: Grails 8 + React 19 + Vite 6 + TypeScript + Tailwind v4.

## Stack

- **Backend:** Grails 8.0.0-M3 with a Gradle-managed Java 21 toolchain
- **Frontend:** React 19, React Router 7, Vite 6, TypeScript 5.9, and Tailwind v4
- **Build:** `processResources` depends on `buildFrontend`, producing one deployable artifact containing the React bundle

## Run it

```bash
./gradlew build
./gradlew bootRun
```

Open `http://localhost:8080/`.

For Vite HMR, run the backend and frontend separately:

```bash
# terminal 1
./gradlew bootRun -x buildFrontend

# terminal 2
cd frontend
npm run dev
```

Vite runs at `http://localhost:5173` and proxies `/api/*` to Grails on port 8080.

## Project layout

```text
@grails.app.name@/
├── build.gradle                    # Grails and Node/Vite build wiring
├── gradle.properties               # Java, Grails, Node, and npm pins
├── frontend/                       # React/Vite source
│   ├── package.json
│   ├── vite.config.ts              # output to src/main/resources/public/
│   └── src/
└── grails-app/
    ├── conf/
    │   ├── application.yml
    │   └── spring/resources.groovy # serves /_app/**
    └── controllers/<package>/
        ├── AppController.groovy    # serves index.html at /
        └── UrlMappings.groovy
```

## Add a backend API

Create a controller and add an explicit mapping in `UrlMappings.groovy`:

```groovy
"/api/health"(controller: "health", action: "index", method: "GET")
```

Call it from React with `fetch('/api/health')`. The Vite development server proxies `/api/*`; production requests are handled directly by Grails.

## Error and management endpoint behavior

API errors are content-negotiated. Send `Accept: application/json` to receive JSON. Stack traces are never included, and exception messages are exposed only in development. Actuator exposes only `health` and `info` in development by default.

## Version updates

- Edit Java, Grails, Node, npm, or Node plugin pins in `gradle.properties`.
- Edit frontend dependencies in `frontend/package.json`, then run `npm install` in `frontend/` to refresh the lockfile.
