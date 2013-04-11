visualee
========

A maven plugin to visualize a java ee app.

Only put this in your maven-pom:

        <plugin>
            <groupId>de.strullerbaumann</groupId>
            <artifactId>visualee</artifactId>
            <version>0.11</version>
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

The visualization will be generated in a project outputdirectory called "visualee".
Open the index.html in your browser and select one of the generated graphs.

