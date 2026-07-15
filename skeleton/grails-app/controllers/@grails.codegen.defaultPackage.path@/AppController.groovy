package @grails.codegen.defaultPackage@

class AppController {

    def index() {
        def stream = getClass().getClassLoader().getResourceAsStream('public/index.html')
        if (!stream) {
            render(status: 503, text: 'Frontend not built. Run: cd frontend && npm run build')
            return
        }
        stream.withCloseable {
            render(text: it.getText('UTF-8'), contentType: 'text/html', encoding: 'UTF-8')
        }
    }
}
