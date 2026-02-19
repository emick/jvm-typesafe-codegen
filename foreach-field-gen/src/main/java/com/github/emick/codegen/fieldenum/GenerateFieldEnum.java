package com.github.emick.codegen.fieldenum;

import com.github.emick.codegen.common.GeneratedVisibility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For each class or record annotated with this annotation, an enum with type name
 * postfixed with "Fields" is generated. The enum contains one value for each
 * field/component of the annotated type.
 *
 * <p> E.g. for class {@code MyClass} with a field {@code date}, an enum class
 * {@code MyClassFields} is generated with enum value {@code DATE}.
 *
 * <p> The intention of this is to provide way to switch and loop over each field
 * of a class. Combined with IDE compilation settings (or SpotBugs check)
 * to require explicit handling of each enum value in a switch expression, this
 * ensures that each field is handled and if the annotated class is changed in a way
 * such as field is added, removed or renamed, the IDE will signal an error to be fixed.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateFieldEnum {
    String generatedName() default "";
    GeneratedVisibility visibility() default GeneratedVisibility.PUBLIC;
}
