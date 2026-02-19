package com.github.mickeer.codegen.test;

import com.github.mickeer.codegen.common.GeneratedVisibility;
import com.github.mickeer.codegen.fieldvisitor.GenerateFieldVisitor;
import com.github.mickeer.codegen.fieldvisitor.GenerateFieldVisitorAnnotationProcessor;
import com.github.mickeer.codegen.util.FieldGenReflectionUtil;
import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.util.List;

public class GenerateFieldVisitorAnnotationProcessorTest {

    private static final String REFLECTION_UTIL = FieldGenReflectionUtil.class.getCanonicalName();

    @Test
    public void shouldProcess() {
        JavaFileObject input = JavaFileObjects.forSourceString(
                "com.example.A",
                """
                package com.example;

                import %s;
                import java.util.ArrayDeque;

                @%s
                public class A {
                    String myField;
                    ArrayDeque<String> myField2;
                }
                """.formatted(
                        GenerateFieldVisitor.class.getCanonicalName(),
                        GenerateFieldVisitor.class.getSimpleName())
        );

        JavaFileObject output = JavaFileObjects.forSourceString(
                "com.example.AFieldVisitor",
                """
                package com.example;

                import java.lang.String;
                import java.util.ArrayDeque;

                public abstract class AFieldVisitor {

                  private A instance;

                  AFieldVisitor(A instance) {
                    this.instance = instance;
                  }

                  protected abstract void visitMyField(String value);
                  protected abstract void visitMyField2(ArrayDeque<String> value);

                  public void visitAll() {
                    visitMyField((String)%s.getFieldValue(instance, "myField"));
                    visitMyField2((ArrayDeque<String>)%s.getFieldValue(instance, "myField2"));
                  }
                }
                """.formatted(REFLECTION_UTIL, REFLECTION_UTIL)
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(List.of(input))
                .processedWith(new GenerateFieldVisitorAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(output);
    }

    @Test
    public void shouldProcessRecord() {
        JavaFileObject input = JavaFileObjects.forSourceString(
                "com.example.A",
                """
                package com.example;

                import %s;

                @%s
                public record A(String name, int quantity) {
                }
                """.formatted(
                        GenerateFieldVisitor.class.getCanonicalName(),
                        GenerateFieldVisitor.class.getSimpleName())
        );

        JavaFileObject output = JavaFileObjects.forSourceString(
                "com.example.AFieldVisitor",
                """
                package com.example;

                import java.lang.String;

                public abstract class AFieldVisitor {

                  private A instance;

                  AFieldVisitor(A instance) {
                    this.instance = instance;
                  }

                  protected abstract void visitName(String value);
                  protected abstract void visitQuantity(int value);

                  public void visitAll() {
                    visitName(instance.name());
                    visitQuantity(instance.quantity());
                  }
                }
                """
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(List.of(input))
                .processedWith(new GenerateFieldVisitorAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(output);
    }

    @Test
    public void shouldProcessWithCustomGeneratedNameAndPackagePrivateVisibility() {
        JavaFileObject input = JavaFileObjects.forSourceString(
                "com.example.A",
                """
                package com.example;

                import %s;
                import %s;

                @%s(generatedName = "CustomVisitor", visibility = GeneratedVisibility.PACKAGE_PRIVATE)
                public class A {
                    String myField;
                }
                """.formatted(
                        GenerateFieldVisitor.class.getCanonicalName(),
                        GeneratedVisibility.class.getCanonicalName(),
                        GenerateFieldVisitor.class.getSimpleName())
        );

        JavaFileObject output = JavaFileObjects.forSourceString(
                "com.example.CustomVisitor",
                """
                package com.example;

                import java.lang.String;

                abstract class CustomVisitor {

                  private A instance;

                  CustomVisitor(A instance) {
                    this.instance = instance;
                  }

                  protected abstract void visitMyField(String value);

                  public void visitAll() {
                    visitMyField((String)%s.getFieldValue(instance, "myField"));
                  }
                }
                """.formatted(REFLECTION_UTIL)
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(List.of(input))
                .processedWith(new GenerateFieldVisitorAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(output);
    }
}
