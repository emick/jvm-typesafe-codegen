package com.github.mickeer.codegen.fieldnames;

import com.github.mickeer.codegen.common.AbstractFieldProcessor;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.util.List;

/**
 * Annotation processor for {@link GenerateFieldNames}.
 */
@AutoService(Processor.class)
public class GenerateFieldNamesAnnotationProcessor extends AbstractFieldProcessor {

    public GenerateFieldNamesAnnotationProcessor() {
        super(GenerateFieldNames.class);
    }

    @Override
    protected TypeSpec.Builder process(Element element, List<Element> sourceFields) {
        TypeSpec.Builder fieldsEnumBuilder = TypeSpec.interfaceBuilder(element.getSimpleName() + "Fields")
                .addModifiers(Modifier.PUBLIC);

        sourceFields.forEach(f -> fieldsEnumBuilder.addField(createField(f)));

        return fieldsEnumBuilder;
    }

    private FieldSpec createField(Element f) {
        String fieldName = getMemberName(f);
        return FieldSpec.builder(String.class, fieldName, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", fieldName)
                .build();
    }
}
