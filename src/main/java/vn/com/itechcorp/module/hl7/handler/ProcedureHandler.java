package vn.com.itechcorp.module.hl7.handler;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v27.group.MFN_M09_MF_TEST_CATEGORICAL;
import ca.uhn.hl7v2.model.v27.message.MFN_M09;
import ca.uhn.hl7v2.model.v27.segment.OM1;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.module.local.service.Hl7MessageService;
import vn.com.itechcorp.ris.dto.ProcedureDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("procedureHandler")
@RequiredArgsConstructor
public class ProcedureHandler extends BaseHandler {
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

    private List<ProcedureDTO> parseMFN_M09(MFN_M09 mfnM09) throws HL7Exception {
        List<ProcedureDTO> procedures = new ArrayList<>();
        List<MFN_M09_MF_TEST_CATEGORICAL> mfTestCategoricalAll = mfnM09.getMF_TEST_CATEGORICALAll();
        for (MFN_M09_MF_TEST_CATEGORICAL mfTest : mfTestCategoricalAll) {
            OM1 om1 = mfTest.getOM1();
            String procedureCode = om1.getOm12_ProducerSServiceTestObservationID().getCwe1_Identifier().getValue();
            String procedureName = om1.getOm12_ProducerSServiceTestObservationID().getCwe2_Text().getValue();
            String modalityType = om1.getOm12_ProducerSServiceTestObservationID().getCwe3_NameOfCodingSystem().getValue();
            String active = om1.getOm14_SpecimenRequired().getValue();
            ProcedureDTO procedure = new ProcedureDTO(procedureCode, procedureName, modalityType, active.equals("Y"));
            procedures.add(procedure);
        }
        return procedures;
    }
}
