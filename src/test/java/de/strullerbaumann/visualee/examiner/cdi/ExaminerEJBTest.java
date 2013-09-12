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
package de.strullerbaumann.visualee.examiner.cdi;

import de.strullerbaumann.visualee.dependency.entity.Dependency;
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.javasource.entity.JavaSource;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class ExaminerEJBTest {

   private ExaminerEJB examiner;

   public ExaminerEJBTest() {
   }

   @Before
   public void init() {
      examiner = new ExaminerEJB();
   }

   @Test
   public void testIsRelevantType() {
      for (DependencyType dependencyType : DependencyType.values()) {
         if (dependencyType == DependencyType.EJB) {
            assertTrue(examiner.isRelevantType(DependencyType.EJB));
         } else {
            assertFalse(examiner.isRelevantType(dependencyType));
         }
      }
   }

   @Test
   public void testgetTypeFromToken() {
      String sourceLine;
      DependencyType actual;

      sourceLine = "My test sourcecode";
      actual = examiner.getTypeFromToken(sourceLine);
      assertEquals(null, actual);

      sourceLine = "@EJB";
      actual = examiner.getTypeFromToken(sourceLine);
      assertEquals(DependencyType.EJB, actual);

      sourceLine = "@EJB(name = \"java:global/test/test-ejb/TestService\", beanInterface = TestService.class)";
      actual = examiner.getTypeFromToken(sourceLine);
      assertEquals(DependencyType.EJB, actual);
   }

   @Test
   public void testFindAndSetAttributesSetEJB() {
      JavaSource javaSource;
      String sourceCode;

      javaSource = new JavaSource("MyTestClass");
      sourceCode = "public abstract class MyTestClass<K, E extends SingleIdEntity<K>> implements CrudAccessor<K, E>, Serializable {\n"
              + "protected EntityManager entityManager;\n"
              + "private Class<E> entityClass;\n"
              + "@EJB\n"
              + "protected void setEntityManager(EntityManager entityManager) {\n"
              + "        this.entityManager = entityManager;\n"
              + "}\n";

      javaSource.setSourceCode(sourceCode);
      examiner.examine(javaSource);
      assertEquals(1, javaSource.getInjected().size());

      Dependency dependency;
      dependency = javaSource.getInjected().get(0);
      assertEquals(DependencyType.EJB, dependency.getDependencyType());
      assertEquals("MyTestClass", dependency.getJavaSourceFrom().getName());
      assertEquals("EntityManager", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindAndSetAttributesSetEJBPackageView() {
      JavaSource javaSource;
      String sourceCode;

      javaSource = new JavaSource("MyTestClass");
      sourceCode = "public abstract class MyTestClass {\n"
              + "@EJB\n"
              + "PersonSessionBean bean;"
              + "}\n";

      javaSource.setSourceCode(sourceCode);
      examiner.examine(javaSource);
      assertEquals(1, javaSource.getInjected().size());

      Dependency dependency;
      dependency = javaSource.getInjected().get(0);
      assertEquals(DependencyType.EJB, dependency.getDependencyType());
      assertEquals("MyTestClass", dependency.getJavaSourceFrom().getName());
      assertEquals("PersonSessionBean", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindAndSetAttributesEJBSetterWithAnnotations() {
      JavaSource javaSource;
      String sourceCode;

      javaSource = new JavaSource("ZeiterfassungEingabeModel");
      sourceCode = "public class ZeiterfassungEingabeModel implements Serializable\n"
              + "{\n"
              + "@EJB\n"
              + "protected void setBuchungsMonat(@Current @Zeiterfassung Date buchungsMonat)\n"
              + "{\n";

      javaSource.setSourceCode(sourceCode);
      examiner.examine(javaSource);
      assertEquals(1, javaSource.getInjected().size());

      Dependency dependency;
      dependency = javaSource.getInjected().get(0);
      assertEquals(DependencyType.EJB, dependency.getDependencyType());
      assertEquals("ZeiterfassungEingabeModel", dependency.getJavaSourceFrom().getName());
      assertEquals("Date", dependency.getJavaSourceTo().getName());
   }

   @Test
   public void testFindAndSetAttributesEJBWithParameter() {
      JavaSource javaSource;
      String sourceCode;

      javaSource = new JavaSource("AlbumServiceImpl");
      sourceCode = "@Stateless\n"
              + "@EJB(name = \"java:global/galleria/galleria-ejb/AlbumService\", beanInterface = AlbumService.class)\n"
              + "@RolesAllowed({ \"RegisteredUsers\" })\n"
              + "@TransactionAttribute(TransactionAttributeType.REQUIRED)\n"
              + "public class AlbumServiceImpl implements AlbumService\n"
              + "{\n"
              + "private static final Logger logger = LoggerFactory.getLogger(AlbumServiceImpl.class);\n"
              + " @Resource\n"
              + "	private SessionContext context;\n"
              + "	@EJB\n"
              + "	private UserRepository userRepository;\n"
              + "	@EJB(name = \"java:global/test/test-ejb/TestService\", beanInterface = TestService.class)\n"
              + "	private AlbumRepository albumRepository;\n"
              + "	@Override\n"
              + "	public Album createAlbum(Album album) throws AlbumException\n"
              + "	{\n"
              + "		validateAlbum(album);\n"
              + "		User user = findCurrentUser(CREATE_ALBUM_INTERNAL_ERROR);\n"
              + "		logger.debug(\"User's albums: {}\", user.getAlbums());\n"
              + "		if (user.getAlbums().contains(album))\n"
              + "		{\n"
              + "			logger.error(\"The album to be created, already exists.\");\n"
              + "			throw new AlbumException(DUPLICATE_ALBUM);\n"
              + "		}\n"
              + "		user.addToAlbums(album);\n"
              + "		Album createdAlbum = albumRepository.create(album);\n"
              + "		return createdAlbum;\n"
              + "	}\n";

      javaSource.setSourceCode(sourceCode);
      examiner.examine(javaSource);
      assertEquals(2, javaSource.getInjected().size());

      Dependency dependency;
      dependency = javaSource.getInjected().get(0);
      assertEquals(DependencyType.EJB, dependency.getDependencyType());
      assertEquals("AlbumServiceImpl", dependency.getJavaSourceFrom().getName());
      assertEquals("UserRepository", dependency.getJavaSourceTo().getName());

      dependency = javaSource.getInjected().get(1);
      assertEquals(DependencyType.EJB, dependency.getDependencyType());
      assertEquals("AlbumServiceImpl", dependency.getJavaSourceFrom().getName());
      assertEquals("AlbumRepository", dependency.getJavaSourceTo().getName());
   }
}
