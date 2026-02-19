package com.github.mickeer.codegen.fieldenum;

import java.util.regex.Pattern;

class EnumUtil {

    private static final Pattern wordSplit = Pattern.compile("(\\p{javaLowerCase})(\\p{javaUpperCase})");
    private static final Pattern abbreviation = Pattern.compile("(\\p{javaUpperCase}+)(\\p{javaUpperCase})");

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
