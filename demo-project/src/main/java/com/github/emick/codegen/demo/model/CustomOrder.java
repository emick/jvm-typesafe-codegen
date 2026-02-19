package com.github.emick.codegen.demo.model;

import com.github.emick.codegen.common.GeneratedVisibility;
import com.github.emick.codegen.fieldnames.GenerateFieldNames;
import com.github.emick.codegen.fieldvisitor.GenerateFieldVisitor;

import java.time.Instant;

@GenerateFieldNames(generatedName = "CustomOrderFieldNames", visibility = GeneratedVisibility.PACKAGE_PRIVATE)
@GenerateFieldVisitor(generatedName = "CustomOrderVisitor", visibility = GeneratedVisibility.PACKAGE_PRIVATE)
public class CustomOrder {
    String id;
    Instant createdAt;
}
