package com.github.mickeer.codegen.demo;

import com.github.mickeer.codegen.demo.model.Order;
import com.github.mickeer.codegen.demo.model.OrderFields;
import com.github.mickeer.codegen.demo.model.OrderLineFields;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GenerationTest {

    @Test
    public void shouldGenerateFieldNames() {
        assertEquals("id", OrderFields.id);
        assertEquals("orderDate", OrderFields.orderDate);
    }

    @Test
    public void shouldBeAbleToUseReflection() throws Exception {
        Order order = new Order();

        Field idField = order.getClass().getDeclaredField(OrderFields.id);
        idField.setAccessible(true);
        idField.set(order, "ORDER-123");

        Field orderDateField = order.getClass().getDeclaredField(OrderFields.orderDate);
        orderDateField.setAccessible(true);
        Instant orderDate = Instant.parse("2026-01-01T00:00:00Z");
        orderDateField.set(order, orderDate);

        assertEquals("ORDER-123", idField.get(order));
        assertEquals(orderDate, orderDateField.get(order));
    }

    @Test
    public void shouldGenerateFieldNameEnum() {
        assertEquals(1, OrderLineFields.values().length);
        assertEquals(OrderLineFields.PRODUCT_NAME, OrderLineFields.valueOf("PRODUCT_NAME"));
        assertEquals("PRODUCT_NAME", OrderLineFields.PRODUCT_NAME.name());
        assertEquals("productName", OrderLineFields.PRODUCT_NAME.getFieldName());
    }


}
