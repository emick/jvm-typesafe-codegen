package com.github.mickeer.codegen.demo.model;

import com.github.mickeer.codegen.fieldnames.GenerateFieldNames;
import com.github.mickeer.codegen.fieldvisitor.GenerateFieldVisitor;

import java.time.Instant;

@GenerateFieldNames
@GenerateFieldVisitor
public class Order {
    String id;
    Instant orderDate;
}
