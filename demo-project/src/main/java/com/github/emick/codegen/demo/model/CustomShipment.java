package com.github.emick.codegen.demo.model;

import com.github.emick.codegen.common.GeneratedVisibility;
import com.github.emick.codegen.transform.GenerateTransformMapper;

@GenerateTransformMapper(generatedName = "CustomShipmentMapper", visibility = GeneratedVisibility.PACKAGE_PRIVATE)
public class CustomShipment {
    String id;
    String sender;
    String receiver;
    ShipmentState status;
}
