grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

def camelVersion = '2.11.0' // integration tests does not work on 2.12.1

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
    mavenLocal()
    // mavenRepo "http://snapshots.repository.codehaus.org"
    // mavenRepo "http://repository.codehaus.org"
    // mavenRepo "http://download.java.net/maven/2/"
    // mavenRepo "http://repository.jboss.com/maven2/"
  }
  dependencies {
    // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

    // runtime 'mysql:mysql-connector-java:5.1.21'

    compile ("org.apache.camel:camel-core:${camelVersion}") { excludes 'slf4j-api' }   
    compile ("org.apache.camel:camel-spring:${camelVersion}") { excludes 'log4j', 'spring-tx', 'spring-context', 'spring-aop' }

    compile ("org.quartz-scheduler:quartz:2.1.6") { excludes 'slf4j-api' }

    test ("org.apache.camel:camel-test:${camelVersion}") { excludes 'junit' }

  }

  plugins {
    build(":tomcat:$grailsVersion",
          ":release:2.2.1",
          ":rest-client-builder:1.0.3") {
      export = false
    }
  }
}
