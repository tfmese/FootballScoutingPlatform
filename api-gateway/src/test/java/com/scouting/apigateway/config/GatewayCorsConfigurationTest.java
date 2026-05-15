package com.scouting.apigateway.config;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class GatewayCorsConfigurationTest {

    @Test
    void splitPatternsShouldTrimValuesAndDiscardBlankEntries() throws Exception {
        String[] patterns = invokeSplitPatterns(" https://club-a.example  , , http://localhost:* ");

        assertArrayEquals(new String[]{"https://club-a.example", "http://localhost:*"}, patterns);
    }

    @Test
    void splitPatternsShouldFallbackToWildcardWhenEveryTokenIsBlank() throws Exception {
        String[] patterns = invokeSplitPatterns(" ,   , ");

        assertArrayEquals(new String[]{"*"}, patterns);
    }

    private static String[] invokeSplitPatterns(String raw) throws Exception {
        Method method = GatewayCorsConfiguration.class.getDeclaredMethod("splitPatterns", String.class);
        method.setAccessible(true);
        return (String[]) method.invoke(null, raw);
    }
}
