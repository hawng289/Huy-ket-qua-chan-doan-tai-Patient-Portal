package vn.com.itechcorp.module.report.service;

import vn.com.itechcorp.base.service.AuditableDtoService;
import vn.com.itechcorp.module.report.persitance.FileSigned;
import vn.com.itechcorp.module.report.service.dto.filesigned.FileSignedDTOGet;

public interface FileSignedService extends AuditableDtoService<FileSignedDTOGet, FileSigned, Long> {
    FileSignedDTOGet getByAccessionNumberRecallFalse(String accessionNumber);

    String copyPdfTempToPath(FileSignedDTOGet fileSigned, String modalityFolder);

    void removeTempFile();
}
