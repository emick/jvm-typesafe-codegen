package com.github.mickeer.codegen.demo.model;

import com.github.mickeer.codegen.fieldvisitor.GenerateFieldVisitor;
import com.github.mickeer.codegen.transform.GenerateTransformMapper;

@GenerateFieldVisitor
@GenerateTransformMapper
public class Shipment {
    String id;
    int quantity;
}
