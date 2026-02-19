package com.github.emick.codegen.demo.model;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneratedRuntimeBehaviorTest {

    @Test
    public void shouldRunGeneratedVisitorAndMapperForClass() {
        Shipment source = new Shipment();
        source.id = "SHP-1";
        source.sender = "Alice";
        source.receiver = "Bob";
        source.status = ShipmentState.DELIVERED;

        ShipmentVisitorProbe visitor = new ShipmentVisitorProbe(source);
        visitor.visitAll();
        assertEquals("SHP-1", visitor.visitedId);
        assertEquals("Alice", visitor.visitedSender);
        assertEquals("Bob", visitor.visitedReceiver);
        assertEquals(ShipmentState.DELIVERED, visitor.visitedStatus);

        Shipment target = new Shipment();
        new ShipmentMapperProbe(source).mapAllTo(target);
        assertEquals("SHP-1", target.id);
        assertEquals("Bob", target.sender);
        assertEquals("Alice", target.receiver);
        assertEquals(ShipmentState.AT_ORIGIN, target.status);

        // Mapper must mutate target only, source should remain unchanged.
        assertEquals("SHP-1", source.id);
        assertEquals("Alice", source.sender);
        assertEquals("Bob", source.receiver);
        assertEquals(ShipmentState.DELIVERED, source.status);
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
        String visitedSender;
        String visitedReceiver;
        ShipmentState visitedStatus;

        private ShipmentVisitorProbe(Shipment instance) {
            super(instance);
        }

        @Override
        protected void visitId(String value) {
            visitedId = value;
        }

        @Override
        protected void visitSender(String value) {
            visitedSender = value;
        }

        @Override
        protected void visitReceiver(String value) {
            visitedReceiver = value;
        }

        @Override
        protected void visitStatus(ShipmentState value) {
            visitedStatus = value;
        }
    }

    private static final class ShipmentMapperProbe extends ShipmentFieldMapper {
        private ShipmentMapperProbe(Shipment source) {
            super(source);
        }

        @Override
        protected void setId(Shipment source, String sourceFieldValue, Consumer<String> setter) {
            setter.accept(sourceFieldValue);
        }

        @Override
        protected void setSender(Shipment source, String sourceFieldValue, Consumer<String> setter) {
            setter.accept(source.receiver);
        }

        @Override
        protected void setReceiver(Shipment source, String sourceFieldValue, Consumer<String> setter) {
            setter.accept(source.sender);
        }

        @Override
        protected void setStatus(Shipment source, ShipmentState sourceFieldValue, Consumer<ShipmentState> setter) {
            setter.accept(ShipmentState.AT_ORIGIN);
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
