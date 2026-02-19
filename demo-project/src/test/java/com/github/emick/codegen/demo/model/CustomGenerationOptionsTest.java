package com.github.emick.codegen.demo.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CustomGenerationOptionsTest {

    @Test
    public void shouldGenerateCustomFieldNamesWithPackagePrivateVisibility() {
        assertFalse(Modifier.isPublic(CustomOrderFieldNames.class.getModifiers()));
        assertEquals("id", CustomOrderFieldNames.id);
        assertEquals("createdAt", CustomOrderFieldNames.createdAt);
    }

    @Test
    public void shouldGenerateCustomVisitorWithPackagePrivateVisibility() {
        assertFalse(Modifier.isPublic(CustomOrderVisitor.class.getModifiers()));

        CustomOrder source = new CustomOrder();
        source.id = "ORD-9";
        source.createdAt = Instant.parse("2026-02-19T12:00:00Z");

        CustomOrderVisitorProbe probe = new CustomOrderVisitorProbe(source);
        probe.visitAll();

        assertEquals("ORD-9", probe.visitedId);
        assertEquals(Instant.parse("2026-02-19T12:00:00Z"), probe.visitedCreatedAt);
    }

    @Test
    public void shouldGenerateCustomEnumWithPackagePrivateVisibility() {
        assertFalse(Modifier.isPublic(CustomOrderLineColumns.class.getModifiers()));

        assertEquals("sku", CustomOrderLineColumns.SKU.getFieldName());
        assertEquals("quantity", CustomOrderLineColumns.QUANTITY.getFieldName());
    }

    @Test
    public void shouldGenerateCustomMapperWithPackagePrivateVisibility() {
        assertFalse(Modifier.isPublic(CustomShipmentMapper.class.getModifiers()));

        CustomShipment source = new CustomShipment();
        source.id = "SHP-900";
        source.sender = "Sender A";
        source.receiver = "Receiver B";
        source.status = ShipmentState.DELIVERED;

        CustomShipment target = new CustomShipment();
        new CustomShipmentMapperProbe(source).mapAllTo(target);

        assertEquals("SHP-900", target.id);
        assertEquals("Receiver B", target.sender);
        assertEquals("Sender A", target.receiver);
        assertEquals(ShipmentState.AT_ORIGIN, target.status);
    }

    private static final class CustomOrderVisitorProbe extends CustomOrderVisitor {
        private String visitedId;
        private Instant visitedCreatedAt;

        private CustomOrderVisitorProbe(CustomOrder instance) {
            super(instance);
        }

        @Override
        protected void visitId(String value) {
            visitedId = value;
        }

        @Override
        protected void visitCreatedAt(Instant value) {
            visitedCreatedAt = value;
        }
    }

    private static final class CustomShipmentMapperProbe extends CustomShipmentMapper {
        private CustomShipmentMapperProbe(CustomShipment source) {
            super(source);
        }

        @Override
        protected void setId(CustomShipment source, String sourceFieldValue, Consumer<String> setter) {
            setter.accept(sourceFieldValue);
        }

        @Override
        protected void setSender(CustomShipment source, String sourceFieldValue, Consumer<String> setter) {
            setter.accept(source.receiver);
        }

        @Override
        protected void setReceiver(CustomShipment source, String sourceFieldValue, Consumer<String> setter) {
            setter.accept(source.sender);
        }

        @Override
        protected void setStatus(CustomShipment source, ShipmentState sourceFieldValue, Consumer<ShipmentState> setter) {
            setter.accept(ShipmentState.AT_ORIGIN);
        }
    }
}
