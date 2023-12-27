package vn.com.itechcorp.his.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.ris.dto.Dto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HisResponse extends Dto {

    @JsonProperty("Status")
    private boolean Status;

    @JsonProperty("ErrorCode")
    private String ErrorCode;

    @JsonProperty("ErrorMessage")
    private String ErrorMessage;

    public HisResponse(boolean Status) {
        this.Status = Status;
    }

    public HisResponse(boolean status, String errorMessage) {
        Status = status;
        ErrorMessage = errorMessage;
    }
}
