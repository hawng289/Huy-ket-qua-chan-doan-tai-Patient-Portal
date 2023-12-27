package vn.com.itechcorp.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import vn.com.itechcorp.ris.dto.Dto;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;

public class JsonUtils {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final JsonUtils instance = new JsonUtils();
    private final Gson gson = new Gson();

    public static JsonUtils getInstance() {
        return instance;
    }

    public String toJsonString(Dto dto) {
        if (dto == null) return null;
        return gson.toJson(dto);

    }

    public String toJsonString(List<? extends Dto> list) {
        if (list == null || list.isEmpty()) return null;
        return gson.toJson(list);

    }

    public <C extends Dto> C jsonToObject(String jsonValue, Class<C> clazz) {
        try {
            if (jsonValue == null || jsonValue.trim().length() == 0) return null;
            return gson.fromJson(jsonValue, clazz);
        } catch (Exception ex) {
            return null;
        }
    }

    public <C extends Dto> List<C> jsonToListObject(String jsonValue, Class<C> elementType) {
        try {
            if (jsonValue == null || jsonValue.trim().length() == 0) return null;
            Type listType = TypeToken.getParameterized(List.class, elementType).getType();
            return gson.fromJson(jsonValue, listType);
        } catch (Exception ex) {
            return null;
        }
    }
    public <C> C convertFeignResponse(feign.Response response,@NonNull Class<C> clazz){
        try {
            if (response == null) return null;
            StringWriter writer = new StringWriter();
            IOUtils.copy(response.body().asInputStream(), writer, "UTF-8");
            return objectMapper.readValue(writer.toString(), clazz);
        } catch (IOException ex){
            logger.error("Can not write response body to string. ERR-{}",ex.getMessage());
            return null;
        } catch (Exception ex){
            logger.error("Can not convert feign response to object. ERR-{}",ex.getMessage());
            return null;
        }
        finally {
            if (response != null) response.close();
        }
    }

}

