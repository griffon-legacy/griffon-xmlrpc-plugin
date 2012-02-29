griffon.project.dependency.resolution = {
    inherits("global") 
    log "warn"
    repositories {
        griffonHome()
        mavenCentral()
    }
    dependencies {
        compile('org.codehaus.groovy:groovy-xmlrpc:0.7') {
            transitive = false
        }
        String smackVersion = '3.2.1'
        compile "org.igniterealtime.smack:smack:$smackVersion",
                "org.igniterealtime.smack:smackx:$smackVersion"
    }
}

griffon {
    doc {
        logo = '<a href="http://griffon.codehaus.org" target="_blank"><img alt="The Griffon Framework" src="../img/griffon.png" border="0"/></a>'
        sponsorLogo = "<br/>"
        footer = "<br/><br/>Made with Griffon (@griffon.version@)"
    }
}

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%d [%t] %-5p %c - %m%n')
    }

    error 'org.codehaus.griffon',
          'org.springframework',
          'org.apache.karaf',
          'groovyx.net'
    warn  'griffon'
}
