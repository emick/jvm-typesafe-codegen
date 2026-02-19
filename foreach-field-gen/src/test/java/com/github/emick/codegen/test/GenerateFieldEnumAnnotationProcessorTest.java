package com.github.emick.codegen.test;

import com.github.emick.codegen.common.GeneratedVisibility;
import com.github.emick.codegen.fieldenum.GenerateFieldEnum;
import com.github.emick.codegen.fieldenum.GenerateFieldEnumAnnotationProcessor;
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
                        GenerateFieldEnum.class.getCanonicalName(),
                        GenerateFieldEnum.class.getSimpleName())
        );

        JavaFileObject output = JavaFileObjects.forSourceString(
                "com.example.AFields",
                """
                package com.example;

                import java.lang.String;

                public enum AFields {
                  NAME("name"),
                  QUANTITY("quantity");

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

    @Test
    public void shouldProcessWithCustomGeneratedNameAndPackagePrivateVisibility() {
        JavaFileObject input = JavaFileObjects.forSourceString(
                "com.example.A",
                """
                package com.example;

                import %s;
                import %s;

                @%s(generatedName = "CustomFieldEnum", visibility = GeneratedVisibility.PACKAGE_PRIVATE)
                public class A {
                    String myField;
                }
                """.formatted(
                        GenerateFieldEnum.class.getCanonicalName(),
                        GeneratedVisibility.class.getCanonicalName(),
                        GenerateFieldEnum.class.getSimpleName())
        );

        JavaFileObject output = JavaFileObjects.forSourceString(
                "com.example.CustomFieldEnum",
                """
                package com.example;

                import java.lang.String;

                enum CustomFieldEnum {
                  MY_FIELD("myField");

                  private final String fieldName;

                  CustomFieldEnum(String fieldName) {
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
