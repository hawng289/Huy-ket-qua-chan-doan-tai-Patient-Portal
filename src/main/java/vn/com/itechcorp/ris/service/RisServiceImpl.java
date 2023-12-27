package vn.com.itechcorp.ris.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.base.exception.ObjectNotFoundException;
import vn.com.itechcorp.his.dto.HisResponse;
import vn.com.itechcorp.his.dto.HisUpdateStatusRequest;
import vn.com.itechcorp.his.proxy.HisProxy;
import vn.com.itechcorp.module.local.service.RisMessageService;
import vn.com.itechcorp.module.local.service.dto.RISMessageDTOGet;
import vn.com.itechcorp.module.lockorder.service.LockOrderService;
import vn.com.itechcorp.module.lockorder.service.dto.LockOrderDTOCreate;
import vn.com.itechcorp.module.report.service.FileSignedService;
import vn.com.itechcorp.module.report.service.ReportService;
import vn.com.itechcorp.module.report.service.dto.filesigned.FileSignedDTOCreate;
import vn.com.itechcorp.module.report.service.dto.report.ReportDTOCreate;
import vn.com.itechcorp.ris.dto.*;
import vn.com.itechcorp.ris.proxy.RisProxy;
import vn.com.itechcorp.util.JsonUtils;
import vn.com.itechcorp.util.TextUtil;
import vn.com.itechcorp.worklist.dto.WorklistResponse;
import vn.com.itechcorp.worklist.service.WorklistService;

import java.io.StringWriter;

