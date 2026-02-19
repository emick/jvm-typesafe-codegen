package com.github.mickeer.codegen.demo.model;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneratedRuntimeBehaviorTest {

    @Test
    public void shouldRunGeneratedVisitorAndMapperForClass() {
        Shipment source = new Shipment();
        source.id = "SHP-1";
        source.quantity = 3;

        ShipmentVisitorProbe visitor = new ShipmentVisitorProbe(source);
        visitor.visitAll();
        assertEquals("SHP-1", visitor.visitedId);
        assertEquals(3, visitor.visitedQuantity);

        Shipment target = new Shipment();
        new ShipmentMapperProbe(source).mapAllTo(target);
        assertEquals("SHP-1-mapped", target.id);
        assertEquals(4, target.quantity);
    }

    @Test
    public void shouldRunGeneratedVisitorAndMapperForRecord() {
        ShipmentRecord source = new ShipmentRecord("REC-1", 4);

        ShipmentRecordVisitorProbe visitor = new ShipmentRecordVisitorProbe(source);
        visitor.visitAll();
        assertEquals("REC-1", visitor.visitedId);
        assertEquals(4, visitor.visitedQuantity);

        ShipmentRecord mapped = new ShipmentRecordMapperProbe(source).mapAll();
        assertEquals("REC-1-mapped", mapped.id());
        assertEquals(5, mapped.quantity());
    }

    private static final class ShipmentVisitorProbe extends ShipmentFieldVisitor {
        String visitedId;
        int visitedQuantity;

        private ShipmentVisitorProbe(Shipment instance) {
            super(instance);
        }

        @Override
        protected void visitId(String value) {
            visitedId = value;
        }

        @Override
        protected void visitQuantity(int value) {
            visitedQuantity = value;
        }
    }

    private static final class ShipmentMapperProbe extends ShipmentFieldMapper {
        private ShipmentMapperProbe(Shipment source) {
            super(source);
        }

        @Override
        protected void setId(Shipment source, String sourceFieldValue, Consumer<String> setter) {
            setter.accept(sourceFieldValue + "-mapped");
        }

        @Override
        protected void setQuantity(Shipment source, int sourceFieldValue, Consumer<Integer> setter) {
            setter.accept(sourceFieldValue + 1);
        }
    }

    private static final class ShipmentRecordVisitorProbe extends ShipmentRecordFieldVisitor {
        String visitedId;
        int visitedQuantity;

        private ShipmentRecordVisitorProbe(ShipmentRecord instance) {
            super(instance);
        }

        @Override
        protected void visitId(String value) {
            visitedId = value;
        }

        @Override
        protected void visitQuantity(int value) {
            visitedQuantity = value;
        }
    }

    private static final class ShipmentRecordMapperProbe extends ShipmentRecordFieldMapper {
        private ShipmentRecordMapperProbe(ShipmentRecord source) {
            super(source);
        }

        @Override
        protected String mapId(ShipmentRecord source, String sourceFieldValue) {
            return sourceFieldValue + "-mapped";
        }

        @Override
        protected int mapQuantity(ShipmentRecord source, int sourceFieldValue) {
            return sourceFieldValue + 1;
        }
    }
}
