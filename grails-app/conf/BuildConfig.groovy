grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

  inherits 'global'
  log 'warn'

  repositories {
    grailsCentral()
    mavenLocal()
    mavenCentral()
  }

  dependencies {

    def camelVersion = '2.12.1'

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
    build ':release:2.2.1', ':rest-client-builder:1.0.3', {
      export = false
    }
  }
}
