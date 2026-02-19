package com.github.emick.codegen.transform;

import com.github.emick.codegen.common.GeneratedVisibility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For each class or record annotated with this annotation, a mapper with type name
 * postfixed with "FieldMapper" is generated. The abstract class contains
 * an abstract method per field/component of the annotated type, helping map each
 * field/component with customized logic.
 *
 * <p> E.g. for a class {@code MyClass} with a field {@code date}, a class
 * {@code MyClassFieldMapper} will be generated, containing an abstract method
 * {@code setDate(MyClass source, Date sourceFieldValue, Consumer<Date> setter)}
 * which can be implemented in extending class for custom mapping logic.
 *
 * <p> The intention of this is to provide a way to type-safely map each field/component
 * with customized logic. E.g. when creating a return shipment from the original shipment,
 * sender and receiver are swapped, many fields are copied as is, some are set to null
 * (e.g. handling statuses) and some are set to default values (e.g. type = RETURN_SHIPMENT).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateTransformMapper {
    String generatedName() default "";

    GeneratedVisibility visibility() default GeneratedVisibility.PUBLIC;
}
