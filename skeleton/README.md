# @grails.app.name@

Generated from the **@grails.profile@** profile — a Grails 7 + React 19 + Vite 6 + TypeScript + Tailwind v4 SPA starter.

## Finish setup (one-time, post-create)

```bash
bash post-create.sh
```

This swaps in the React-aware `build.gradle` and removes itself.
(See [Why this exists](#why-the-post-create-step) below for context.)

## Stack

- **Backend:** Grails 7.0.12 on a Gradle-managed Java 17 toolchain
- **Frontend:** React 19 + React Router 7 + Vite 6 + TypeScript 5.9 + Tailwind v4
- **Build:** Gradle drives both — `processResources` depends on `buildFrontend`, so a single `./gradlew build` produces a deployable artifact with the React bundle inside

## Project layout

```
@grails.app.name@/
├── build.gradle                   # Grails + node-gradle wiring
├── gradle.properties              # version pins (Java/Node/Grails)
├── frontend/                      # React/Vite source
│   ├── package.json               # React stack versions
│   ├── vite.config.ts             # Vite output → src/main/resources/public/
│   ├── index.html
│   └── src/
│       ├── main.tsx               # StrictMode + HashRouter
│       ├── App.tsx                # Welcome page (edit me!)
│       └── index.css              # @import "tailwindcss";
└── grails-app/
    ├── conf/
    │   ├── application.yml        # static-locations: classpath:/public/
    │   └── spring/resources.groovy # serves Vite-built /_app/** assets
    └── controllers/<package>/
        ├── AppController.groovy   # serves index.html at /
        └── UrlMappings.groovy
```

## Running

### Single-process build (production-like)

```bash
./gradlew build && ./gradlew bootRun
```

Vite builds into `src/main/resources/public/`, Grails serves it on `http://localhost:8080`.

### Two-process dev (best DX, Vite HMR)

In one terminal:
```bash
./gradlew bootRun -x buildFrontend
```

In another:
```bash
cd frontend && npm run dev
```

Vite serves on `http://localhost:5173` with HMR. Requests to `/api/*` are proxied to Grails on `:8080`.

### Single-process with rebuild-on-change

```bash
cd frontend && npm run build:watch  # one terminal
./gradlew bootRun                    # another terminal
```

Vite rebuilds into `src/main/resources/public/` on every change; refresh the browser to see updates.

## Bumping versions

- **Java / Node / Grails / npm versions:** edit `gradle.properties`
- **React / Vite / TypeScript / Tailwind versions:** edit `frontend/package.json`, then `cd frontend && npm install` to refresh the lock file

## Adding a backend API

Create a controller under `grails-app/controllers/<package>/`:

```groovy
package my.app

class HealthController {
    def index() {
        render(contentType: 'application/json', text: '{"status":"ok"}')
    }
}
```

Map it in `UrlMappings.groovy`:

```groovy
"/api/health"(controller: "health", action: "index", method: "GET")
```

Hit it from React with `fetch('/api/health')` — the Vite dev server proxies `/api/*` to Grails on `:8080`.

## Consuming API errors from React

Spring Boot content-negotiates error responses. When your fetch sends `Accept: application/json`, 404s and 500s come back as JSON. In development, the response includes the exception message for diagnostics:

```json
{
  "timestamp": "2026-05-02T...",
  "status": 404,
  "error": "Not Found",
  "message": "No endpoint GET /api/missing.",
  "path": "/api/missing"
}
```

Stack traces are always stripped. Exception messages are also hidden by default and enabled only in development; production clients should display their own stable, user-safe message and log a correlation ID instead of rendering server exception text.

A typical React consumer:

```tsx
async function fetchTodo(id: number) {
  const res = await fetch(`/api/todos/${id}`, {
    headers: { 'Accept': 'application/json' }
  })
  if (!res.ok) {
    const err = await res.json()
    throw new Error(`${err.status} ${err.error}: ${err.message}`)
    // → "404 Not Found: No endpoint GET /api/todos/999."
  }
  return res.json()
}
```

For browser users hitting an unknown URL directly (not via `fetch`), Spring's default Whitelabel HTML page is shown. Override it by adding `grails-app/views/error.html` (or wiring up a custom controller) if you want branded error pages.

## Why the post-create step

This profile extends the Grails `base` profile, but the generated template is web based: it uses the Grails web Gradle plugin, WAR packaging, Tomcat, URL mappings, and web testing support. The build follows the structure Grails web applications originally use, with React/Vite wired into `processResources`.

Grails 7's profile system can still create a starter `build.gradle` that is not the React-aware web build this template needs. To work around that, this profile ships its `build.gradle` as `build.gradle.react`, and `post-create.sh` swaps it into place after `grails create-app` runs.

If/when the Grails profile system gains proper file-replace semantics, this step will go away.
