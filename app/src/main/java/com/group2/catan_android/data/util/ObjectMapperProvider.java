package com.group2.catan_android.data.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperProvider {
    private ObjectMapperProvider(){}
    private static final ObjectMapper mapper = new ObjectMapper();
    public static ObjectMapper getMapper(){
        return mapper;
    }
}
