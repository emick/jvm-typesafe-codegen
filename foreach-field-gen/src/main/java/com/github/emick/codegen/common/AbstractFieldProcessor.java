package com.github.emick.codegen.common;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstract processor for generating a type (class or enum) from class and its fields.
 */
public abstract class AbstractFieldProcessor extends AbstractProcessor {

    private static final String GENERATED_NAME_PROPERTY = "generatedName";
    private static final String VISIBILITY_PROPERTY = "visibility";

    private final Class<? extends Annotation> annotationClass;

    protected AbstractFieldProcessor(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(annotationClass)
                .forEach(element -> tryProcess(element));
        return true; // Annotation is claimed
    }

    private void tryProcess(Element element) {
        try {
            process(element);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void process(Element element) throws IOException {
        if (!isSupportedSourceKind(element.getKind())) {
            error(element, "Only classes and records may be annotated with @" + annotationClass.getSimpleName());
            return;
        }

        Element packageElement = element.getEnclosingElement();
        if (packageElement.getKind() != ElementKind.PACKAGE) {
            error(element, "Failed to locate package for @" + annotationClass.getSimpleName());
            return;
        }

        List<Element> sourceFields = getSourceMembers(element);

        if (hasFieldNameConflicts(sourceFields)) {
            error(element, "Field names should not differ only by case");
            return;
        }

        var builder = process(element, sourceFields);

        String packageName = ((PackageElement) packageElement).getQualifiedName().toString();
        JavaFile javaFile = JavaFile.builder(packageName, builder.build())
                .build();

        javaFile.writeTo(processingEnv.getFiler());
    }

    private boolean hasFieldNameConflicts(List<Element> sourceFields) {
        var nameSet = sourceFields.stream()
                .map(el -> getMemberName(el).toUpperCase())
                .collect(Collectors.toSet());
        return nameSet.size() != sourceFields.size();
    }

    protected void error(Element element, String s) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, s, element);
    }

    protected String getGeneratedTypeName(Element element, String defaultName) {
        AnnotationValue annotationValue = getAnnotationValue(element, GENERATED_NAME_PROPERTY);
        String generatedName = annotationValue == null ? defaultName : annotationValue.getValue().toString();

        if (generatedName.isBlank()) {
            return defaultName;
        }

        if (!isValidGeneratedTypeName(generatedName)) {
            error(element, "\"" + GENERATED_NAME_PROPERTY + "\" must be a valid simple Java identifier");
            return defaultName;
        }

        return generatedName;
    }

    protected boolean isPublicGeneratedType(Element element) {
        AnnotationValue annotationValue = getAnnotationValue(element, VISIBILITY_PROPERTY);
        if (annotationValue == null) {
            return true;
        }

        Object visibilityValue = annotationValue.getValue();
        if (visibilityValue instanceof VariableElement variableElement) {
            return variableElement.getSimpleName().contentEquals(GeneratedVisibility.PUBLIC.name());
        }

        return true;
    }

    protected static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    protected static String getMemberName(Element element) {
        return element.getSimpleName().toString();
    }

    protected static boolean isRecordComponentMember(Element element) {
        return element.getKind() == ElementKind.RECORD_COMPONENT;
    }

    protected static boolean isRecordMember(Element element) {
        Element enclosing = element.getEnclosingElement();
        return enclosing != null && enclosing.getKind() == ElementKind.RECORD;
    }

    private AnnotationValue getAnnotationValue(Element element, String propertyName) {
        AnnotationMirror annotationMirror = getAnnotationMirror(element);
        if (annotationMirror == null) {
            return null;
        }

        Map<? extends ExecutableElement, ? extends AnnotationValue> values =
                processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror);

        return values.entrySet().stream()
                .filter(entry -> entry.getKey().getSimpleName().contentEquals(propertyName))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private AnnotationMirror getAnnotationMirror(Element element) {
        return element.getAnnotationMirrors().stream()
                .filter(mirror -> {
                    Element annotationElement = mirror.getAnnotationType().asElement();
                    if (!(annotationElement instanceof TypeElement typeElement)) {
                        return false;
                    }
                    return typeElement.getQualifiedName().contentEquals(annotationClass.getCanonicalName());
                })
                .findFirst()
                .orElse(null);
    }

    private static boolean isValidGeneratedTypeName(String generatedName) {
        if (generatedName == null) {
            return false;
        }

        if (generatedName.indexOf('.') >= 0) {
            return false;
        }

        if (!Character.isJavaIdentifierStart(generatedName.charAt(0))) {
            return false;
        }

        for (int i = 1; i < generatedName.length(); i++) {
            if (!Character.isJavaIdentifierPart(generatedName.charAt(i))) {
                return false;
            }
        }

        return !SourceVersion.isKeyword(generatedName);
    }

    private static boolean isSupportedSourceKind(ElementKind kind) {
        return kind == ElementKind.CLASS
                || kind == ElementKind.RECORD;
    }

    private static List<Element> getSourceMembers(Element sourceType) {
        if (sourceType.getKind() == ElementKind.RECORD) {
            return sourceType.getEnclosedElements().stream()
                    .filter(e -> e.getKind() == ElementKind.RECORD_COMPONENT)
                    .collect(Collectors.toList());
        }

        return sourceType.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .filter(e -> !e.getModifiers().contains(Modifier.STATIC))
                .collect(Collectors.toList());
    }

    protected abstract TypeSpec.Builder process(Element element, List<Element> sourceFields);

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    public @Override
    Set<String> getSupportedAnnotationTypes() {
        return Set.of(annotationClass.getCanonicalName());
    }
}
