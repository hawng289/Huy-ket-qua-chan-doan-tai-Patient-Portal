package vn.com.itechcorp.his.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.ris.dto.Dto;

import javax.validation.constraints.NotNull;

@Getter @Setter @NoArgsConstructor
public class HisUpdateStatusRequest extends Dto {
    @NotNull
    private String SoPhieu;

    @NotNull
    private String MaBenhNhan;
}
