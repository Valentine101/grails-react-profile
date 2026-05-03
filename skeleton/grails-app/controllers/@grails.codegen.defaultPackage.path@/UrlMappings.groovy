package @grails.codegen.defaultPackage@

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: "app", action: "index")

        // Note: no "404"/"500" view mappings here. The inherited web profile
        // drops notFound.gsp/error.gsp into grails-app/views/, but this
        // profile does NOT include the GSP plugin/runtime, so referencing
        // those views would throw ServletException on every 404 (e.g.
        // /favicon.ico). Spring Boot's default error handling produces
        // sensible 404/500 responses without GSP.
    }
}
