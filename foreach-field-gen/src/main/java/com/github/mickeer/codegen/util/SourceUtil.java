package com.github.mickeer.codegen.util;

import javax.lang.model.element.Name;
import java.util.regex.Pattern;

public class SourceUtil {

    private static final Pattern wordSplit = Pattern.compile("(\\p{javaLowerCase})(\\p{javaUpperCase})");
    private static final Pattern abbreviation = Pattern.compile("(\\p{javaUpperCase}+)(\\p{javaUpperCase})");

    /**
     * Transforms regular Java field name (lower camel case) to enum name (screaming snake case). E.g. {@code myField} to {@code MY_FIELD}
     */
    public static String fieldNameToEnumName(Name fieldName) {
        return fieldNameToEnumName(fieldName.toString());
    }

    // Package private for testing
    static String fieldNameToEnumName(String fieldName) {
        // Add _ after each abbreviation such as XML or IO, e.g. myXMLFormatter
        String first = abbreviation.matcher(fieldName)
                .replaceAll("$1_$2");

        // Add _ before each word, e.g. myField
        String second = wordSplit.matcher(first)
                .replaceAll("$1_$2");

        return second.toUpperCase();
    }
}
