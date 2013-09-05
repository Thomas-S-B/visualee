visualee
========

A maven plugin to visualize a java ee app.

After successful build, only put this in your maven-pom:

        <plugin>
            <groupId>de.strullerbaumann</groupId>
            <artifactId>visualee</artifactId>
            <version>0.15</version>
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


It's possible to configure the graphs individually.
The attributes are:
- distance
- gravity
- graphsize
- fontsize (in percent)

The graph-name is the name of the genreted files.

Example:
          <plugin>
            <groupId>de.strullerbaumann</groupId>
            <artifactId>visualee</artifactId>
            <version>0.14</version>
            <configuration>
               <outputdirectory>visualee</outputdirectory>
               <graphs>
                  <graph>
                     <name>graphEventObserverClasses</name>
                     <distance>100</distance>
                     <gravity>10</gravity>
                     <graphsize>600</graphsize>
                  </graph>
                  <graph>
                     <name>graphInjectClasses</name>
                     <distance>200</distance>
                     <gravity>20</gravity>
                     <graphsize>800</graphsize>
                     <fontsize>110</fontsize>
                  </graph>
                  <graph>
                     <name>graphEJBClasses</name>
                     <distance>300</distance>
                     <gravity>30</gravity>
                     <graphsize>900</graphsize>
                  </graph>
                  <graph>
                     <name>graphInstanceClasses</name>
                     <distance>200</distance>
                     <gravity>20</gravity>
                     <graphsize>800</graphsize>
                  </graph>
                  <graph>
                     <name>graphProducesClasses</name>
                     <distance>200</distance>
                     <gravity>20</gravity>
                     <graphsize>800</graphsize>
                  </graph>
                  <graph>
                     <name>graphResourcesClasses</name>
                     <distance>200</distance>
                     <gravity>20</gravity>
                     <graphsize>800</graphsize>
                  </graph>
                  <graph>
                     <name>graphJPAClasses</name>
                     <distance>200</distance>
                     <gravity>20</gravity>
                     <graphsize>800</graphsize>
                  </graph>
                  <graph>
                     <name>graphOnlyCDIJPA</name>
                     <distance>200</distance>
                     <gravity>20</gravity>
                     <graphsize>800</graphsize>
                  </graph>
                  <graph>
                     <name>graphAllClasses</name>
                     <fontsize>10</fontsize>
                  </graph>
               </graphs>
            </configuration>
            <executions>
               <execution>
                  <phase>process-resources</phase>
                  <goals>
                     <goal>visualize</goal>
                  </goals>
               </execution>
            </executions>
         </plugin>