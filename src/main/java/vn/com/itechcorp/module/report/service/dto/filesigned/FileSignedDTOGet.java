package vn.com.itechcorp.module.report.service.dto.filesigned;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.DtoGet;
import vn.com.itechcorp.module.report.persitance.FileSigned;

@Getter
@Setter
@NoArgsConstructor
public class FileSignedDTOGet extends DtoGet<FileSigned, Long> {
    private String accessionNumber;

    private String requestNumber;

    private String procedureCode;

    private String signer;

    private String pdfPath;

    private boolean reCall;

    private String tempPath;

    @Override
    public void parse(FileSigned fileSigned) {
        this.setAccessionNumber(fileSigned.getAccessionNumber());
        this.setRequestNumber(fileSigned.getRequestNumber());
        this.setProcedureCode(fileSigned.getProcedureCode());
        this.setSigner(fileSigned.getSigner());
        this.setPdfPath(fileSigned.getPdfPath());
        this.setReCall(fileSigned.isReCall());
        this.setTempPath(fileSigned.getTempPath());
    }

    public FileSignedDTOGet(FileSigned fileSigned) {
        super(fileSigned);
    }
}
