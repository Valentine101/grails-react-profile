package @grails.codegen.defaultPackage@

class UrlMappings {

    static mappings = {
        "/"(controller: "app", action: "index")

        // Add backend routes explicitly under /api with an HTTP method, for
        // example: "/api/health"(controller: "health", method: "GET")

        // Note: no "404"/"500" view mappings here. This web based template
        // does NOT include the GSP plugin/runtime, so referencing GSP error
        // views would throw ServletException on every 404 (e.g. /favicon.ico).
        // Spring Boot's default error handling produces sensible 404/500
        // responses without GSP.
    }
}
