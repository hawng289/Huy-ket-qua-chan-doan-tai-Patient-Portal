package vn.com.itechcorp.module.report.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.base.exception.APIException;
import vn.com.itechcorp.base.service.dto.BaseDtoCreate;
import vn.com.itechcorp.base.service.impl.AuditableDtoJpaServiceImpl;
import vn.com.itechcorp.module.report.persitance.FileSigned;
import vn.com.itechcorp.module.report.persitance.FileSignedRepository;
import vn.com.itechcorp.module.report.service.dto.filesigned.FileSignedDTOCreate;
import vn.com.itechcorp.module.report.service.dto.filesigned.FileSignedDTOGet;
import vn.com.itechcorp.module.report.service.dto.filesigned.FileSignedDTOUpdate;
import vn.com.itechcorp.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service("fileSignedService")
public class FileSignedServiceImpl extends AuditableDtoJpaServiceImpl<FileSignedDTOGet, FileSigned, Long> implements FileSignedService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FileSignedRepository fileSignedRepository;

    @Override
    public FileSignedRepository getRepository() {
        return fileSignedRepository;
    }

    @Override
    public FileSignedDTOGet convert(FileSigned fileSigned) {
        if (fileSigned == null) return null;
        return new FileSignedDTOGet(fileSigned);
    }

    @Value("${setting.signed.pdf.store.temp.path}")
    private String tempPath;

    @Value("${setting.signed.pdf.store.path}")
    private String pdfPath;

    @Value("${setting.signed.pdf.store.toSavePath}")
    private String pdfPathToSave;

    @Override
    protected FileSigned validateAndCreateEntry(BaseDtoCreate<FileSigned, Long> entity) throws APIException {
        FileSignedDTOCreate object = (FileSignedDTOCreate) entity;
        // 1. If have fileSigned before, update they to reCall = true
        List<FileSigned> existFileSigned = this.findEntryByAccessionNumber(object.getAccessionNumber());
        if (!existFileSigned.isEmpty()) {
            // Update status
            existFileSigned.forEach(report -> {
                FileSignedDTOUpdate dtoUpdate = new FileSignedDTOUpdate();
                dtoUpdate.setId(report.getId());
                dtoUpdate.setReCall(true);
                update(dtoUpdate);
            });
        }
        // 2. Write temp file
        String tempFilePath = this.saveSignedTempPDF(object.getData(), object.getAccessionNumber());

        if (tempFilePath == null) {
            throw new APIException("{0} - Can not save pdf file temp", object.getAccessionNumber());
        }
        FileSigned entry = object.toEntry();
        entry.setTempPath(tempFilePath);

        return entry;
    }

    @Override
    public FileSignedDTOGet getByAccessionNumberRecallFalse(String accessionNumber) {
        return convert(getRepository().findByAccessionNumberAndReCallFalse(accessionNumber).orElse(null));
    }

    private List<FileSigned> findEntryByAccessionNumber(String accessionNumber) {
        return getRepository().findAllByAccessionNumber(accessionNumber);
    }

    private int countAllByAccessionNumber(String accessionNumber) {
        return this.getRepository().countAllByAccessionNumber(accessionNumber);
    }

    private String getFileName(String accessionNumber) {
        int filesNo = countAllByAccessionNumber(accessionNumber);
        if (filesNo == 0) return accessionNumber + ".pdf";
        return accessionNumber + "_" + filesNo + ".pdf";
    }

    @Override
    public String copyPdfTempToPath(FileSignedDTOGet fileSigned, String modalityFolder) {
        try {
            String tempFilePath = fileSigned.getTempPath();
            String hisPdfPath = tempFilePath.replace("temporary", modalityFolder);

            FileUtils.createStoredDir(pdfPath, modalityFolder);

            org.apache.commons.io.FileUtils.copyFile(new File(tempPath + "/" + tempFilePath), new File(pdfPath + "/" + hisPdfPath));

            String filePathToSave = pdfPathToSave + "\\" + hisPdfPath.replace("/", "\\");

            logger.info("FileID-{} Copy temp file {} to {}",fileSigned.getId(),tempFilePath,hisPdfPath);

            // Update pdf path in db
            FileSignedDTOUpdate dtoUpdate = new FileSignedDTOUpdate();
            dtoUpdate.setId(fileSigned.getId());
            dtoUpdate.setPdfPath(filePathToSave);
            update(dtoUpdate);

            return filePathToSave;
        } catch (Exception ex) {
            return null;
        }

    }

    @Override
    public void removeTempFile(){
        try {
            // Lay ra danh sach file temp da tao qua 24h
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, -1);
            Date twentyFourHourBack = cal.getTime();

            List<FileSigned> fileTemps = getRepository().findAllByDateCreatedIsLessThan(twentyFourHourBack);
            for (FileSigned fileTemp: fileTemps) {
                File file = new File(tempPath + "/" + fileTemp.getTempPath());
                if (file.exists()){
                    org.apache.commons.io.FileUtils.forceDelete(file);
                }
            }
        } catch (Exception ex){
            logger.error("Error in clean temp file. {}",ex.getMessage());
        }

    }

    private String saveSignedTempPDF(byte[] data, String accessionNumber) {
        try {
            // File path format: /<root>/<Date approve>/temporary/<file-name>.pdf

            String filePath = FileUtils.createStoredDir(tempPath, "temporary") + "/" + getFileName(accessionNumber);

            File signedPDF = new File(tempPath + "/" + filePath);

            try (FileOutputStream fos = new FileOutputStream(signedPDF)) {
                fos.write(data);
            }
            logger.info("AccNo-{} saved temp file in {}",accessionNumber,filePath);
            return filePath;
        } catch (Exception ex) {
            logger.error("[AccNo-{}] FAILED to write temp signed pdf due to: {}", accessionNumber, ex.getMessage());
            return null;
        }
    }
}
