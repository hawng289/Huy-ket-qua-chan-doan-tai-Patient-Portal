package vn.com.itechcorp.ris.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public abstract class Dto implements Serializable {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public Dto() {
    }
    public String toString() {
        try {
            String json = OBJECT_MAPPER.writeValueAsString(this);
            return super.getClass().getSimpleName() + " " + json;
        } catch (JsonProcessingException var2) {
            return super.toString();
        }
    }
}