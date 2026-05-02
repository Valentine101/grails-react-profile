package grails.react

import spock.lang.Specification

/**
 * Structural smoke checks on the profile's skeleton files.
 *
 * These guard against accidental skeleton regressions (missing files,
 * dropped substitution tokens, etc.). They do NOT exercise `grails create-app`
 * end-to-end — that is the responsibility of the manual smoke test
 * documented in README.md and (eventually) a CI job.
 */
class ProfileSmokeSpec extends Specification {

    static final File PROJECT_ROOT = new File(System.getProperty('user.dir'))
    static final File SKELETON = new File(PROJECT_ROOT, 'skeleton')

    def "skeleton root contains expected files"() {
        expect:
        // build.gradle.react is the canonical build file (renamed to avoid
        // concatenation merge with the inherited web profile's build.gradle).
        // post-create.sh is responsible for moving it into place.
        new File(SKELETON, 'build.gradle.react').exists()
        new File(SKELETON, 'post-create.sh').exists()
        new File(SKELETON, 'settings.gradle').exists()
        new File(SKELETON, 'gradle.properties').exists()
        new File(SKELETON, '.gitignore').exists()
        new File(SKELETON, 'README.md').exists()
    }

    def "skeleton build.gradle.react wires the node-gradle plugin and buildFrontend task"() {
        given:
        def text = new File(SKELETON, 'build.gradle.react').text

        expect:
        text.contains('com.github.node-gradle:gradle-node-plugin')
        text.contains('com.github.node-gradle.node')
        text.contains("tasks.register('buildFrontend'")
        text.contains("processResources.dependsOn('buildFrontend')")
    }

    def "post-create.sh moves build.gradle.react into place"() {
        given:
        def text = new File(SKELETON, 'post-create.sh').text

        expect:
        text.contains('mv build.gradle.react build.gradle')
        text.contains('rm -- "$0"')
    }

    def "skeleton gradle.properties pins JVM and Node toolchain versions"() {
        given:
        def text = new File(SKELETON, 'gradle.properties').text

        expect:
        text.contains('grailsVersion=')
        text.contains('javaVersion=')
        text.contains('nodeVersion=')
        text.contains('npmVersion=')
        text.contains('nodePluginVersion=')
    }

    def "skeleton application.yml configures the public/ static location"() {
        given:
        def text = new File(SKELETON, 'grails-app/conf/application.yml').text

        expect:
        text.contains('static-locations: classpath:/public/')
        text.contains("@grails.codegen.defaultPackage@")
    }

    def "skeleton resources.groovy serves Vite-built /_app/** assets"() {
        given:
        def text = new File(SKELETON, 'grails-app/conf/spring/resources.groovy').text

        expect:
        text.contains('reactAssetsHandler')
        text.contains('/_app/**')
    }

    def "skeleton has package-substituted controller and init paths"() {
        given:
        def packageDir = '@grails.codegen.defaultPackage.path@'

        expect:
        new File(SKELETON, "grails-app/controllers/${packageDir}/AppController.groovy").exists()
        new File(SKELETON, "grails-app/controllers/${packageDir}/UrlMappings.groovy").exists()
        new File(SKELETON, "grails-app/init/${packageDir}/Application.groovy").exists()
        new File(SKELETON, "grails-app/init/${packageDir}/BootStrap.groovy").exists()
    }

    def "controllers and init classes use the package substitution token"() {
        given:
        def packageDir = '@grails.codegen.defaultPackage.path@'
        def files = [
            new File(SKELETON, "grails-app/controllers/${packageDir}/AppController.groovy"),
            new File(SKELETON, "grails-app/controllers/${packageDir}/UrlMappings.groovy"),
            new File(SKELETON, "grails-app/init/${packageDir}/Application.groovy"),
        ]

        expect:
        files.every { it.text.contains('package @grails.codegen.defaultPackage@') }
    }

    def "AppController serves index.html from the classpath"() {
        given:
        def packageDir = '@grails.codegen.defaultPackage.path@'
        def text = new File(SKELETON, "grails-app/controllers/${packageDir}/AppController.groovy").text

        expect:
        text.contains("'public/index.html'")
        text.contains('Frontend not built')
    }

    def "frontend skeleton has all expected files"() {
        expect:
        new File(SKELETON, 'frontend/package.json').exists()
        new File(SKELETON, 'frontend/package-lock.json').exists()
        new File(SKELETON, 'frontend/vite.config.ts').exists()
        new File(SKELETON, 'frontend/tsconfig.json').exists()
        new File(SKELETON, 'frontend/index.html').exists()
        new File(SKELETON, 'frontend/src/main.tsx').exists()
        new File(SKELETON, 'frontend/src/App.tsx').exists()
        new File(SKELETON, 'frontend/src/index.css').exists()
    }

    def "frontend package.json has the expected stack"() {
        given:
        def text = new File(SKELETON, 'frontend/package.json').text

        expect:
        text.contains('"@grails.app.name@-frontend"')
        text.contains('"react"')
        text.contains('"react-dom"')
        text.contains('"react-router-dom"')
        text.contains('"vite"')
        text.contains('"typescript"')
        text.contains('"tailwindcss"')
        text.contains('"@tailwindcss/vite"')
    }

    def "vite.config.ts targets src/main/resources/public and proxies /api"() {
        given:
        def text = new File(SKELETON, 'frontend/vite.config.ts').text

        expect:
        text.contains("outDir: '../src/main/resources/public'")
        text.contains("assetsDir: '_app'")
        text.contains("'/api'")
        text.contains('http://localhost:8080')
    }

    def "frontend index.html and App.tsx use the app-name substitution"() {
        expect:
        new File(SKELETON, 'frontend/index.html').text.contains('<title>@grails.app.name@</title>')
        new File(SKELETON, 'frontend/src/App.tsx').text.contains('@grails.app.name@')
    }

    def "main.tsx wraps app in StrictMode and HashRouter"() {
        given:
        def text = new File(SKELETON, 'frontend/src/main.tsx').text

        expect:
        text.contains('StrictMode')
        text.contains('HashRouter')
    }

    def ".gitignore excludes node_modules and Vite build output"() {
        given:
        def text = new File(SKELETON, '.gitignore').text

        expect:
        text.contains('frontend/node_modules/')
        text.contains('src/main/resources/public/')
    }

    def "profile.yml extends the web profile"() {
        given:
        def text = new File(PROJECT_ROOT, 'profile.yml').text

        expect:
        text.contains('name: grails-react')
        text.contains('extends:')
        text.contains('- web')
    }
}
