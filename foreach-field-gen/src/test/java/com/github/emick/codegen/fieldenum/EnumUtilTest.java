package com.github.emick.codegen.fieldenum;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnumUtilTest {

    @ParameterizedTest
    @CsvSource(value = {
            "field; FIELD",
            "myField; MY_FIELD",
            "myAPIKey; MY_API_KEY",
    }, delimiter = ';')
    public void shouldConvertFieldNameToEnumName(String input, String result) {
        Assertions.assertEquals(result, EnumUtil.fieldNameToEnumName(input));
    }
}
