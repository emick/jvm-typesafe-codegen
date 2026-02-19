package com.github.emick.codegen.demo.model;

import com.github.emick.codegen.fieldvisitor.GenerateFieldVisitor;
import com.github.emick.codegen.transform.GenerateTransformMapper;

@GenerateFieldVisitor
@GenerateTransformMapper
public record ShipmentRecord(String id, int quantity) {
}
