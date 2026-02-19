package com.github.emick.codegen.transform;

import com.github.emick.codegen.common.AbstractFieldProcessor;
import com.github.emick.codegen.util.FieldGenReflectionUtil;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Annotation processor for {@link GenerateTransformMapper}.
 */
@AutoService(Processor.class)
public class GenerateTransformMapperAnnotationProcessor extends AbstractFieldProcessor {

    public GenerateTransformMapperAnnotationProcessor() {
        super(GenerateTransformMapper.class);
    }

    @Override
    protected TypeSpec.Builder process(Element element, List<Element> sourceFields) {
        String generatedTypeName = getGeneratedTypeName(element, element.getSimpleName() + "FieldMapper");
        TypeSpec.Builder mapperBuilder = TypeSpec.classBuilder(generatedTypeName)
                .addModifiers(Modifier.ABSTRACT);
        if (isPublicGeneratedType(element)) {
            mapperBuilder.addModifiers(Modifier.PUBLIC);
        }

        var elementType = TypeName.get(element.asType());

        mapperBuilder.addField(elementType, "source", Modifier.PRIVATE);
        mapperBuilder.addMethod(MethodSpec.constructorBuilder()
                .addParameter(ParameterSpec.builder(elementType, "source").build())
                .addStatement("this.source = source")
                .build());

        if (element.getKind() == ElementKind.RECORD) {
            sourceFields.forEach(f -> mapperBuilder.addMethod(createRecordFieldMappingMethod(elementType, f)));
            mapperBuilder.addMethod(createRecordMapAllMethod(elementType, sourceFields));
        } else {
            sourceFields.forEach(f -> mapperBuilder.addMethod(createFieldMappingMethod(elementType, f)));
            mapperBuilder.addMethod(createMapAllToMethod(elementType, sourceFields));
        }

        return mapperBuilder;
    }

    private static MethodSpec createMapAllToMethod(TypeName elementType, List<Element> sourceFields) {
        var method = MethodSpec.methodBuilder("mapAllTo")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(elementType, "target").build());

        sourceFields.forEach(f -> {
            if (isRecordComponentMember(f) || isRecordMember(f)) {
                method.addStatement("$L(source, $L, value -> $L)",
                        getSettingMethodName(f),
                        getSourceValueExpression("source", f),
                        getTargetSettingExpression("target", f));
                return;
            }

            method.addStatement("$L(source, ($T)$L.getFieldValue(source, $S), value -> $L.setFieldValue(target, $S, value))",
                    getSettingMethodName(f),
                    TypeName.get(f.asType()),
                    FieldGenReflectionUtil.class.getCanonicalName(),
                    getMemberName(f),
                    FieldGenReflectionUtil.class.getCanonicalName(),
                    getMemberName(f));
        });

        return method.build();

    }

    private static MethodSpec createRecordMapAllMethod(TypeName elementType, List<Element> sourceFields) {
        var method = MethodSpec.methodBuilder("mapAll")
                .addModifiers(Modifier.PUBLIC)
                .returns(elementType);

        String constructorArgs = sourceFields.stream()
                .map(f -> getRecordMappingMethodName(f) + "(source, " + getSourceValueExpression("source", f) + ")")
                .collect(Collectors.joining(", "));

        method.addStatement("return new $T($L)", elementType, constructorArgs);
        return method.build();
    }

    private MethodSpec createFieldMappingMethod(TypeName elementType, Element field) {
        TypeName memberType = TypeName.get(field.asType());
        ParameterizedTypeName setterType = ParameterizedTypeName.get(ClassName.get(Consumer.class), memberType.box());

        return MethodSpec.methodBuilder(getSettingMethodName(field))
                .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
                .addParameter(elementType, "source")
                .addParameter(ParameterSpec.builder(memberType, "sourceFieldValue")
                        .build())
                .addParameter(ParameterSpec.builder(setterType, "setter").build())
                .build();
    }

    private MethodSpec createRecordFieldMappingMethod(TypeName elementType, Element field) {
        TypeName memberType = TypeName.get(field.asType());

        return MethodSpec.methodBuilder(getRecordMappingMethodName(field))
                .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
                .returns(memberType)
                .addParameter(elementType, "source")
                .addParameter(ParameterSpec.builder(memberType, "sourceFieldValue")
                        .build())
                .build();
    }

    private static String getSettingMethodName(Element element) {
        String fieldName = getMemberName(element);
        String capitalizedName = capitalize(fieldName);
        return "set" + capitalizedName;
    }

    private static String getRecordMappingMethodName(Element element) {
        String fieldName = getMemberName(element);
        String capitalizedName = capitalize(fieldName);
        return "map" + capitalizedName;
    }

    private static String getSourceValueExpression(String sourceName, Element member) {
        return sourceName + "." + member.getSimpleName() + "()";
    }

    private static String getTargetSettingExpression(String targetName, Element member) {
        return FieldGenReflectionUtil.class.getCanonicalName()
                + ".setFieldValue(" + targetName + ", \"" + getMemberName(member) + "\", value)";
    }
}
