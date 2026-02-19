package com.github.emick.codegen.test;

import com.github.emick.codegen.common.GeneratedVisibility;
import com.github.emick.codegen.transform.GenerateTransformMapper;
import com.github.emick.codegen.transform.GenerateTransformMapperAnnotationProcessor;
import com.github.emick.codegen.util.FieldGenReflectionUtil;
import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.util.List;

public class GenerateTransformMapperAnnotationProcessorTest {

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
                        GenerateTransformMapper.class.getCanonicalName(),
                        GenerateTransformMapper.class.getSimpleName())
        );

        JavaFileObject output = JavaFileObjects.forSourceString(
                "com.example.AFieldMapper",
                """
                package com.example;

                import java.lang.String;
                import java.util.ArrayDeque;
                import java.util.function.Consumer;

                public abstract class AFieldMapper {
                  private A source;

                  AFieldMapper(A source) {
                    this.source = source;
                  }

                  protected abstract void setMyField(A source, String sourceFieldValue, Consumer<String> setter);
                  protected abstract void setMyField2(A source, ArrayDeque<String> sourceFieldValue, Consumer<ArrayDeque<String>> setter);

                  public void mapAllTo(A target) {
                    setMyField(source, (String)%s.getFieldValue(source, "myField"), value -> %s.setFieldValue(target, "myField", value));
                    setMyField2(source, (ArrayDeque<String>)%s.getFieldValue(source, "myField2"), value -> %s.setFieldValue(target, "myField2", value));
                  }
                }
                """.formatted(REFLECTION_UTIL, REFLECTION_UTIL, REFLECTION_UTIL, REFLECTION_UTIL)
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(List.of(input))
                .processedWith(new GenerateTransformMapperAnnotationProcessor())
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
                        GenerateTransformMapper.class.getCanonicalName(),
                        GenerateTransformMapper.class.getSimpleName())
        );

        JavaFileObject output = JavaFileObjects.forSourceString(
                "com.example.AFieldMapper",
                """
                package com.example;

                import java.lang.String;

                public abstract class AFieldMapper {
                  private A source;

                  AFieldMapper(A source) {
                    this.source = source;
                  }

                  protected abstract String mapName(A source, String sourceFieldValue);
                  protected abstract int mapQuantity(A source, int sourceFieldValue);

                  public A mapAll() {
                    return new A(mapName(source, source.name()), mapQuantity(source, source.quantity()));
                  }
                }
                """
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(List.of(input))
                .processedWith(new GenerateTransformMapperAnnotationProcessor())
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

                @%s(generatedName = "CustomMapper", visibility = GeneratedVisibility.PACKAGE_PRIVATE)
                public class A {
                    String myField;
                }
                """.formatted(
                        GenerateTransformMapper.class.getCanonicalName(),
                        GeneratedVisibility.class.getCanonicalName(),
                        GenerateTransformMapper.class.getSimpleName())
        );

        JavaFileObject output = JavaFileObjects.forSourceString(
                "com.example.CustomMapper",
                """
                package com.example;

                import java.lang.String;
                import java.util.function.Consumer;

                abstract class CustomMapper {
                  private A source;

                  CustomMapper(A source) {
                    this.source = source;
                  }

                  protected abstract void setMyField(A source, String sourceFieldValue, Consumer<String> setter);

                  public void mapAllTo(A target) {
                    setMyField(source, (String)%s.getFieldValue(source, "myField"), value -> %s.setFieldValue(target, "myField", value));
                  }
                }
                """.formatted(REFLECTION_UTIL, REFLECTION_UTIL)
        );

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(List.of(input))
                .processedWith(new GenerateTransformMapperAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(output);
    }
}
