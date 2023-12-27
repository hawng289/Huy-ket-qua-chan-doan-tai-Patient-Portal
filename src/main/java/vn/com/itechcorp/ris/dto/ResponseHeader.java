package vn.com.itechcorp.ris.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class ResponseHeader extends Dto {
    @JsonProperty("datetime")
    public Date datetime;

    @JsonProperty("code")
    public int code;

    @JsonProperty("message")
    public String message;

}
