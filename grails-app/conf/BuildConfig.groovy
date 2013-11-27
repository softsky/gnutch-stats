grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

String camelVersion = "2.11.1"
String activeMQVersion = "5.8.0"

grails.project.dependency.resolution = {
  // inherit Grails' default dependencies
  inherits("global") {
    // uncomment to disable ehcache
    // excludes 'ehcache'
  }
  log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
  legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility
  repositories {
    grailsCentral()
    mavenCentral()
    // uncomment the below to enable remote dependency resolution
    // from public Maven repositories
    //mavenLocal()
    //mavenRepo "http://snapshots.repository.codehaus.org"
    //mavenRepo "http://repository.codehaus.org"
    //mavenRepo "http://download.java.net/maven/2/"
    //mavenRepo "http://repository.jboss.com/maven2/"
  }
  dependencies {
    // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

    // runtime 'mysql:mysql-connector-java:5.1.21'

    compile ("org.apache.camel:camel-core:${camelVersion}") { excludes 'slf4j-api' }   
    compile ("org.apache.camel:camel-http:${camelVersion}") { excludes 'commons-codec' }
    compile ("org.apache.camel:camel-mail:${camelVersion}")
    compile ("org.apache.camel:camel-groovy:${camelVersion}") { excludes 'groovy-all' }
    compile ("org.apache.camel:camel-spring:${camelVersion}") { excludes 'log4j', 'spring-tx', 'spring-jms','spring-context', 'spring-beans', 'spring-aop' }
    compile ("org.apache.camel:camel-jms:${camelVersion}")  { excludes 'spring-tx', 'spring-jms','spring-context', 'spring-beans', 'spring-aop', 'spring-core' }
    compile ("org.apache.camel:camel-cache:${camelVersion}") { excludes 'xercesImpl', 'xml-apis', 'slf4j-api', 'ehcache'  }
    compile ("org.apache.camel:camel-tagsoup:${camelVersion}")

    test ("org.apache.camel:camel-test:${camelVersion}") { excludes 'junit' }

  }

  plugins {
    build(":release:2.2.1",
          ":rest-client-builder:1.0.3") {
      export = false
    }
  }
}
