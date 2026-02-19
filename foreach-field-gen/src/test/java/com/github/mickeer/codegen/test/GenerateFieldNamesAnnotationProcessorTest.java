package com.github.mickeer.codegen.test;

import com.github.mickeer.codegen.fieldnames.GenerateFieldNames;
import com.github.mickeer.codegen.fieldnames.GenerateFieldNamesAnnotationProcessor;
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
}
