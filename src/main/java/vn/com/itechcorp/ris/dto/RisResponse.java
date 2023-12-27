package vn.com.itechcorp.ris.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class RisResponse extends Dto {
    @JsonProperty("header")
    public ResponseHeader header;

    @JsonProperty("body")
    private Object body;

    @Override
    public String toString() {
        return "RisResponse{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}

