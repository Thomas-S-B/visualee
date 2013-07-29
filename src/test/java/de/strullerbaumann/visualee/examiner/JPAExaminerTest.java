/*
 Copyright 2013 Thomas Struller-Baumann, struller-baumann.de

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package de.strullerbaumann.visualee.examiner;

import de.strullerbaumann.visualee.dependency.Dependency;
import de.strullerbaumann.visualee.dependency.DependenciyType;
import de.strullerbaumann.visualee.resources.JavaSource;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JPAExaminerTest {

   public JPAExaminerTest() {
   }

   @Test
   public void testIsRelevantType() {
      JPAExaminer jpaExaminer = new JPAExaminer();
      assertTrue(jpaExaminer.isRelevantType(DependenciyType.MANY_TO_MANY));
      assertTrue(jpaExaminer.isRelevantType(DependenciyType.MANY_TO_ONE));
      assertTrue(jpaExaminer.isRelevantType(DependenciyType.ONE_TO_ONE));
      assertTrue(jpaExaminer.isRelevantType(DependenciyType.ONE_TO_MANY));
      assertFalse(jpaExaminer.isRelevantType(DependenciyType.EJB));
      assertFalse(jpaExaminer.isRelevantType(DependenciyType.EVENT));
      assertFalse(jpaExaminer.isRelevantType(DependenciyType.INJECT));
      assertFalse(jpaExaminer.isRelevantType(DependenciyType.INSTANCE));
      assertFalse(jpaExaminer.isRelevantType(DependenciyType.OBSERVES));
      assertFalse(jpaExaminer.isRelevantType(DependenciyType.PRODUCES));
   }

   @Test
   public void testFindAndSetAttributesManyToOne() {
      JPAExaminer jpaExaminer = new JPAExaminer();
      JavaSource javaSource;
      Dependency dependency;
      String sourceCode;

      // Many to one
      javaSource = new JavaSource("MyTestClass");
      sourceCode = getTestSourceCodeBeforeBody()
              + "@ManyToOne(cascade = { CascadeType.DETACH })\n"
              + "@JoinColumn(name = \"ALBUMID\", nullable = false)\n"
              + "@NotNull(groups = PersistenceConstraint.class)\n"
              + "private Album album;\n";
      javaSource.setSourceCode(sourceCode);
      jpaExaminer.examine(javaSource);
      dependency = javaSource.getInjected().get(0);
      assertEquals(1, javaSource.getInjected().size());
      assertEquals(DependenciyType.MANY_TO_ONE, dependency.getDependencyType());
      assertEquals("MyTestClass", dependency.getJavaSourceFrom().getName());
      assertEquals("Album", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindAndSetAttributesManyToMany() {
      JPAExaminer jpaExaminer = new JPAExaminer();
      JavaSource javaSource;
      Dependency dependency;
      String sourceCode;

      // Many to many
      javaSource = new JavaSource("User");
      sourceCode = getTestSourceCodeBeforeBody()
              + "@ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY)\n"
              + "@JoinTable(name = \"USERS_GROUPS\", joinColumns = {\n"
              + "   @JoinColumn(name = \"USERID\", nullable = false)}, inverseJoinColumns = {\n"
              + "   @JoinColumn(name = \"GROUPID\", nullable = false)})\n"
              + "private Set<Group> groups;\n";
      javaSource.setSourceCode(sourceCode);
      jpaExaminer.examine(javaSource);
      dependency = javaSource.getInjected().get(0);
      assertEquals(1, javaSource.getInjected().size());
      assertEquals(DependenciyType.MANY_TO_MANY, dependency.getDependencyType());
      assertEquals("User", dependency.getJavaSourceFrom().getName());
      assertEquals("Group", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindAndSetAttributesIgnoreComments() {
      JPAExaminer jpaExaminer = new JPAExaminer();
      JavaSource javaSource;
      String sourceCode;

      javaSource = new JavaSource("Cocktail");
      sourceCode = "//@Entity\n"
              + "//@Access(AccessType.FIELD)\n"
              + "public class Cocktail implements Comparable<Cocktail>\n"
              + "{\n"
              + "// @Id\n"
              + "private String             id;\n"
              + "private String             name;\n"
              + "// @ManyToMany\n"
              + "private Set<CocktailZutat> zutaten = new HashSet<CocktailZutat>();\n"
              + "// @ManyToOne\n"
              + "private CocktailZutat      basisZutat;\n"
              + "public Cocktail(String id, String name)\n"
              + "{\n"
              + "this.id = id;\n"
              + "this.name = name;\n"
              + "}\n";

      javaSource.setSourceCode(sourceCode);
      jpaExaminer.examine(javaSource);
      assertEquals(0, javaSource.getInjected().size());
   }

   @Test
   public void testFindAndSetAttributesIgnoreCommentBlocks() {
      JPAExaminer jpaExaminer = new JPAExaminer();
      JavaSource javaSource;
      String sourceCode;

      javaSource = new JavaSource("CocktailModel");
      sourceCode = "@Model\n"
              + "public class CocktailModel implements Serializable\n"
              + "{\n"
              + "  private List<Cocktail>     nonAlcoholicCocktails;\n"
              + "  private List<Cocktail>     alcoholicCocktails;\n"
              + "  /*\n"
              + "  @Inject\n"
              + "  private CocktailRepository cocktailRepository;\n"
              + "    */\n"
              + "  public List<Cocktail> getNonAlcoholicCocktails()\n"
              + "{\n"
              + "return this.nonAlcoholicCocktails;\n"
              + "}\n";

      javaSource.setSourceCode(sourceCode);
      jpaExaminer.examine(javaSource);
      assertEquals(0, javaSource.getInjected().size());
   }

   // TODO auslagern siehe auch JavaSourceExaminerTest
   private String getTestSourceCode() {
      return getTestSourceCodeBeforeBody() + getTestSourceCodeBody();
   }

   // TODO auslagern siehe auch JavaSourceExaminerTest
   private String getTestSourceCodeBeforeBody() {
      return "/*\n"
              + " Copyright 2013 Thomas Struller-Baumann, struller-baumann.de\n"
              + "\n"
              + " Licensed under the Apache License, Version 2.0 (the \"License\");\n"
              + " you may not use this file except in compliance with the License.\n"
              + " You may obtain a copy of the License at\n"
              + "\n"
              + " http://www.apache.org/licenses/LICENSE-2.0\n"
              + "\n"
              + " Unless required by applicable law or agreed to in writing, software\n"
              + " distributed under the License is distributed on an \"AS IS\" BASIS,\n"
              + " WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
              + " See the License for the specific language governing permissions and\n"
              + " limitations under the License.\n"
              + " */\n"
              + "package de.strullerbaumann.visualee.resources;\n"
              + "\n"
              + "import de.strullerbaumann.visualee.cdi.CDIDependency;\n"
              + "import de.strullerbaumann.visualee.cdi.CDIType;\n"
              + "import java.io.BufferedReader;\n"
              + "import java.io.FileNotFoundException;\n"
              + "import java.io.FileReader;\n"
              + "import java.io.IOException;\n"
              + "import java.util.HashMap;\n"
              + "import java.util.Map;\n"
              + "import java.util.Scanner;\n"
              + "import java.util.logging.Level;\n"
              + "import java.util.logging.Logger;\n"
              + "\n"
              + "/**\n"
              + " *\n"
              + " * @author Thomas Struller-Baumann <thomas at struller-baumann.de>\n"
              + " */\n"
              + "public class JavaSourceExaminer {\n"
              + "\n";
   }

   // TODO auslagern siehe auch JavaSourceExaminerTest
   private String getTestSourceCodeBody() {
      return "   private JavaSourceContainer javaSourceContainer;\n"
              + "    private static class JavaSourceExaminerHolder {\n"
              + "        private static final JavaSourceExaminer INSTANCE = new JavaSourceExaminer();\n"
              + "    }\n"
              + "    private JavaSourceExaminer() {\n"
              + "    }\n"
              + "    public static JavaSourceExaminer getInstance() {\n"
              + "        return JavaSourceExaminerHolder.INSTANCE;\n"
              + "    }\n"
              + "}\n";
   }
}
