package vn.com.itechcorp.module.report.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.com.itechcorp.base.service.filter.PaginationInfo;
import vn.com.itechcorp.his.dto.HisRequest;
import vn.com.itechcorp.his.dto.HisResponse;
import vn.com.itechcorp.his.service.HisService;
import vn.com.itechcorp.module.hl7.service.ReportHl7Service;
import vn.com.itechcorp.module.notification.service.PushNotificationService;
import vn.com.itechcorp.module.notification.service.dto.NotificationDTOCreate;
import vn.com.itechcorp.module.report.constants.ReportType;
import vn.com.itechcorp.module.report.service.FileSignedService;
import vn.com.itechcorp.module.report.service.ReportSentService;
import vn.com.itechcorp.module.report.service.ReportService;
import vn.com.itechcorp.module.report.service.dto.filesigned.FileSignedDTOGet;
import vn.com.itechcorp.module.report.service.dto.filesigned.FileSignedDTOUpdate;
import vn.com.itechcorp.module.report.service.dto.report.ReportDTOGet;
import vn.com.itechcorp.module.report.service.dto.report.ReportDTOUpdate;
import vn.com.itechcorp.module.report.service.dto.report.ReportFilter;
import vn.com.itechcorp.module.report.service.dto.reportsent.ReportSendDTOCreate;
import vn.com.itechcorp.ris.dto.HisReportStatus;
import vn.com.itechcorp.ris.dto.HisReportStatusUpdate;
import vn.com.itechcorp.ris.dto.PatientDTO;
import vn.com.itechcorp.ris.dto.RisResponse;
import vn.com.itechcorp.ris.service.RisService;
import vn.com.itechcorp.util.DateUtil;
import vn.com.itechcorp.util.FolderNameFactory;
import vn.com.itechcorp.util.JsonUtils;
import vn.com.itechcorp.util.TextUtil;

import java.util.Date;
import java.util.List;

@Component("reportSender")
public class ReportSender {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ReportService reportService;

    @Autowired
    private FileSignedService fileSignedService;

    @Autowired
    private ReportHl7Service reportHl7Service;

    @Autowired
    private HisService hisService;

    @Autowired
    private ReportSentService reportSentService;

    @Autowired
    private FolderNameFactory folderNameFactory;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private RisService risService;

    @Value("${max.report.send:20}")
    private int maxReport;

    @Value("${max.retry.send:3}")
    private int maxRetry;

    @Value("${notification.enabled:false}")
    private boolean enableNotificationMobile;

    @Scheduled(fixedDelayString = "${time.delay.send.retry:60000}")
    public void getReport() {
        PaginationInfo paginationInfo = new PaginationInfo(0, maxReport);

        ReportFilter filter = new ReportFilter();
        filter.setReCall(false);
        filter.setHisStatus(false);
        filter.setNumOfRetry(maxRetry);


        List<ReportDTOGet> reports = reportService.getPageOfData(filter, paginationInfo).getElements();
        logger.info("Find {} need commit to his", reports.size());

        if (reports.isEmpty()) return;

        for (ReportDTOGet report : reports) {

            boolean condition = reportSentService.isSentReportToHisSucceed(report.getId());
            if (condition) {
                logger.info("[AccNo-{}] ReportID-{} was send succeed to HIS",report.getAccessionNumber(), report.getId());
                continue;
            }

            // Neu report la xoa
            if (!report.getIsCreate()) {
                try {
                    // Convert to hl7 ORC = CA
                    HisRequest request = reportHl7Service.deleteReport(report.getAccessionNumber(), report.getProcedureCode());
                    if (request == null) {
                        logger.error("[AccNo-{}] Report Id -{} FAILED convert to HL7 ORU message",report.getAccessionNumber(), report.getId());
                        continue;
                    }
                    // Send to HIS
                    HisResponse hisResponse = hisService.deleteReport(request);

                    // Save in table report_sent
                    checkResponse(report, null, hisResponse, request);

                } catch (Exception ex) {
                    logger.error("[AccNo-{}] Report Id -{} FAILED to commit report to HIS",report.getAccessionNumber(), report.getId());
                }
            }
            // Neu la update hay create
            else {
                try {
                    FileSignedDTOGet fileSigned = fileSignedService.getByAccessionNumberRecallFalse(report.getAccessionNumber());
                    // Not have file pdf
                    if (fileSigned == null) {
                        logger.info("[AccNo-{}] ReportID-{} not have file pdf",report.getAccessionNumber(), report.getId());
                        continue;
                    }
                    // Get modalityFolder
                    String folderName = folderNameFactory.get(report.getModality());
                    if (folderName == null) {
                        logger.info("No config folder save for {}", report.getModality());
                        return;
                    }

                    // copy file temp to pdfPath
                    String pdfPath = fileSignedService.copyPdfTempToPath(fileSigned, folderName);
                    fileSigned.setPdfPath(pdfPath);

                    // Check report is sent to his
                    boolean isSentToHis = reportSentService.reportIsSentToHis(report.getAccessionNumber());

                    HisRequest request;
                    if (isSentToHis) {
                        request = reportHl7Service.createReport(report, pdfPath, ReportType.UPDATE);
                    } else {
                        request = reportHl7Service.createReport(report, pdfPath, ReportType.CREATE);
                    }
                    if (request == null) {
                        logger.error("[AccNo-{}] Report Id -{} FAILED convert to HL7 ORU message",report.getAccessionNumber(), report.getId());
                        continue;
                    }
                    // Send to HIS
                    HisResponse hisResponse = hisService.sendReport(request);

                    // Save in table report_sent
                    checkResponse(report, fileSigned, hisResponse, request);
                } catch (Exception ex) {
                    logger.error("[AccNo-{}] Report Id -{} FAILED to commit report to HIS", report.getAccessionNumber() ,report.getId());
                }

            }
        }
    }

