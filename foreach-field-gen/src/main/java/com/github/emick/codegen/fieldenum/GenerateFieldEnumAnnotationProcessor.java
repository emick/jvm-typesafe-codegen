package com.github.emick.codegen.fieldenum;

import com.github.emick.codegen.common.AbstractFieldProcessor;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
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
        String generatedTypeName = getGeneratedTypeName(element, element.getSimpleName() + "Fields");
        TypeSpec.Builder fieldsEnumBuilder = TypeSpec.enumBuilder(generatedTypeName);
        if (isPublicGeneratedType(element)) {
            fieldsEnumBuilder.addModifiers(Modifier.PUBLIC);
        }

        sourceFields.forEach(f -> fieldsEnumBuilder.addEnumConstant(
                EnumUtil.fieldNameToEnumName(f.getSimpleName().toString()),
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
}