@Service("risService")
@RequiredArgsConstructor
public class RisServiceImpl implements RisService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RisProxy risProxy;

    @Autowired
    private WorklistService worklistService;

    @Value("${viewer.url}")
    private String viewerUrl;

    @Autowired
    private ReportService reportService;

    @Autowired
    private FileSignedService fileSignedService;

    @Autowired
    private RisMessageService risMessageService;

    @Autowired
    private HisProxy hisProxy;

    @Autowired
    private LockOrderService lockOrderService;

    private RisResponse convertResponseToJson(Response response) {
        try {
            if (response == null) {
                logger.error("Cannot send to RIS");
                return null;
            }
            StringWriter writer = new StringWriter();
            IOUtils.copy(response.body().asInputStream(), writer, "UTF-8");
            RisResponse risResponse = objectMapper.readValue(writer.toString(), RisResponse.class);
            logger.info("[RIS] Response-{}", risResponse);
            return risResponse;
        } catch (Exception ex) {
            logger.error("Cannot convert feign response to Json. Err-{}", ex.getMessage());
            return new RisResponse(null, ex.getMessage());
        } finally {
            if (response != null) response.close();
        }
    }

    @Override
    public RisResponse sendOrder(OrderDTO object) {
        try {
            logger.info("[RIS][sendOrder] order {}", object);
            try (Response response = risProxy.sendOrder(object)) {
                return convertResponseToJson(response);
            }
        } catch (Exception ex) {
            logger.error("[AccNo-{}] Cannot create order. Err-{}", object.getAccessionNumber(), ex.getMessage());
            return new RisResponse(null, ex.getMessage());
        }
    }

    @Override
    public RisResponse removeOrder(OrderDTO object) {
        try {
            logger.info("[RIS][removeOrder] AccNo-{}", object.getAccessionNumber());
            try (Response response = risProxy.removeOrder(object.getAccessionNumber())) {
                return convertResponseToJson(response);
            }
        } catch (Exception ex) {
            logger.error("[AccNo-{}] Cannot remove order. Err-{}", object.getAccessionNumber(), ex.getMessage());
            return new RisResponse(null, ex.getMessage());
        }
    }

    @Override
    public RisMWLResponse removeMWL(String accessionNumber) {
        try {
            if (accessionNumber != null && !accessionNumber.trim().isEmpty()) {
                WorklistResponse workListResponse = worklistService.removeWorklist(accessionNumber);
                return new RisMWLResponse(workListResponse.isSucceed(), workListResponse.getData());
            }
            return new RisMWLResponse(false);
        } catch (Exception ex) {
            logger.error("[AccNo-{}] Cannot remove worklist. Err-{}", accessionNumber, ex.getMessage());
            return new RisMWLResponse(false, ex.getMessage());
        }
    }

    @Override
    public RisMWLResponse createReport(ReportDTO object) {
        try {
            logger.info("[AccNo-{}][RisIT] Received Report from RIS: {}", object.getAccessionNumber(), object);
            // 1. Luu lai report trong database
            ReportDTOCreate dtoCreate = object.toReportDTOCreate();

            Long id = this.reportService.create(dtoCreate);

            return new RisMWLResponse(id != null && id > 0);
        } catch (Exception ex) {
            logger.error("[AccNo-{}][Create report] Errors: {}", object.getAccessionNumber(), ex.getMessage());
            return new RisMWLResponse(false, ex.getMessage());
        }
    }

    @Override
    public String getViewerUrl(String accessionNumber) {
        try {
            return TextUtil.getInstance().toCleanText(viewerUrl + risProxy.getViewerURL(accessionNumber));
        } catch (Exception ex) {
            logger.error("[AccNo-{}][ViewerUrl] Errors: {}", accessionNumber, ex.getMessage());
            return "";
        }
    }

    @Override
    public byte[] getSignedPDF(String accessionNumber) throws Exception {
        try {
            ResponseEntity<OrderDTOGet> response = risProxy.getOrder(accessionNumber);
            OrderDTOGet order = response.getBody();
            if (order == null || order.getServices() == null || order.getServices().isEmpty())
                throw new Exception("Cannot find service from accessionNumber " + accessionNumber);
            OrderProcedureDTOGet orderProcedure = order.getServices().get(0);
            if (orderProcedure.getApprovedReportID() == null)
                throw new Exception("Cannot find signed PDF for accessionNumber " + accessionNumber);
            return risProxy.getSignedPDF(orderProcedure.getId()).getBody();
        } catch (Exception ex) {
            logger.error("[AccNo-{}][DataPDF] Errors: {}", accessionNumber, ex.getMessage());
            return null;
        }

    }

    @Override
    public RisResponse createPatient(PatientDTO patient) {
        try {
            logger.info("[RIS][createPatient] patient {}", patient);
            ObjectMapper objectMapper = new ObjectMapper();
            StringWriter writer = new StringWriter();
            try (Response response = risProxy.createPatient(patient);) {
                IOUtils.copy(response.body().asInputStream(), writer, "UTF-8");
            } catch (Exception e) {
                logger.error(e.getMessage());
                return new RisResponse(null, e.getMessage());
            }
            return objectMapper.readValue(writer.toString(), RisResponse.class);
        } catch (Exception ex) {
            logger.error("[PID-{}][CreatePatient] Errors: {}", patient.getPid(), ex.getMessage());
            return new RisResponse(null, ex.getMessage());
        }
    }

    @Override
    public RisResponse updatePatient(PatientDTO patient) {
        try {
            logger.info("[Pid-{}][updatePatient] patient {}", patient.getPid(), patient);
            try (Response response = risProxy.updatePatient(patient)) {
                return convertResponseToJson(response);
            }
        } catch (Exception ex) {
            logger.error("[PID-{}][updatePatient] Errors: {}", patient.getPid(), ex.getMessage());
            return new RisResponse(null, ex.getMessage());
        }
    }

    @Override
    public RisResponse createStudy(StudyDTO study) {
        try {
            logger.info("[RIS][createStudy] study {}", study);
            try (Response response = risProxy.createStudy(study)) {
                return convertResponseToJson(response);
            }
        } catch (Exception ex) {
            logger.error("[AccNo-{}][createStudy] Errors: {}", study.getAccessionNumber(), ex.getMessage());
            return new RisResponse(null, ex.getMessage());
        }
    }

    @Override
    public RisMWLResponse createPdfReport(PdfReportDTO object) {
        try {
            logger.info("[AccNo-{}][RisIT] Received PDF Report from RIS: {}", object.getAccessionNumber(), object);

            FileSignedDTOCreate fileSignedDTO = object.fileSignedDTOCreate();

            Long id = fileSignedService.create(fileSignedDTO, 0L);

            return new RisMWLResponse(id != null && id > 0);
        } catch (Exception ex) {
            logger.error("[AccNo-{}][createPdfReport] Errors: {}", object.getAccessionNumber(), ex.getMessage());
            return new RisMWLResponse(false);
        }
    }

    @Override
    public RisMWLResponse removeReport(String accessionNumber, String procedureCode) {
        try {
            logger.info("[AccNo-{}][removePdfReport] Remove Report from RIS: {}", accessionNumber, procedureCode);

            ReportDTOCreate reportDTODelete = new ReportDTOCreate(accessionNumber, procedureCode);

            Long id = reportService.create(reportDTODelete);

            return new RisMWLResponse(id > 0);
        } catch (Exception ex) {
            logger.error("[AccNo-{}][removePdfReport] Errors: {}", accessionNumber, ex.getMessage());
            return new RisMWLResponse(false);
        }
    }

    @Override
    public RisMWLResponse lockOrder(String orderNumber) {
        //1. Find in table ris_message with key = orderNumber
        RISMessageDTOGet risMessage = risMessageService.getRisMessage(orderNumber);
        if (risMessage == null) {
            logger.error("[OrderNo-{}] This order not sent to PACS",orderNumber);
            throw new ObjectNotFoundException("Order number {0} not send to PACS",orderNumber);
        }
        //2. Send data with accessionNumber + pid to HIS
        HisUpdateStatusRequest request = new HisUpdateStatusRequest();
        request.setSoPhieu(risMessage.getAccessionNumber());
        request.setMaBenhNhan(risMessage.getPid());

        HisResponse hisResponse = null;
        try (Response response = hisProxy.updateStatus(request);){
            hisResponse = JsonUtils.getInstance().convertFeignResponse(response, HisResponse.class);
        }
        //3. Saved log
        LockOrderDTOCreate dtoCreate = new LockOrderDTOCreate();
        dtoCreate.setAccessionNumber(risMessage.getAccessionNumber());
        dtoCreate.setOrderNumber(risMessage.getOrderNumber());
        dtoCreate.setPid(risMessage.getPid());
        dtoCreate.setRequest(request.toString());
        dtoCreate.setResponse(hisResponse == null ? null : hisResponse.toString());
        lockOrderService.create(dtoCreate,0L);

        return new RisMWLResponse(hisResponse != null && hisResponse.isStatus());
    }

    @Override
    public RisResponse updateHisReportStatus(String orderNumber, String procedureCode, HisReportStatusUpdate object){
        logger.info("[OrderNo-{}] ProcedureCode-{} Update status report to {}",orderNumber,procedureCode,object);
        try(Response response = risProxy.updateHisReportStatus(orderNumber, procedureCode, object);){
            return JsonUtils.getInstance().convertFeignResponse(response, RisResponse.class);
        }
    }
}
