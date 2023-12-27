package vn.com.itechcorp.module.report.service.dto.filesigned;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.DtoUpdate;
import vn.com.itechcorp.module.report.persitance.FileSigned;

@Getter
@Setter
@NoArgsConstructor
public class FileSignedDTOUpdate extends DtoUpdate<FileSigned, Long> {

    private Boolean reCall;

    private String pdfPath;

    @Override
    public boolean apply(FileSigned fileSigned) {
        boolean modified = false;
        if (reCall != null && reCall != fileSigned.isReCall()) {
            fileSigned.setReCall(reCall);
            modified = true;
        }
        if (pdfPath != null) {
            fileSigned.setPdfPath(pdfPath);
            modified = true;
        }
        return modified;
    }
}
