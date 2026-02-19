package com.github.mickeer.codegen.fieldenum;

import com.github.mickeer.codegen.common.AbstractFieldProcessor;
import com.github.mickeer.codegen.util.SourceUtil;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import java.util.List;

/**
 * Annotation processor for {@link GenerateFieldEnum}.
 */
@AutoService(Processor.class)
public class GenerateFieldEnumAnnotationProcessor extends AbstractFieldProcessor {

    public GenerateFieldEnumAnnotationProcessor() {
        super(GenerateFieldEnum.class);
    }

    @Override
    protected TypeSpec.Builder process(Element element, List<Element> sourceFields) {
        TypeSpec.Builder fieldsEnumBuilder = TypeSpec.enumBuilder(element.getSimpleName() + "Fields")
                .addModifiers(Modifier.PUBLIC);

        sourceFields.forEach(f -> fieldsEnumBuilder.addEnumConstant(
                SourceUtil.fieldNameToEnumName(asName(getMemberName(f))),
                TypeSpec.anonymousClassBuilder("$S", getMemberName(f)).build()
        ));

        fieldsEnumBuilder.addField(FieldSpec.builder(String.class, "fieldName", Modifier.PRIVATE, Modifier.FINAL).build());
        fieldsEnumBuilder.addMethod(MethodSpec.constructorBuilder()
                .addParameter(String.class, "fieldName")
                .addStatement("this.fieldName = fieldName")
                .build());
        fieldsEnumBuilder.addMethod(MethodSpec.methodBuilder("getFieldName")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return fieldName")
                .build());

        return fieldsEnumBuilder;
    }

    private static Name asName(String value) {
        return new Name() {
            @Override
            public boolean contentEquals(CharSequence cs) {
                return value.contentEquals(cs);
            }

            @Override
            public int length() {
                return value.length();
            }

            @Override
            public char charAt(int index) {
                return value.charAt(index);
            }

            @Override
            public CharSequence subSequence(int start, int end) {
                return value.subSequence(start, end);
            }

            @Override
            public String toString() {
                return value;
            }
        };
    }

}
