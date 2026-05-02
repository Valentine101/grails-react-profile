import org.springframework.web.servlet.resource.ResourceHttpRequestHandler
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping
import org.springframework.core.io.ClassPathResource

// Serve Vite-built React assets at /_app/** before Grails URL mappings intercept them.
// Vite is configured with assetsDir: '_app', so bundled JS/CSS land in public/_app/.
// order = -100 ensures this handler fires before Grails's own URL mapping handler.
beans = {
    reactAssetsHandler(ResourceHttpRequestHandler) {
        locations = [new ClassPathResource('public/_app/')]
    }

    reactAssetsMapping(SimpleUrlHandlerMapping) {
        order = -100
        urlMap = ['/_app/**': ref('reactAssetsHandler')]
    }
}
