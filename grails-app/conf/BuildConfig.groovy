grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = 'target'

def camelVersion = '2.12.3' // integration tests does not work on 2.12.1

grails.project.fork = [

        // configure settings for the test-app JVM, uses the daemon by default

        test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon: true],

// configure settings for the run-app JVM

        run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve: false],

// configure settings for the run-war JVM

        war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve: false],

// configure settings for the Console UI JVM

        console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]

]

grails.project.dependency.resolver = "maven" // or ivy

grails.project.dependency.resolution = {

  inherits 'global'
  log 'warn'

  repositories {
    grailsCentral()
    mavenLocal()
    mavenCentral()
  }

  dependencies {

    test ("org.apache.camel:camel-core:${camelVersion}") { excludes 'slf4j-api' }   
    test ("org.apache.camel:camel-spring:${camelVersion}") { excludes 'log4j', 'spring-tx', 'spring-context', 'spring-aop' }

    compile ("org.quartz-scheduler:quartz:2.2.1") { excludes 'slf4j-api' }

    test ("org.apache.camel:camel-test:${camelVersion}") { excludes 'junit' }
  }

  plugins {
    build ":tomcat:7.0.50", ':release:2.2.1', ':rest-client-builder:1.0.3', {
      export = false
    }
  }
}
