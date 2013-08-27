visualee
========

A maven plugin to visualize a java ee app.

After successful build, only put this in your maven-pom:

        <plugin>
            <groupId>de.strullerbaumann</groupId>
            <artifactId>visualee</artifactId>
            <version>0.14</version>
            <configuration>
               <outputdirectory>visualee</outputdirectory>
            </configuration>
            <executions>
               <execution>
                  <phase>compile</phase>
                  <goals>
                     <goal>visualize</goal>
                  </goals>
               </execution>
            </executions>
         </plugin>

Alternatively you can use a ready to use visualee-plugin.
Add this also to your pom:

    <pluginRepositories>
      <pluginRepository>
         <id>strullerbaumann-ftp</id>
         <name>Visualee Repository</name>
         <url>http://www.struller-baumann.de/maven2</url>
      </pluginRepository>
    </pluginRepositories>

The visualization will be generated in a project outputdirectory called "visualee".
Open the index.html in your browser and select one of the generated graphs.
It is recommended to use the google chrome browser, due to it's javascript performance and HTML5-capabilities.
It's only tested with chrome version 28.0.1500.71 and firefox version 22.
