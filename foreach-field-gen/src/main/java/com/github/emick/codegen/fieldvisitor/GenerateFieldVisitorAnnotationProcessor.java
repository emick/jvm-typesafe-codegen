package com.github.emick.codegen.fieldvisitor;

import com.github.emick.codegen.common.AbstractFieldProcessor;
import com.github.emick.codegen.util.FieldGenReflectionUtil;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.util.List;

/**
 * Annotation processor for {@link GenerateFieldVisitor}.
 */
@AutoService(Processor.class)
public class GenerateFieldVisitorAnnotationProcessor extends AbstractFieldProcessor {

    public GenerateFieldVisitorAnnotationProcessor() {
        super(GenerateFieldVisitor.class);
    }

    @Override
    protected TypeSpec.Builder process(Element element, List<Element> sourceFields) {
        String generatedTypeName = getGeneratedTypeName(element, element.getSimpleName() + "FieldVisitor");
        TypeSpec.Builder fieldVisitorBuilder = TypeSpec.classBuilder(generatedTypeName)
                .addModifiers(Modifier.ABSTRACT);
        if (isPublicGeneratedType(element)) {
            fieldVisitorBuilder.addModifiers(Modifier.PUBLIC);
        }

        var elementType = TypeName.get(element.asType());

        fieldVisitorBuilder.addField(elementType, "instance", Modifier.PRIVATE);
        fieldVisitorBuilder.addMethod(MethodSpec.constructorBuilder()
                .addParameter(ParameterSpec.builder(elementType, "instance").build())
                .addStatement("this.instance = instance")
                .build());

        sourceFields.forEach(f -> fieldVisitorBuilder.addMethod(createFieldVisitMethod(f)));

        fieldVisitorBuilder.addMethod(createVisitAllMethod(sourceFields));

        return fieldVisitorBuilder;
    }


    private static MethodSpec createVisitAllMethod(List<Element> fields) {
        var method = MethodSpec.methodBuilder("visitAll")
                .addModifiers(Modifier.PUBLIC);

        fields.forEach(f -> {
            if (isRecordComponentMember(f) || isRecordMember(f)) {
                method.addStatement("$L($L)",
                        getVisitorMethodName(f),
                        getSourceValueExpression("instance", f));
                return;
            }

            method.addStatement("$L(($T)$L.getFieldValue(instance, $S))",
                    getVisitorMethodName(f),
                    TypeName.get(f.asType()),
                    FieldGenReflectionUtil.class.getCanonicalName(),
                    getMemberName(f));
        });

        return method.build();
    }

    private MethodSpec createFieldVisitMethod(Element f) {
        return MethodSpec.methodBuilder(getVisitorMethodName(f))
                .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
                .addParameter(ParameterSpec.builder(TypeName.get(f.asType()), "value")
                        .build())
                .build();
    }

    private static String getVisitorMethodName(Element element) {
        String fieldName = getMemberName(element);
        String capitalizedName = capitalize(fieldName);
        return "visit" + capitalizedName;
    }

    private static String getSourceValueExpression(String instanceName, Element member) {
        return instanceName + "." + member.getSimpleName() + "()";
    }
}
