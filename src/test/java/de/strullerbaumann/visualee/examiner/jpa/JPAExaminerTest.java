package de.strullerbaumann.visualee.examiner.jpa;

/*
 * #%L
 * visualee
 * %%
 * Copyright (C) 2013 Thomas Struller-Baumann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import de.strullerbaumann.visualee.dependency.boundary.DependencyContainer;
import de.strullerbaumann.visualee.dependency.entity.Dependency;
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.examiner.SourceCodeProvider;
import de.strullerbaumann.visualee.source.entity.JavaSource;
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
      ExaminerJPA jpaExaminer = new ExaminerJPA();
      assertTrue(jpaExaminer.isRelevantType(DependencyType.MANY_TO_MANY));
      assertTrue(jpaExaminer.isRelevantType(DependencyType.MANY_TO_ONE));
      assertTrue(jpaExaminer.isRelevantType(DependencyType.ONE_TO_ONE));
      assertTrue(jpaExaminer.isRelevantType(DependencyType.ONE_TO_MANY));
      assertFalse(jpaExaminer.isRelevantType(DependencyType.EJB));
      assertFalse(jpaExaminer.isRelevantType(DependencyType.EVENT));
      assertFalse(jpaExaminer.isRelevantType(DependencyType.INJECT));
      assertFalse(jpaExaminer.isRelevantType(DependencyType.INSTANCE));
      assertFalse(jpaExaminer.isRelevantType(DependencyType.OBSERVES));
      assertFalse(jpaExaminer.isRelevantType(DependencyType.PRODUCES));
   }

   @Test
   public void testFindAndSetAttributesManyToOne() {
      ExaminerJPA jpaExaminer = new ExaminerJPA();
      JavaSource javaSource;
      Dependency dependency;
      String sourceCode;

      // Many to one
      javaSource = new JavaSource("MyTestClass");
      sourceCode = SourceCodeProvider.getTestSourceCodeBeforeBody()
              + "@ManyToOne(cascade = { CascadeType.DETACH })\n"
              + "@JoinColumn(name = \"ALBUMID\", nullable = false)\n"
              + "@NotNull(groups = PersistenceConstraint.class)\n"
              + "private Album album;\n";
      javaSource.setSourceCode(sourceCode);
      jpaExaminer.examine(javaSource);
      dependency = DependencyContainer.getInstance().getDependencies(javaSource).get(0);
      assertEquals(1, DependencyContainer.getInstance().getDependencies(javaSource).size());
      assertEquals(DependencyType.MANY_TO_ONE, dependency.getDependencyType());
      assertEquals("MyTestClass", dependency.getJavaSourceFrom().getName());
      assertEquals("Album", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindAndSetAttributesManyToMany() {
      ExaminerJPA jpaExaminer = new ExaminerJPA();
      JavaSource javaSource;
      Dependency dependency;
      String sourceCode;

      // Many to many
      javaSource = new JavaSource("User");
      sourceCode = SourceCodeProvider.getTestSourceCodeBeforeBody()
              + "@ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY)\n"
              + "@JoinTable(name = \"USERS_GROUPS\", joinColumns = {\n"
              + "   @JoinColumn(name = \"USERID\", nullable = false)}, inverseJoinColumns = {\n"
              + "   @JoinColumn(name = \"GROUPID\", nullable = false)})\n"
              + "private Set<Group> groups;\n";
      javaSource.setSourceCode(sourceCode);
      jpaExaminer.examine(javaSource);
      dependency = DependencyContainer.getInstance().getDependencies(javaSource).get(0);
      assertEquals(1, DependencyContainer.getInstance().getDependencies(javaSource).size());
      assertEquals(DependencyType.MANY_TO_MANY, dependency.getDependencyType());
      assertEquals("User", dependency.getJavaSourceFrom().getName());
      assertEquals("Group", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindAndSetAttributesIgnoreComments() {
      ExaminerJPA jpaExaminer = new ExaminerJPA();
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
      assertEquals(0, DependencyContainer.getInstance().getDependencies(javaSource).size());
   }

   @Test
   public void testFindAndSetAttributesIgnoreCommentBlocks() {
      ExaminerJPA jpaExaminer = new ExaminerJPA();
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
      assertEquals(0, DependencyContainer.getInstance().getDependencies(javaSource).size());
   }
}
