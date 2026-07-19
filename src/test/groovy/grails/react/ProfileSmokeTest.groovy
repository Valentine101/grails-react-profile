package grails.react

import spock.lang.Specification

import java.util.jar.JarFile

/**
 * Structural smoke checks on the profile's skeleton files.
 *
 * These guard against accidental skeleton regressions (missing files,
 * dropped substitution tokens, etc.). End-to-end generation is exercised
 * separately against the locally published profile during release validation.
 */
class ProfileSmokeTest extends Specification {

    static final File PROJECT_ROOT = new File(System.getProperty('user.dir'))
    static final File SKELETON = new File(PROJECT_ROOT, 'skeleton')

    def "skeleton root contains expected files"() {
        expect:
        new File(SKELETON, 'build.gradle').exists()
        new File(SKELETON, 'settings.gradle').exists()
        new File(SKELETON, 'gradle.properties').exists()
        new File(SKELETON, 'gitignore').exists()
        new File(SKELETON, '.sdkmanrc').exists()
        new File(SKELETON, 'README.md').exists()
        new File(SKELETON, 'gradlew').exists()
        new File(SKELETON, 'gradle/wrapper/gradle-wrapper.jar').exists()
        new File(SKELETON, 'gradle/wrapper/gradle-wrapper.properties').exists()
        new File(SKELETON, 'grailsw').exists()
        new File(SKELETON, 'grails-wrapper.jar').exists()
        !new File(SKELETON, 'build.gradle.react').exists()
        !new File(SKELETON, 'post-create.sh').exists()
    }

    def "skeleton build.gradle wires the node-gradle plugin and buildFrontend task"() {
        given:
        def text = new File(SKELETON, 'build.gradle').text

        expect:
        text.contains('com.github.node-gradle:gradle-node-plugin')
        text.contains('com.github.node-gradle.node')
        text.contains("tasks.register('buildFrontend'")
        text.contains("processResources.dependsOn('buildFrontend')")
        text.contains("providers.gradleProperty('javaVersion').get().toInteger()")
        text.contains('languageVersion = JavaLanguageVersion.of(targetJavaVersion)')
        text.contains('options.release = targetJavaVersion')
        text.contains('profile "@grails.profile@"')
        text.contains('spring-boot-starter-jackson')
        text.contains('frontend/tsconfig.json')
        text.count('frontend/package-lock.json') == 2
    }

    def "skeleton gradle.properties pins JVM and Node toolchain versions"() {
        given:
        def text = new File(SKELETON, 'gradle.properties').text

        expect:
        text.contains('grailsVersion=8.0.0-M3')
        text.contains('javaVersion=21')
        text.contains('nodeVersion=')
        text.contains('npmVersion=')
        text.contains('nodePluginVersion=')
    }

    def "SDKMAN environment pins Grails M3 and Amazon Corretto 21"() {
        given:
        def rootEnvironment = new File(PROJECT_ROOT, '.sdkmanrc').text
        def appEnvironment = new File(SKELETON, '.sdkmanrc').text

        expect:
        [rootEnvironment, appEnvironment].every {
            it.contains('java=21.0.11-amzn') && it.contains('grails=8.0.0-M3')
        }
    }

    def "skeleton application.yml configures the public/ static location"() {
        given:
        def text = new File(SKELETON, 'grails-app/conf/application.yml').text

        expect:
        text.contains('static-locations: classpath:/public/')
        text.contains("@grails.codegen.defaultPackage@")
    }

    def "skeleton application.yml hardens error responses for React consumers"() {
        given:
        def text = new File(SKELETON, 'grails-app/conf/application.yml').text

        expect:
        text.contains('include-stacktrace: never')
        text.contains('include-message: never')
        text.contains('include-message: always')
        text.contains("include: 'health,info'")
        !text.contains("include: '*'")
    }

    def "skeleton resources.groovy serves Vite-built /_app/** assets"() {
        given:
        def text = new File(SKELETON, 'grails-app/conf/spring/resources.groovy').text

        expect:
        text.contains('reactAssetsHandler')
        text.contains('/_app/**')
        text.contains('CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable()')
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
        text.contains('stream.withCloseable')
    }

    def "UrlMappings does NOT reference GSP error views"() {
        // Regression guard for v1.0.0 bug: GSP `/notFound` and `/error`
        // view mappings caused ServletException on every 404 because this
        // profile doesn't include the GSP runtime. Removed in v1.0.1.
        given:
        def packageDir = '@grails.codegen.defaultPackage.path@'
        def text = new File(SKELETON, "grails-app/controllers/${packageDir}/UrlMappings.groovy").text

        expect:
        !text.contains("view:'/notFound'")
        !text.contains("view:'/error'")
        !text.contains('/$controller/$action')
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
        text.contains("base: './'")
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

    def "generated gitignore excludes Gradle, node_modules, and Vite build output"() {
        given:
        def text = new File(SKELETON, 'gitignore').text

        expect:
        text.contains('.gradle')
        text.contains('build/')
        text.contains('frontend/node_modules/')
        text.contains('src/main/resources/public/')
    }

    def "profile JAR includes the React gitignore defaults"() {
        given:
        def version = new File(PROJECT_ROOT, 'gradle.properties')
            .readLines()
            .find { it.startsWith('profileVersion=') }
            .split('=', 2)[1]
        def jar = new File(PROJECT_ROOT, "build/libs/grails-react-${version}.jar")

        expect:
        jar.exists()
        new JarFile(jar).withCloseable {
            it.getEntry('META-INF/grails-profile/skeleton/.gitignore') != null
        }
    }

    def "profile JAR excludes generated local build state"() {
        given:
        def version = new File(PROJECT_ROOT, 'gradle.properties')
            .readLines()
            .find { it.startsWith('profileVersion=') }
            .split('=', 2)[1]
        def jar = new File(PROJECT_ROOT, "build/libs/grails-react-${version}.jar")

        expect:
        new JarFile(jar).withCloseable {
            !it.entries().any { entry ->
                entry.name.startsWith('META-INF/grails-profile/skeleton/.gradle/') ||
                    entry.name.startsWith('META-INF/grails-profile/skeleton/build/') ||
                    entry.name.startsWith('META-INF/grails-profile/skeleton/frontend/node_modules/')
            }
        }
    }

    def "profile.yml defines a self-contained executable Grails 8 skeleton"() {
        given:
        def text = new File(PROJECT_ROOT, 'profile.yml').text

        expect:
        text.contains('name: grails-react')
        text.contains('skeleton:')
        text.contains('executable:')
        text.contains('binaryExtensions:')
        !text.contains('extends:')
        !text.contains('org.apache.grails.profiles:base')
    }
}
