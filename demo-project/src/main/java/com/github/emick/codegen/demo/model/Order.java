package com.github.emick.codegen.demo.model;

import com.github.emick.codegen.fieldnames.GenerateFieldNames;
import com.github.emick.codegen.fieldvisitor.GenerateFieldVisitor;

import java.time.Instant;

@GenerateFieldNames
@GenerateFieldVisitor
public class Order {
    String id;
    Instant orderDate;
}
