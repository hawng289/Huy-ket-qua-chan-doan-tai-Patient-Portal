package vn.com.itechcorp.module.report.service.dto.filesigned;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.SerialIDDtoCreate;
import vn.com.itechcorp.module.report.persitance.FileSigned;

@Getter
@Setter
@NoArgsConstructor
public class FileSignedDTOCreate extends SerialIDDtoCreate<FileSigned> {

    private String accessionNumber;

    private String requestNumber;

    private String procedureCode;

    private String signer;

    private byte[] data;

    @Override
    public FileSigned toEntry() {
        FileSigned fileSigned = new FileSigned();
        fileSigned.setAccessionNumber(this.accessionNumber);
        fileSigned.setRequestNumber(this.requestNumber);
        fileSigned.setProcedureCode(this.procedureCode);
        fileSigned.setSigner(this.signer);
        return fileSigned;
    }
}
