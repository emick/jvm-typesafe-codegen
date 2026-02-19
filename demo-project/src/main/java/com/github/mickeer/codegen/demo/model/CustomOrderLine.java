package com.github.mickeer.codegen.demo.model;

import com.github.mickeer.codegen.common.GeneratedVisibility;
import com.github.mickeer.codegen.fieldenum.GenerateFieldEnum;

@GenerateFieldEnum(generatedName = "CustomOrderLineColumns", visibility = GeneratedVisibility.PACKAGE_PRIVATE)
public class CustomOrderLine {
    String sku;
    int quantity;
}
