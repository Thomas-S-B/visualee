

    Explore
    Gist
    Blog
    Help

    Thomas-S-B

    1
    0
    0

public Thomas-S-B/visualee

visualee /

or cancel

16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
59
               <execution>
                  <phase>compile</phase>
                  <goals>
                     <goal>visualize</goal>
                  </goals>
               </execution>
            </executions>
         </plugin>
Alternatively you can use a ready to use visualee-plugin.
Add this to your pom:
        <project
        <pluginRepositories>
                <pluginRepository>
                         <id>strullerbaumann-ftp</id>
                        <name>Visualee Repository</name>
                        <url>http://www.struller-baumann.de/maven2</url>
                </pluginRepository>
        </pluginRepositories>
        <build>
                <plugins>
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
                </plugins>
        </build>
        </project>

The visualization will be generated in a project outputdirectory called "visualee".
Open the index.html in your browser and select one of the generated graphs.
It is recommended to use the google chrome browser, due to it's javascript performance and HTML5-capabilities.
It's only tested with chrome version 28.0.1500.71 and firefox version 22.
