看了一些scala的语法，写了点简单的脚本后，打算尝试用scala开发web应用。
因为目前主要在用spring做java的web开发，如果放弃现有的技术框架成本太高，
所以目标是能够继承spring和scala，既能依靠之前的技术栈，又可以尝试scala的特性。
尝试开始：

#搭建项目框架
在网上找了一些scala web相关的说明，但是没有符合需求的，有提到用SBT或者scala的一些框架，也有说spring-scala和spring-boot。
spring-scala的最新版本是2013年发布的，不知道是不是还在更新，SBT的例子基本是直接继承Servlet，没有利用到spring，而我最终的目的是能够继续使用Maven，Jenkins这一套，所以决定还是回归老本行，先搭一个简单的java web项目。

##创建Java Web项目
正常创建Java Web项目，导入Maven支持，调整目录结构和pom文件，顺手建了src/main/scala目录备用。
![项目结构图片](https://derfighyt.github.io/notes/scala/img/01-simple_web_app_structure.png)

加入web.xml，log4j.xml，spring-web-servlet.xml三个配置文件

###pom.xml
需要引入scala-maven-plugin和scala相关的依赖，scalatra依赖的akka-actor报错，单独引用
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.derfy</groupId>
    <artifactId>scala-web-app</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>war</packaging>

    <name>scalaWebApp</name>
    <description>learning to build a web application based on scala</description>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <scala.version>2.11.8</scala.version>
    </properties>

    <repositories>
        <repository>
            <id>scalaz</id>
            <name>scalaz</name>
            <url>http://dl.bintray.com/scalaz/releases</url>
        </repository>
        <repository>
            <id>central</id>
            <name>Maven Repository Switchboard</name>
            <url>http://repo1.maven.org/maven2</url>
        </repository>
        <repository>
            <id>milestone.repo.springsource.org</id>
            <name>repo.springsource.org-milestone</name>
            <url>https://repo.springsource.org/libs-milestone</url>
        </repository>
    </repositories>

    <build>
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/resources/</directory>
                <includes>
                    <include>*.properties</include>
                    <include>*.xml</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/resources/spring</directory>
                <targetPath>spring</targetPath>
                <includes>
                    <include>*.xml</include>
                </includes>
            </resource>
        </resources>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.1</version>
                <configuration>
                    <source>1.8</source> <!-- 源代码使用的开发版本 -->
                    <target>1.8</target> <!-- 需要生成的目标class文件的编译版本 -->
                </configuration>
            </plugin>
            <plugin>
                <groupId>net.alchim31.maven</groupId>
                <artifactId>scala-maven-plugin</artifactId>
                <version>3.2.1</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>testCompile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>build-helper-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <id>add-source</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>add-source</goal>
                        </goals>
                        <configuration>
                            <sources>
                                <source>src/main/scala</source>
                            </sources>
                        </configuration>
                    </execution>
                    <execution>
                        <id>add-test-source</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>add-test-source</goal>
                        </goals>
                        <configuration>
                            <sources>
                                <source>src/test/scala</source>
                            </sources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>4.2.5.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.scala-lang</groupId>
            <artifactId>scala-library</artifactId>
            <version>${scala.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.scala-lang</groupId>
            <artifactId>scala-compiler</artifactId>
            <version>${scala.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.scala-lang</groupId>
            <artifactId>scala-reflect</artifactId>
            <version>${scala.version}</version>
        </dependency>
        <dependency>
            <groupId>org.scala-lang</groupId>
            <artifactId>scala-actors</artifactId>
            <version>${scala.version}</version>
        </dependency>
        <dependency>
            <groupId>com.typesafe.akka</groupId>
            <artifactId>akka-actor_2.10</artifactId>
            <version>2.2-M3</version>
        </dependency>
        <dependency>
            <groupId>org.scalatra</groupId>
            <artifactId>scalatra</artifactId>
            <version>2.2.0</version>
            <exclusions>
                <exclusion>
                    <groupId>com.typesafe.akka</groupId>
                    <artifactId>akka-actor</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>servlet-api</artifactId>
            <version>2.5</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.16</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.5</version>
        </dependency>
    </dependencies>

</project>
```

###web.xml
正常web项目的配置，指定过滤器，log4j，使用spring的DispatcherServlet
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
		  http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
         version="2.5">

    <filter>
        <filter-name>characterEncodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
        <init-param>
            <param-name>forceEncoding</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>

    <filter-mapping>
        <filter-name>characterEncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <!-- log 配置-->
    <context-param>
        <param-name>log4jConfigLocation</param-name>
        <param-value>classpath:log4j.xml</param-value>
    </context-param>
    <context-param>
        <param-name>log4jRefreshInterval</param-name>
        <param-value>60000</param-value>
    </context-param>
    <listener>
        <listener-class>org.springframework.web.util.Log4jConfigListener</listener-class>
    </listener>

    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:/spring/spring-web-servlet.xml</param-value>
    </context-param>
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <servlet>
        <servlet-name>scala</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextAttribute</param-name>
            <param-value>org.springframework.web.context.WebApplicationContext.ROOT</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>scala</servlet-name>
        <url-pattern>/*</url-pattern>
    </servlet-mapping>

    <context-param>
        <param-name>webAppRootKey</param-name>
        <param-value>scala.webapp.root</param-value>
    </context-param>

    <welcome-file-list>
        <welcome-file>index.jsp</welcome-file>
    </welcome-file-list>

    <display-name>scala.webapp</display-name>

</web-app>
```

###log4j.xml
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">

    <appender name="CONSOLE" class="org.apache.log4j.ConsoleAppender">
        <param name="encoding" value="UTF-8"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d %-5p [%c{1}] %m%n" />
        </layout>
    </appender>

    <root>
        <priority value="INFO" />
        <appender-ref ref="CONSOLE" />
    </root>

</log4j:configuration>
```

###spring-web-servlet.xml
指定component-scan的位置，这里在实验时发现，我在src/main/java和src/main/scala下面定义了两个同名的包，运行时都能够被spring扫描到。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context-3.0.xsd
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
        http://www.springframework.org/schema/mvc
		http://www.springframework.org/schema/mvc/spring-mvc-3.0.xsd">

    <mvc:annotation-driven/>
    <mvc:default-servlet-handler/>

    <!-- 扫描 -->
    <context:annotation-config/>
    <context:component-scan base-package="com.derfy"/>
    
</beans>
```

##测试spring框架
在src/main/java下面创建com.derfy.web.controller包，创建HelloJavaController.java
###HelloJavaController.java
```java
package com.derfy.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "java")
public class HelloJavaController {

    @RequestMapping(path = "/hello")
    @ResponseBody
    public String hello() {
        return "hello java";
    }
}
```
配置tomcat，启动，浏览器输入http://localhost:8080/java/hello，输出"hello java"，项目成功启动。

##实验spring对scala的支持
在src/main/scala下面创建com.derfy.web.controller包，创建HelloScalaController.scala
复制java代码过来自动转换为scala风格，做一些修改。
注意这里直接按java的方式在类和方法上使用了spring的注解，但RequestMapping的path需要使用字符串数组。
###HelloScalaController.scala
```scala
package com.derfy.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{RequestMapping, ResponseBody}

@Controller
@RequestMapping(path = Array("scala"))
class HelloScalaController {

  @RequestMapping(path = Array("/hello"))
  @ResponseBody
  def hello(): String = {
    "hello scala"
  }

}
```
启动tomcat，能够正常启动，输出如下日志，说明两个Controller类都被spring扫描到并加载。
```
2017-02-23 17:28:08,474 INFO  [RequestMappingHandlerMapping] Mapped "{[/java/hello]}" onto public java.lang.String com.derfy.web.controller.HelloJavaController.hello()
2017-02-23 17:28:08,476 INFO  [RequestMappingHandlerMapping] Mapped "{[/scala/hello]}" onto public java.lang.String com.derfy.web.controller.HelloScalaController.hello()
```
浏览器输入http://localhost:8080/scala/hello，输出"hello scala"，scala风格的Controller也可以成功运行。

尝试加入参数，PathVariable注解也可以正常使用
###HelloScalaController.scala
```scala
package com.derfy.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, ResponseBody}

@Controller
@RequestMapping(path = Array("scala"))
class HelloScalaController {

  @RequestMapping(path = Array("/hello"))
  @ResponseBody
  def hello(@PathVariable name: String): String = {
    "hello" + name
  }

}
```
