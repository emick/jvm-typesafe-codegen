package com.github.mickeer.codegen.test;

import com.github.mickeer.codegen.fieldenum.GenerateFieldEnum;
import com.github.mickeer.codegen.fieldenum.GenerateFieldEnumAnnotationProcessor;
import com.google.common.base.Joiner;
import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.util.List;

public class GenerateFieldEnumAnnotationProcessorTest {

    private static final String NEW_LINE = System.lineSeparator();

    @Test
    public void shouldProcess() {
        JavaFileObject input = JavaFileObjects.forSourceString("com.example.A",
                Joiner.on(NEW_LINE).join(
                        "package com.example;",
                        "",
                        "import " + GenerateFieldEnum.class.getCanonicalName() + ";",
                        "",
                        "@" + GenerateFieldEnum.class.getSimpleName(),
                        "public class A {",
                        "    String myField;",
                        "}"));

        JavaFileObject output = JavaFileObjects.forSourceString("com.example.AFields",
                Joiner.on(NEW_LINE).join(
                        "package com.example;",
                        "",
                        "import java.lang.String;",
                        "",
                        "public enum AFields {",
                        "  MY_FIELD(\"myField\");",
                        "",
                        "  private final String fieldName;",
                        "",
                        "  AFields(String fieldName) {",
                        "    this.fieldName = fieldName;",
                        "  }",
                        "",
                        "  public String getFieldName() {",
                        "    return fieldName;",
                        "  }",
                        "}"));

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(List.of(input))
                .processedWith(new GenerateFieldEnumAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(output);
    }
}
