package com.github.mickeer.codegen.demo;

import com.github.mickeer.codegen.demo.model.Order;
import com.github.mickeer.codegen.demo.model.OrderFields;
import com.github.mickeer.codegen.demo.model.OrderLineFields;

public class Main {
    public static void main(String[] args) {
        // @GenerateFieldEnum produces an enum with the fields as enum values
        var fields = OrderLineFields.values();
        for (OrderLineFields field : fields) {
            String fieldName = switch (field) {
                case PRODUCT_NAME -> "product name";
            };
            System.out.println(fieldName);
        }
    }
}
