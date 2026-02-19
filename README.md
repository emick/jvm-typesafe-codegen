# jvm-typesafe-codegen

This repository provides tools to generate code to do type-safe "for each object's field" kind of loops. If e.g. a new 
field is added, or a field is removed/renamed, the generated code will signal with compilation error and require manual
action to handle the changed field. This is similar to IDE errors from missing cases in switch-on-enum, but more generic.

The additional code is generated at compile-time using annotation processing. After initial config, no manual compilation
is necessary. The generation should work for all JVM languages (Java, Groovy, Kotlin etc.)

## Annotation Summary

* [`@GenerateFieldVisitor`](#generatefieldvisitor): generates an abstract visitor for type-safe processing of all fields/components.
* [`@GenerateFieldEnum`](#generatefieldenum): generates an enum containing all field/component names with `getFieldName()`.
* [`@GenerateFieldNames`](#generatefieldnames): generates an interface with string constants for all field/component names.
* [`@GenerateTransformMapper`](#generatetransformmapper): generates an abstract mapper for field-by-field transformation.

## @GenerateFieldVisitor

Generates a visitor class for type-safe processing of each field/component in the annotated type.

### Options

| Option | Type | Default | Description |
| --- | --- | --- | --- |
| `generatedName` | `String` | `""` | Custom name of the generated type. Empty value uses default `<TypeName>FieldVisitor`. |
| `visibility` | `GeneratedVisibility` | `PUBLIC` | Visibility of the generated type. |

### Usage

```java
@GenerateFieldVisitor
public class Order {
    private long id;
    private List<OrderItem> items;
    
    // ...
}
```

Generates an `OrderFieldVisitor` abstract base class that can be extended to customize processing.

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

It can then be used to process all fields of an instance:

```java
new OrderFieldProcessor(order).visitAll();
```

### Real world usage

The main benefit is that the compiler checks that all fields/components are handled. Compilation breaks when the model changes and a new field/component is not handled yet.

* Ensure that a copy constructor handles all fields
* Ensure that soft-delete also marks child entities as soft-deleted
* Separate serialization logic to another class
* Separate validation logic to another class
* PII redaction before logging, where each sensitive field must be explicitly handled.
* Audit trail generation where every field/component must be classified.

### Alternatives

* (Partial) IntelliJ IDEA checks that copy constructor handles all fields

## @GenerateFieldEnum

Generates an enum containing all fields/components of the annotated type as enum values. The enum values have `getFieldName()` getter returning the original field/component name.

### Options

| Option | Type | Default | Description |
| --- | --- | --- | --- |
| `generatedName` | `String` | `""` | Custom name of the generated type. Empty value uses default `<TypeName>Fields`. |
| `visibility` | `GeneratedVisibility` | `PUBLIC` | Visibility of the generated type. |

### Usage

```java
@GenerateFieldEnum
public class OrderLine {
    String productName;
    int quantity;
}
```

Generates enum values and a field-name getter:

```java
assertEquals("productName", OrderLineFields.PRODUCT_NAME.getFieldName());
```

### Real world usage

* UI table column configuration (show/hide/sort) with exhaustive `switch` over generated enum constants.
* API sort/filter allowlists where new fields/components must be explicitly approved.
* Export mapping (CSV/JSON) where each field/component gets a required mapping rule.

## @GenerateFieldNames

Generates an interface containing all field/component names of the annotated type as constant String fields.

### Options

| Option | Type | Default | Description |
| --- | --- | --- | --- |
| `generatedName` | `String` | `""` | Custom name of the generated type. Empty value uses default `<TypeName>Fields`. |
| `visibility` | `GeneratedVisibility` | `PUBLIC` | Visibility of the generated type. |

### Usage

```java
@GenerateFieldNames
public class Order {
    String id;
}
```

Generated constants can be used for reflection safely:

```java
Field idField = Order.class.getDeclaredField(OrderFields.id);
idField.setAccessible(true);
idField.set(order, "ORDER-1");
```

### Real world usage

* Reflection-based patch/update handling without string literals.
* Dynamic query or filter builders with compiler-safe field/component names.
* Field-level authorization maps keyed by generated constants.

### Alternatives

1. Java: No viable alternative without other annotation processors
2. Lombok `@FieldNameConstants` can generate field name constants.
3. Kotlin natively supports: `SomeClass::someField.name`

## @GenerateTransformMapper

Generates a mapper for type-safe field-by-field transformation.

### Options

| Option | Type | Default | Description |
| --- | --- | --- | --- |
| `generatedName` | `String` | `""` | Custom name of the generated type. Empty value uses default `<TypeName>FieldMapper`. |
| `visibility` | `GeneratedVisibility` | `PUBLIC` | Visibility of the generated type. |

### Behavior

* For classes, generated `mapAllTo(target)` maps values into the provided target instance.
* For records, generated `mapAll()` creates and returns a new record instance.

### Usage

For classes:

```java
@GenerateTransformMapper
public class Shipment {
    String id;
    String sender;
    String receiver;
    ShipmentState status;
}

public class ReturnShipmentMapper extends ShipmentFieldMapper {
    public ReturnShipmentMapper(Shipment source) {
        super(source);
    }

    @Override
    protected void setId(Shipment source, String sourceFieldValue, Consumer<String> setter) {
        setter.accept(sourceFieldValue);
    }

    @Override
    protected void setSender(Shipment source, String sourceFieldValue, Consumer<String> setter) {
        setter.accept(source.receiver);
    }

    @Override
    protected void setReceiver(Shipment source, String sourceFieldValue, Consumer<String> setter) {
        setter.accept(source.sender);
    }

    @Override
    protected void setStatus(Shipment source, ShipmentState sourceFieldValue, Consumer<ShipmentState> setter) {
        setter.accept(ShipmentState.AT_ORIGIN);
    }
}

// Usage
Shipment target = new Shipment();
new ReturnShipmentMapper(source).mapAllTo(target);
```

For records:

```java
@GenerateTransformMapper
public record ShipmentRecord(String id, int quantity) {
}

public class ShipmentRecordMapper extends ShipmentRecordFieldMapper {
    public ShipmentRecordMapper(ShipmentRecord source) {
        super(source);
    }

    @Override
    protected String mapId(ShipmentRecord source, String sourceFieldValue) {
        return sourceFieldValue + "-mapped";
    }

    @Override
    protected int mapQuantity(ShipmentRecord source, int sourceFieldValue) {
        return sourceFieldValue + 1;
    }
}

ShipmentRecord mapped = new ShipmentRecordMapper(source).mapAll();
```

### Real world usage

* Return shipment creation, where sender/receiver are swapped and status is reset.
* DTO-to-entity update flows with per-field normalization/conversion rules.
* Model version migration (`v1 -> v2`) where added/renamed fields/components break compilation until mapped.

### Alternatives

1. MapStruct provides compile-time generated mappers with explicit field mapping support.
2. A reflection-based unit test can be made for this case in such a way that the test fails on unknown fields
   and has known fields categorized to "stays same", "is nulled", etc. categories.

## Setup

For usage in another Gradle project, this library is not yet published to a public repository. First, publish it to Maven local:

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
  implementation 'com.github.emick.codegen:foreach-field-gen:1.0'

  // Apply annotation processor which generates the code:
  annotationProcessor 'com.github.emick.codegen:foreach-field-gen:1.0'
}
```

## Possible Future Improvements

* FieldVisitor annotation could have options to generate `fieldName` and `fieldType` parameters in visitor methods
* Investigate GraalVM native build support

## Limitations

* Java 21+ is required
* Only type members are supported. Inheritance hierarchy traversal is not supported.
* GraalVM native build is not tested and most likely not supported for all annotations due to the usage of reflection

## Links

* https://github.com/ryandens/auto-delegate - generate base class for proxy/decorator pattern to avoid unnecessary super-calling methods
* https://github.com/cmelchior/realmfieldnameshelper - Realm extension to create type-safe field references
