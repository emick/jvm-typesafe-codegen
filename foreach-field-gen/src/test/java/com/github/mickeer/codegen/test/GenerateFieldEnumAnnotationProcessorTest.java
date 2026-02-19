package com.github.mickeer.codegen.test;

import com.github.mickeer.codegen.fieldenum.GenerateFieldEnum;
import com.github.mickeer.codegen.fieldenum.GenerateFieldEnumAnnotationProcessor;
import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.util.List;

public class GenerateFieldEnumAnnotationProcessorTest {

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
                        GenerateFieldEnum.class.getCanonicalName(),
                        GenerateFieldEnum.class.getSimpleName())
        );

        JavaFileObject output = JavaFileObjects.forSourceString(
                "com.example.AFields",
                """
                package com.example;

                import java.lang.String;

                public enum AFields {
                  MY_FIELD("myField");

                  private final String fieldName;

                  AFields(String fieldName) {
                    this.fieldName = fieldName;
                  }

                  public String getFieldName() {
                    return fieldName;
                  }
                }
                """
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(List.of(input))
                .processedWith(new GenerateFieldEnumAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(output);
    }
}
