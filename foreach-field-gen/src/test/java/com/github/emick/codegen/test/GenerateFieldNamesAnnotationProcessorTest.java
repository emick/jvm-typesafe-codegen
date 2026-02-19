package com.github.emick.codegen.test;

import com.github.emick.codegen.common.GeneratedVisibility;
import com.github.emick.codegen.fieldnames.GenerateFieldNames;
import com.github.emick.codegen.fieldnames.GenerateFieldNamesAnnotationProcessor;
import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.util.List;

public class GenerateFieldNamesAnnotationProcessorTest {

    @Test
    public void shouldProcess() {
        JavaFileObject input = JavaFileObjects.forSourceString(
                "com.example.A",
                """
                package com.example;

                import %s;

                @%s
                public class A {
                    String myField;
                }
                """.formatted(
                        GenerateFieldNames.class.getCanonicalName(),
                        GenerateFieldNames.class.getSimpleName())
        );

        JavaFileObject output = JavaFileObjects.forSourceString(
                "com.example.AFields",
                """
                package com.example;

                import java.lang.String;

                public interface AFields {
                  String myField = "myField";
                }
                """
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(List.of(input))
                .processedWith(new GenerateFieldNamesAnnotationProcessor())
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
                        GenerateFieldNames.class.getCanonicalName(),
                        GenerateFieldNames.class.getSimpleName())
        );

        JavaFileObject output = JavaFileObjects.forSourceString(
                "com.example.AFields",
                """
                package com.example;

                import java.lang.String;

                public interface AFields {
                  String name = "name";

                  String quantity = "quantity";
                }
                """
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(List.of(input))
                .processedWith(new GenerateFieldNamesAnnotationProcessor())
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

                @%s(generatedName = "CustomFieldNames", visibility = GeneratedVisibility.PACKAGE_PRIVATE)
                public class A {
                    String myField;
                }
                """.formatted(
                        GenerateFieldNames.class.getCanonicalName(),
                        GeneratedVisibility.class.getCanonicalName(),
                        GenerateFieldNames.class.getSimpleName())
        );

        JavaFileObject output = JavaFileObjects.forSourceString(
                "com.example.CustomFieldNames",
                """
                package com.example;

                import java.lang.String;

                interface CustomFieldNames {
                  String myField = "myField";
                }
                """
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(List.of(input))
                .processedWith(new GenerateFieldNamesAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(output);
    }
}
