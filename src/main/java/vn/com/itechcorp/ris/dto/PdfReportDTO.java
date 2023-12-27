package vn.com.itechcorp.ris.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.module.report.service.dto.filesigned.FileSignedDTOCreate;
import vn.com.itechcorp.util.JsonUtils;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class PdfReportDTO extends Dto {
    @NotNull
    private String orderNumber;

    @NotNull
    private String accessionNumber;

    private String requestNumber;

    @NotNull
    private String procedureCode;

    @NotNull
    private UserDTO signer;

    private byte[] data;

    @Override
    public String toString() {
        return "PdfReportDTO{" +
                "orderNumber='" + orderNumber + '\'' +
                ", accessionNumber='" + accessionNumber + '\'' +
                ", requestNumber='" + requestNumber + '\'' +
                ", procedureCode='" + procedureCode + '\'' +
                ", signer=" + signer +
                ", data=" + (data == null ? 0 : data.length) +
                '}';
    }

    public FileSignedDTOCreate fileSignedDTOCreate() {
        FileSignedDTOCreate object = new FileSignedDTOCreate();
        object.setAccessionNumber(this.accessionNumber);
        object.setProcedureCode(this.procedureCode);
        object.setRequestNumber(this.requestNumber);
        object.setSigner(JsonUtils.getInstance().toJsonString(this.signer));
        object.setData(this.data);
        return object;
    }
}
