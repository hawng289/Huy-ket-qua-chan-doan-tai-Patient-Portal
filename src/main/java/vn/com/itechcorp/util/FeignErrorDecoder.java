package vn.com.itechcorp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import vn.com.itechcorp.base.exception.APIAuthenticationException;
import vn.com.itechcorp.base.exception.APIException;
import vn.com.itechcorp.base.exception.IllegalPropertyException;
import vn.com.itechcorp.base.exception.ObjectNotFoundException;

import java.io.IOException;
import java.io.InputStream;

public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        ExceptionMessage message;
        try (InputStream bodyIs = response.body()
                .asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, ExceptionMessage.class);
            System.out.println(message);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        if (response.status() == HttpStatus.FORBIDDEN.value())
            return new APIAuthenticationException("Access is denied");
        else if (response.status() == HttpStatus.NOT_FOUND.value())
            return new ObjectNotFoundException("Object is not found");
        else if (response.status() == HttpStatus.BAD_REQUEST.value())
            return new IllegalPropertyException("Invalid request properties");

        return new APIException("Exception: " + response.reason());
    }

}

@Getter
@Setter
@NoArgsConstructor
class ExceptionMessage {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}

