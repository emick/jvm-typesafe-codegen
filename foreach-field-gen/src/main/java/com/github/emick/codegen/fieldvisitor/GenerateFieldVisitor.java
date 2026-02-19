package com.github.emick.codegen.fieldvisitor;

import com.github.emick.codegen.common.GeneratedVisibility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For each class or record annotated with this annotation, an abstract class with name
 * postfixed with "FieldVisitor" is generated. The generated class contains
 * an abstract method per field/component of the annotated type.
 *
 * <p> E.g. for a class {@code MyClass} with a field {@code date}, a class
 * {@code MyClassFieldVisitor} is generated. The generated class contains
 * an abstract method {@code visitDate(Date)} which can be implemented
 * in extending class for customized visiting logic.
 *
 * <p> The intention of this is to provide way to iterate over all fields of a
 * class similar to reflection, but have type-safe access to the field values
 * and remain simpler to use than reflection. Also, any change to the annotated
 * class is automatically reflected in the generated class and thus any changes
 * such as adding, renaming or removing fields will trigger compilation errors,
 * signalling the developer to fix those.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateFieldVisitor {
    String generatedName() default "";
    GeneratedVisibility visibility() default GeneratedVisibility.PUBLIC;
}
