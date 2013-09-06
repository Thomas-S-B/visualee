visualee
========

A maven plugin to visualize a java ee app.

Please look into the wiki.

Some notes to the implementation:
Visualee scans the java-source-files for relevant dependencies.
The dependencies are generated as JSON-Files.
This JSON-Files are the input for the d3.js-visualisation.
The GUI uses jquery and jquery-ui.

Why not using java-reflection?

Early versions used java reflection to examine the java-classes, but i decided against it and prefer a simple scan of the source-files, because:
- it's possible to visualize even not compilable code (important for emergency cases)
- the implementation is simpler (the famous rule of thumb: avoid reflection)
- it's easier to implement visualee to other languages
- much less trouble with plugin-configuration, because the classes must be loadable (cue: „absent code...“, implemenation of the EE-Satck must be included).