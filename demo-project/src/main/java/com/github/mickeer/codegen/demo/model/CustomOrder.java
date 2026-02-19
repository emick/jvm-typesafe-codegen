package com.github.mickeer.codegen.demo.model;

import com.github.mickeer.codegen.common.GeneratedVisibility;
import com.github.mickeer.codegen.fieldnames.GenerateFieldNames;
import com.github.mickeer.codegen.fieldvisitor.GenerateFieldVisitor;

import java.time.Instant;

@GenerateFieldNames(generatedName = "CustomOrderFieldNames", visibility = GeneratedVisibility.PACKAGE_PRIVATE)
@GenerateFieldVisitor(generatedName = "CustomOrderVisitor", visibility = GeneratedVisibility.PACKAGE_PRIVATE)
public class CustomOrder {
    String id;
    Instant createdAt;
}