    @Scheduled(cron = "${cron.job.clean:0 0 23 * * ?}", zone = "Asia/Saigon")
    public void cleanTempFile() {
        fileSignedService.removeTempFile();
    }

    private void checkResponse(ReportDTOGet report, FileSignedDTOGet fileSigned, HisResponse response, HisRequest request) {
        if (response == null) {
            logger.error("Cannot send report to HIS- Response null");
            return;
        }
        logger.info("[AccNo-{}]ReportID-{} Response from HIS-{}", report.getAccessionNumber(),report.getId(), response);

        // Update report status
        if (response.isStatus()) {
            logger.info("[AccNo-{}]ReportID-{} SUCCEED send report to HIS",report.getAccessionNumber(), report.getId());
            updateReport(report, fileSigned, true, TextUtil.getInstance().toCleanText(response.getErrorMessage()), request);

            // Send API to PACS to update status
            HisReportStatusUpdate object = new HisReportStatusUpdate();
            object.setHisReportStatusTime(DateUtil.HIS_DATE_FORMAT.format(new Date()));
            object.setHisMessage(response.toString());
            object.setHisReportStatus(HisReportStatus.SUCCEEDED);

            RisResponse risResponse = risService.updateHisReportStatus(report.getOrderNumber(), report.getProcedureCode(), object);
            logger.info("[AccNo-{}]ReportID-{} Update his status report to PACS - {}",report.getAccessionNumber(),report.getId(),risResponse);

            // Send notification to mobile app
            if (enableNotificationMobile) {
                if (fileSigned != null) {
                    try {
                        PatientDTO patient = null;
                        patient = JsonUtils.getInstance().jsonToObject(report.getPatient(), PatientDTO.class);
                        if (patient == null) return;
                        boolean isPushed = pushNotificationService.pushNotification(patient.getFullname(), report.getProcedureName(), patient.getPid(), fileSigned.getPdfPath());

                        NotificationDTOCreate dto = new NotificationDTOCreate();
                        dto.setAccessionNumber(report.getAccessionNumber());
                        dto.setPid(patient.getPid());
                        dto.setStatus(isPushed);
                        pushNotificationService.create(dto);
                    } catch (Exception e) {
                        logger.error("Error push notification to mobile app. Err-{}", e.getMessage());
                    }
                }
            }
        } else {
            logger.info("ReportID-{} FAILED send report to HIS", report.getId());
            updateReport(report, fileSigned, false, TextUtil.getInstance().toCleanText(response.getErrorMessage()), request);
        }
    }

    private void updateReport(ReportDTOGet report, FileSignedDTOGet fileSigned, boolean isSucceed, String err, HisRequest request) {
        {
            ReportSendDTOCreate object = new ReportSendDTOCreate();
            object.setAccessionNumber(report.getAccessionNumber());
            object.setReportId(report.getId());
            object.setRequest(request.toString());
            object.setFileId(fileSigned == null ? null : fileSigned.getId());
            object.setHisStatus(isSucceed);
            object.setErrorDetail(err);
            reportSentService.create(object, 0L);
        }

        {
            // Update status report to false
            if (!isSucceed){
                if (report.getNumOfRetries() != null && report.getNumOfRetries() == maxRetry){
                    // Send API to PACS to update status
                    HisReportStatusUpdate object = new HisReportStatusUpdate();
                    object.setHisReportStatusTime(DateUtil.HIS_DATE_FORMAT.format(new Date()));
                    object.setHisMessage(err);
                    object.setHisReportStatus(HisReportStatus.FAILED);

                    RisResponse risResponse = risService.updateHisReportStatus(report.getOrderNumber(), report.getProcedureCode(), object);
                    logger.info("[AccNo-{}]ReportID-{} Update his status report to PACS - {}",report.getAccessionNumber(),report.getId(),risResponse);
                }
            }

            ReportDTOUpdate update = new ReportDTOUpdate();
            update.setId(report.getId());
            update.setHisStatus(isSucceed);
            update.setNumOfRetry(report.getNumOfRetries() == null ? 0 : report.getNumOfRetries() + 1);
            reportService.update(update);
        }
        // Update file signed
        {
            if (fileSigned != null) {
                FileSignedDTOUpdate update = new FileSignedDTOUpdate();
                update.setId(fileSigned.getId());
                update.setReCall(isSucceed);
                fileSignedService.update(update, 0L);
            }
        }
    }
}
