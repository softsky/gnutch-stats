grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = 'target'

def camelVersion = '2.11.0' // integration tests does not work on 2.12.1

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

    compile ("org.quartz-scheduler:quartz:2.2.0") { excludes 'slf4j-api' }

    test ("org.apache.camel:camel-test:${camelVersion}") { excludes 'junit' }
  }

  plugins {
    build ':release:2.2.1', ':rest-client-builder:1.0.3', {
      export = false
    }
  }
}
