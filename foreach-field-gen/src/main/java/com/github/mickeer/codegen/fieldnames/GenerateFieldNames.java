package com.github.mickeer.codegen.fieldnames;

import com.github.mickeer.codegen.common.GeneratedVisibility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For each class annotated with this annotation, a class with class' name
 * postfixed with "FieldNames" is generated. The generated class contains fields
 * which names correspond to the fields of the annotated class and each field's
 * value is the name of the field.
 *
 * <p> E.g. for class {@code MyClass} with a field {@code date}, a class
 * {@code MyClassFieldNames} is generated with field {@code date} with value
 * {@code "date"}.
 *
 * <p> The intention of this is to provide compiler-safe way to refer to fields of
 * a class for reflection usage. Whenever field's name changes, this generated
 * class also changes and anything referring to these generated fields will break
 * and notify developer.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateFieldNames {
    String generatedName() default "";
    GeneratedVisibility visibility() default GeneratedVisibility.PUBLIC;
}
