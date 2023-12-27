package vn.com.itechcorp.hsm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.ris.dto.Dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class ITSignDTO extends Dto {

    @NotNull
    private String agreementID;

    private String secret;

    @NotNull
    @NotEmpty
    private byte[] file;

    @Override
    public String toString() {
        return "ITSignDTO{" +
                "agreementID='" + agreementID + '\'' +
                ", secret='" + secret + '\'' +
                ", fileSize=" + file.length +
                '}';
    }
}
