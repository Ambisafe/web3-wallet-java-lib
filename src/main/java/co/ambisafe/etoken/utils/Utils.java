package co.ambisafe.etoken.utils;

import co.ambisafe.etoken.exception.ETokenException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Utils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String writeObjectAsString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ETokenException(e);
        }
    }

    public static JsonNode readTree(String body) {
        try {
            return objectMapper.readTree(body);
        } catch (IOException e) {
            throw new ETokenException(e);
        }
    }

    public static String check0x(String param) {
        if (!param.startsWith("0x")) param = "0x" + param;
        return param;
    }
}
