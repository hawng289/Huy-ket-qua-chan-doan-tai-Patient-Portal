package vn.com.itechcorp.worklist.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.json.JSONReader;
import org.dcm4che3.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.util.TextUtil;
import vn.com.itechcorp.worklist.dto.MwlItemFilter;
import vn.com.itechcorp.worklist.dto.WorklistDTO;
import vn.com.itechcorp.worklist.dto.WorklistResponse;
import vn.com.itechcorp.worklist.proxy.WorklistProxy;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("worklistService")
@RequiredArgsConstructor
public class WorklistServiceImpl implements WorklistService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WorklistProxy worklistProxy;

    @Value("${worklist.server.url}")
    private String WORKLIST_SERVER_URL;

    @Value("${worklist.dcm4chee.enableb:false}")
    private boolean dcm4cheeEnable;


    private Map<String, Object> convertDicomTag(Attributes attributes) throws JsonProcessingException {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = Json.createGenerator(writer);
        new JSONWriter(gen).write(attributes);
        gen.flush();
        return new ObjectMapper().readValue(writer.toString(), HashMap.class);
    }

    private boolean isPatientExisted(URI uri, String pid) throws Exception {
        Map<String, Object> filter = new HashMap<>();
        filter.put("PatientID", pid);
        filter.put("offset", 0);
        filter.put("limit", 1);

        logger.info("[PATIENT-{}] Finding Patient item in worklist server", pid);
        Response response = worklistProxy.getPatients(uri, filter);
        logger.info("[PATIENT-{}] Received status from worklist server: (status-{}, body-{})", pid, response.status(), response.body() != null);
        if (response.status() != HttpStatus.OK.value() || response.body() == null) return false;

        List<Attributes> attributes = parseJSON(response.body().asInputStream());
        return !attributes.isEmpty();
    }

    @Override
    public synchronized WorklistResponse sendWorklist(WorklistDTO worklist) {
        try {
            URI uri = new URI(WORKLIST_SERVER_URL);
            // WL is exist => return response wl true
            if (getWorkList(new MwlItemFilter(worklist.getAccessionNumber())) != null)
                return new WorklistResponse(true);

            if (dcm4cheeEnable) {
                if (!isPatientExisted(uri, worklist.getPid())) {
                    Attributes patientItem = new Attributes();
                    patientItem.setValue(Tag.PatientName, VR.PN, worklist.getPatientName());
                    patientItem.setValue(Tag.PatientBirthDate, VR.DA, worklist.getPatientBirthDate());
                    patientItem.setValue(Tag.PatientID, VR.LO, worklist.getPid());
                    patientItem.setValue(Tag.PatientSex, VR.CS, worklist.getPatientGender());
                    patientItem.setValue(Tag.IssuerOfPatientID, VR.LO, "ABCD"); // Hard-coded

                    logger.info("[AccNo-{}] Sending patient item to worklist server (PatientID-{}, PatientName-{}).", worklist.getAccessionNumber(),
                            worklist.getPid(), worklist.getPatientName());
                    feign.Response response = worklistProxy.createPatient(uri, convertDicomTag(patientItem));
                    if (response.status() != HttpStatus.OK.value()) {
                        logger.info("[AccNo-{}] Received status from worklist server: {}", worklist.getAccessionNumber(), response.status());
                        return new WorklistResponse(false);
                    }
                }
            }

            Attributes spssAttrs = new Attributes(); // scheduledProcedureStepSequence
            spssAttrs.setValue(Tag.Modality, VR.CS, worklist.getModalityType());
            spssAttrs.setValue(Tag.ScheduledProcedureStepStartDate, VR.DA, worklist.getOrderDate());
            spssAttrs.setValue(Tag.ScheduledProcedureStepStartTime, VR.TM, worklist.getOrderTime());
            spssAttrs.setValue(Tag.ScheduledProcedureStepID, VR.SH, worklist.getOrderNumber());
            spssAttrs.setValue(Tag.ScheduledProcedureStepDescription, VR.LO, worklist.getRequestedProcedureDescription());
            spssAttrs.setValue(Tag.ScheduledProcedureStepStatus, VR.SH, worklist.getProcedureStepStatus());

            Attributes mwlItem = new Attributes();
            mwlItem.newSequence(Tag.ScheduledProcedureStepSequence, 1).add(spssAttrs);
            mwlItem.setValue(Tag.StudyInstanceUID, VR.UI, worklist.getStudyInstanceUID());
            mwlItem.setValue(Tag.PatientID, VR.LO, worklist.getPid());
            mwlItem.setValue(Tag.AccessionNumber, VR.SH, worklist.getOrderNumber());
            mwlItem.setValue(Tag.RequestedProcedureID, VR.SH, worklist.getRequestedProcedureID());
            mwlItem.setValue(Tag.RequestedProcedureDescription, VR.LO, worklist.getRequestedProcedureDescription());
            mwlItem.setValue(Tag.ReferringPhysicianName, VR.PN, worklist.getReferDoctorName());

            if (!dcm4cheeEnable) {
                mwlItem.setValue(Tag.PatientName, VR.PN, worklist.getPatientName());
                mwlItem.setValue(Tag.PatientBirthDate, VR.DA, worklist.getPatientBirthDate());
                mwlItem.setValue(Tag.PatientSex, VR.CS, worklist.getPatientGender());
                mwlItem.setValue(Tag.IssuerOfPatientID, VR.LO, "ABCD"); // Hard-coded
            }

            if (worklist.getRequestDepartmentName() != null)
                mwlItem.setValue(Tag.RequestingService, VR.LO, worklist.getRequestDepartmentName());

            logger.info("[AccNo-{}] Sending worklist item to worklist server (ScheduledProcedureStepID-{},StudyInstanceUID-{}).", worklist.getAccessionNumber(),
                    worklist.getOrderNumber(), worklist.getStudyInstanceUID());
            try (Response response = worklistProxy.createWorklist(uri, convertDicomTag(mwlItem))) {
                if (response.status() != HttpStatus.OK.value()) {
                    logger.info("[AccNo-{}] Received status from worklist server: {}", worklist.getAccessionNumber(), response.status());
                    return new WorklistResponse(false, response.reason());
                }
            }
            return new WorklistResponse(true);
        } catch (Exception ex) {
            logger.error("[AccNo-{}] has exception {}", worklist.getOrderNumber(), ex.getMessage());
            return new WorklistResponse(false, ex.getMessage());
        }
    }

    private List<Attributes> parseJSON(InputStream in) {
        JSONReader jsonReader = new JSONReader(Json.createParser(new InputStreamReader(in, StandardCharsets.UTF_8)));
        List<Attributes> result = new ArrayList<>();
        jsonReader.readDatasets((fmi, attrs) -> {
            result.add(attrs);
        });
        return result;
    }

    @Override
    public WorklistResponse removeWorklist(String accessionNumber) {
        try {
            URI uri = new URI(WORKLIST_SERVER_URL);
            List<Attributes> mwlAttributes = getWorkList(new MwlItemFilter(accessionNumber));
            if (mwlAttributes == null) return new WorklistResponse(true);

            for (Attributes mwl : mwlAttributes) {
                String study = mwl.getString(Tag.StudyInstanceUID);
                Sequence sequence = mwl.getSequence(Tag.ScheduledProcedureStepSequence);
                Attributes firstAttributes = sequence.iterator().next();
                String spsID = firstAttributes.getString(Tag.ScheduledProcedureStepID);
                worklistProxy.deleteWorklist(uri, study, spsID);
                logger.info("[AccNo-{}] Removed MWL item (ScheduledProcedureStepID-{}, StudyInstanceUID-{})", accessionNumber, spsID, study);
            }
            return new WorklistResponse(true);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new WorklistResponse(false, ex.getMessage());
        }
    }

    @Override
    public String searchWorkList(MwlItemFilter mwlItemFilter) {
        try {
            Response response = getAttributesWorklist(mwlItemFilter);
            if (response == null) {
                return TextUtil.getInstance().toCleanText(null);
            }
            String json = IOUtils.toString(response.body().asInputStream());
            logger.info("[MWL] Worklist response -\n{}", json);
            return json;
        } catch (Exception ex) {
            logger.error("[MWL] ERROR search worklist. Err-{}", ex.getMessage());
            return TextUtil.getInstance().toCleanText(null);
        }
    }

    @Override
    public List<Attributes> getWorkList(MwlItemFilter mwlItemFilter) {
        try (Response response = getAttributesWorklist(mwlItemFilter)) {
            if (response == null) {
                return null;
            }
            List<Attributes> attributes = parseJSON(response.body().asInputStream());
            logger.info("[MWL] Worklist response Attrs-\n{}", attributes);
            return attributes.isEmpty() ? null : attributes;
        } catch (Exception ex) {
            logger.error("[MWL] ERROR get worklist. Err-{}", ex.getMessage());
            return null;
        }
    }

    private feign.Response getAttributesWorklist(MwlItemFilter mwlItemFilter) {
        Map<String, Object> filter = new HashMap<>();

        if (mwlItemFilter.getAccessionNumber() != null)
            filter.put("accessionNumber", mwlItemFilter.getAccessionNumber());
        if (mwlItemFilter.getPatientID() != null) filter.put("patientID", mwlItemFilter.getPatientID());
        if (mwlItemFilter.getPatientName() != null) filter.put("patientName", mwlItemFilter.getPatientName());
        if (mwlItemFilter.getStartDate() != null) filter.put("startDate", mwlItemFilter.getStartDate());
        if (mwlItemFilter.getEndDate() != null) filter.put("endDate", mwlItemFilter.getEndDate());
        if (mwlItemFilter.getModality() != null) filter.put("modality", mwlItemFilter.getModality());
        if (mwlItemFilter.getScheduledStationAETitle() != null)
            filter.put("scheduledStationAETitle", mwlItemFilter.getScheduledStationAETitle());
        if (mwlItemFilter.getStatus() != null) filter.put("status", mwlItemFilter.getStatus());

        logger.info("Finding mwl item with properties-{}", filter);
        try {
            URI uri = new URI(WORKLIST_SERVER_URL);
            try (feign.Response response = worklistProxy.getWorklists(uri, filter)) {
                logger.info("[MWL] Received status from worklist server: (status-{}, body-{})", response.status(), response.body() != null);
                if (response.status() != HttpStatus.OK.value() || response.body() == null) return null;
                return response;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

}
