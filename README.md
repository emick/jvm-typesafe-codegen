# jvm-typesafe-codegen

This repository provides tools to generate code to do type-safe "for each object's field" kind of loops. If e.g. a new 
field is added, or a field is removed/renamed, the generated code will signal with compilation error and require manual
action to handle the changed field. This is similar to IDE errors from missing cases in switch-on-enum, but more generic.

The additional code is generated at compile-time using annotation processing. After initial config, no manual compilation
is necessary. The generation should work for all JVM languages (Java, Groovy, Kotlin etc.)

## @GenerateFieldVisitor

Generates a visitor class for type-safe processing of each field in the annotated class. 

### Usage

```java
@GenerateFieldVisitor
public class Order {
    private long id;
    private List<OrderItem> items;
    
    // ...
}
```

generates an `OrderFieldVisitor` parent class which can be extended to customize the processing

```java
public class OrderFieldProcessor extends OrderFieldVisitor {
  OrderFieldProcessor(Order instance) {
    super(instance);
  }

  @Override
  protected void visitId(long value) {
    // process id field
  }

  @Override
  protected void visitItems(List<OrderItem> value) {
    // process order items field
  }
}
```

and can then be used to process all fields of an instance:

```java
new OrderFieldProcessor(order).visitAll();
```

### Real world usage

The main benefit is that a compiler checks that all fields are handled. Thus compilation will break if a field is not handled, e.g. when a field is added or renamed. This can be made to:

  * Ensure that copy constructor handles all fields
  * Separate serialization logic to another class
  * Separate validation logic to another class
  * Ensure that soft-delete also marks child entities as soft-deleted

### Alternatives

  * (Partial:) IntelliJ IDEA checks that copy constructor handles all fields

## @GenerateFieldEnum

Generates an enum containing all fields of the annotated class as enum values. The enum values have `getFieldName()` getter returning the field name.

## @GenerateFieldNames

Generates an interface containing all field names of the annotated class as constant String fields.

### Real world usage

  * Compiler-checked field references when using reflection

### Alternatives

1) Java: No viable alternative without other annotation processors
2) Kotlin natively supports: `SomeClass::someField.name`

## @GenerateTransformMapper

Generates a mapper transforming an instance of a class to a new instance of the same class.

### Real world usage

When a customer returns a shipment, a return shipment may be created from the original shipment 
with some fields staying same, some emptied and some set to a default value. This mapper
allows a compiler-safe way to handle added or changed fields in this kind of mappings.

### Alternatives

A reflection based unit test can be made for this case in such way that the test fails on unknown fields
and has known fields categorized to "stays same", "is nulled" etc. categories.

Usage
-----

The `demo-project` in this repository uses project dependencies and can be built directly from a fresh checkout.

For usage in another Gradle project, this library is not yet published to a public repository. First publish it to Maven local:

1. Clone this repository to your local machine
2. Run `./gradlew clean publishToMavenLocal` to publish the project to Maven local repository

### Usage in a Gradle project 

```groovy
// Import the library from maven local:
repositories {
  mavenLocal()
}

// Include the generated sources:
sourceSets.main.java.srcDir new File(buildDir, 'generated/sources/annotationProcessor')

// Mark the generated sources as "generated" in IntelliJ IDEA
idea {
  module {
    // Marks the already(!) added srcDir as "generated"
    generatedSourceDirs += file('build/generated/sources/annotationProcessor')
  }
}

dependencies {
  // Depend on the annotations and helper code:
  implementation 'com.github.mickeer.codegen:foreach-field-gen:1.0'

  // Apply annotation processor which generates the code:
  annotationProcessor 'com.github.mickeer.codegen:foreach-field-gen:1.0'
}
```

TODO
----

* Add more examples and documentation

Possible future improvements
============================

  * Configurable name and postfix of generated classes
  * Configurable visibility of generated classes
  * FieldVisitor annotation could have options to generate `fieldName` and `fieldType` parameters in visitor methods
  * Investigate GraalVM support
  
Limitations
===========

  * Java 21+ is required
  * Only type members are supported. Inheritance hierarchy traversal is not supported.
  * GraalVM is not tested and most likely not supported for all annotations due to the usage of reflection
  
Links
=====

  * https://github.com/ryandens/auto-delegate - generate base class for proxy/decorator pattern to avoid unnecessary super-calling methods
  * https://github.com/cmelchior/realmfieldnameshelper - Realm extension to create type-safe field references 
