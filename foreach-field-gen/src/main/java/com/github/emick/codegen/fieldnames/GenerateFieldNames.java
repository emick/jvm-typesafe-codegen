package com.github.emick.codegen.fieldnames;

import com.github.emick.codegen.common.GeneratedVisibility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For each class or record annotated with this annotation, an interface with type name
 * postfixed with "Fields" is generated. The generated interface contains String constants
 * where constant names correspond to field/component names and each constant value is the
 * original field/component name.
 *
 * <p> E.g. for class {@code MyClass} with a field {@code date}, an interface
 * {@code MyClassFields} is generated with constant {@code date} and value
 * {@code "date"}.
 *
 * <p> The intention of this is to provide compiler-safe way to refer to field/component
 * names in reflection usage. Whenever a field name changes, this generated interface also
 * changes and anything referring to these generated constants will break and notify developer.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateFieldNames {
    String generatedName() default "";
    GeneratedVisibility visibility() default GeneratedVisibility.PUBLIC;
}
