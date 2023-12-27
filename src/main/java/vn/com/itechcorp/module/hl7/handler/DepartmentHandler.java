package vn.com.itechcorp.module.hl7.handler;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v27.group.MFN_M05_MF_LOCATION;
import ca.uhn.hl7v2.model.v27.message.MFN_M05;
import ca.uhn.hl7v2.model.v27.segment.LOC;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.module.local.service.Hl7MessageService;
import vn.com.itechcorp.ris.dto.DepartmentDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("departmentHandler")
@RequiredArgsConstructor
public class DepartmentHandler extends BaseHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Hl7MessageService hl7Service;

    @Override
    public Hl7MessageService getHl7MessageService() {
        return this.hl7Service;
    }

    @Override
    public Message process(String messageId, Message message, Map<String, Object> map) throws Exception {

        Message ack = message.generateACK();
        getLogger().info("MsgID-{}] Return ACK:\n{}", messageId, ack);
        return ack;
    }

    @Override
    public boolean canProcess(Message message) {
        return false;
    }

    private List<DepartmentDTO> parseMFN_M05(MFN_M05 mfnM05) throws HL7Exception {
        List<DepartmentDTO> departments = new ArrayList<>();
        // parse LOC segment
        List<MFN_M05_MF_LOCATION> mfLocationAll = mfnM05.getMF_LOCATIONAll();
        for (MFN_M05_MF_LOCATION mfLocation : mfLocationAll) {
            LOC loc = mfLocation.getLOC();
            String locationCode = loc.getLoc1_PrimaryKeyValueLOC().getPl1_PointOfCare().getHd1_NamespaceID().getValue();
            String locationName = loc.getLoc1_PrimaryKeyValueLOC().getPl9_LocationDescription().getValue();
            String locationDescription = loc.getLoc2_LocationDescription().getValue();
            String active = loc.getLoc1_PrimaryKeyValueLOC().getPl5_LocationStatus().getValue();
            DepartmentDTO department = new DepartmentDTO(locationCode, locationName, locationDescription, active.equals("Y"));
            departments.add(department);
        }
        return departments;
    }
}
