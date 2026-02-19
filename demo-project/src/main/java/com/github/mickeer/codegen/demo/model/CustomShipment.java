package com.github.mickeer.codegen.demo.model;

import com.github.mickeer.codegen.common.GeneratedVisibility;
import com.github.mickeer.codegen.transform.GenerateTransformMapper;

@GenerateTransformMapper(generatedName = "CustomShipmentMapper", visibility = GeneratedVisibility.PACKAGE_PRIVATE)
public class CustomShipment {
    String id;
    String sender;
    String receiver;
    ShipmentState status;
}
