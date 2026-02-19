package com.github.mickeer.codegen.transform;

import com.github.mickeer.codegen.common.GeneratedVisibility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For each class annotated with this annotation, a mapper with class' name
 * postfixed with "FieldMapper" is generated. The abstract class contains
 * an abstract method per field of the annotated class, helping mapping each
 * field with customized logic to the corresponding field in the target class.
 *
 * <p> E.g. for a class {@code MyClass} with a field {@code date}, a class
 * {@code MyClassFieldMapper} will be generated, containing an abstract method
 * {@code setDate(MyClass source, Date sourceFieldValue, Consumer<Date> setter)}
 * which can be implemented in extending class for custom mapping logic.
 *
 * <p> The intention of this is to provide a way to type-safely map each field
 * of a class to another instance of the same class with customized logic. E.g.
 * when creating a return shipment from the original shipment, sender and receiver
 * are swapped, many fields are copied as is, some are set to null (e.g. handling
 * statuses) and some are set to default values (e.g. type = RETURN_SHIPMENT).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateTransformMapper {
    String generatedName() default "";
    GeneratedVisibility visibility() default GeneratedVisibility.PUBLIC;
}
