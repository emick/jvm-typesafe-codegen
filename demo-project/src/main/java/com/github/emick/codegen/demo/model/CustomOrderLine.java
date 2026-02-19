package com.github.emick.codegen.demo.model;

import com.github.emick.codegen.common.GeneratedVisibility;
import com.github.emick.codegen.fieldenum.GenerateFieldEnum;

@GenerateFieldEnum(generatedName = "CustomOrderLineColumns", visibility = GeneratedVisibility.PACKAGE_PRIVATE)
public class CustomOrderLine {
    String sku;
    int quantity;
}
