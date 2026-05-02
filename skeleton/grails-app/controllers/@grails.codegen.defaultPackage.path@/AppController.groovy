package @grails.codegen.defaultPackage@

class AppController {

    def index() {
        def stream = getClass().getClassLoader().getResourceAsStream('public/index.html')
        if (!stream) {
            render(status: 503, text: 'Frontend not built. Run: cd frontend && npm run build')
            return
        }
        render(text: stream.text, contentType: 'text/html', encoding: 'UTF-8')
    }
}
