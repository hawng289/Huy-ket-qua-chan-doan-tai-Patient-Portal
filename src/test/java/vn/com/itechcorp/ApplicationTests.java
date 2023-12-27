package vn.com.itechcorp;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.hoh.api.DecodeException;
import ca.uhn.hl7v2.hoh.api.EncodeException;
import ca.uhn.hl7v2.hoh.api.IReceivable;
import ca.uhn.hl7v2.hoh.api.MessageMetadataKeys;
import ca.uhn.hl7v2.hoh.hapi.client.HohClientSimple;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v27.datatype.ST;
import ca.uhn.hl7v2.model.v27.message.OMI_O23;
import ca.uhn.hl7v2.model.v27.message.ORU_R01;
import ca.uhn.hl7v2.model.v27.segment.MSH;
import ca.uhn.hl7v2.model.v27.segment.OBR;
import ca.uhn.hl7v2.model.v27.segment.OBX;
import ca.uhn.hl7v2.model.v27.segment.PID;
import ca.uhn.hl7v2.protocol.ApplicationRouter;
import ca.uhn.hl7v2.protocol.Transportable;
import ca.uhn.hl7v2.protocol.impl.AppRoutingDataImpl;
import ca.uhn.hl7v2.protocol.impl.ApplicationRouterImpl;
import ca.uhn.hl7v2.protocol.impl.TransportableImpl;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vn.com.itechcorp.module.hl7.handler.PatientHandler;
import vn.com.itechcorp.module.hl7.handler.OrderHandler;
import vn.com.itechcorp.module.hl7.service.ReportHl7Service;
import vn.com.itechcorp.hsm.dto.ITSignDTO;
import vn.com.itechcorp.hsm.service.SignService;
import vn.com.itechcorp.module.local.service.Hl7MessageService;
import vn.com.itechcorp.module.local.service.RisMessageService;
import vn.com.itechcorp.module.local.service.WorkListMessageService;
import vn.com.itechcorp.ris.dto.*;
import vn.com.itechcorp.ris.service.RisService;
import vn.com.itechcorp.util.Base64Util;
import vn.com.itechcorp.util.HtmlUtil;
import vn.com.itechcorp.util.Util;
import vn.com.itechcorp.worklist.dto.WorklistResponse;
import vn.com.itechcorp.worklist.service.WorklistService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ApplicationTests {

    @Autowired
    private ReportHl7Service reportHl7Service;

    @Test
    void contextLoads() {
    }

    private final String bodyHTML = "<table style=\"border-collapse: collapse; width: 100%; border-color: #000000; border-style: solid;\" border=\"1px\"><tbody><tr><td style=\"text-align: center; width: 8.45295%;\"><span style=\"font-size: 11pt;\">Nhĩ tr&aacute;i</span></td><td style=\"text-align: center; width: 7.41627%;\"><span style=\"font-size: 11pt;\">ĐMC</span></td><td style=\"text-align: center; width: 47.5067%;\" colspan=\"6\"><span style=\"font-size: 11pt;\">Thất tr&aacute;i</span></td><td style=\"text-align: center; width: 7.41627%;\"><span style=\"font-size: 11pt;\">ĐTP</span></td><td style=\"text-align: center; width: 15.2599%;\" colspan=\"2\"><span style=\"font-size: 11pt;\">Bề d&agrave;y VLT</span></td><td style=\"text-align: center; width: 13.7522%;\" colspan=\"2\"><span style=\"font-size: 11pt;\">Bề d&agrave;y TSTT</span></td></tr><tr><td style=\"text-align: center; width: 8.45295%;\"><span style=\"font-size: 11pt;\">31 &plusmn; 4</span><br /><span style=\"font-size: 11pt;\">mm</span></td><td style=\"text-align: center; width: 7.41627%;\"><span style=\"font-size: 11pt;\">28 &plusmn; 3</span><br /><span style=\"font-size: 11pt;\">mm</span></td><td style=\"text-align: center; width: 7.39509%;\"><span style=\"font-size: 11pt;\">Dd</span><br /><span style=\"font-size: 11pt;\">46 &plusmn; 4</span><br /><span style=\"font-size: 11pt;\">mm</span></td><td style=\"text-align: center; width: 6.85805%;\"><span style=\"font-size: 11pt;\">Ds&nbsp;</span><br /><span style=\"font-size: 11pt;\">30&plusmn; 3</span><br /><span style=\"font-size: 11pt;\">mm</span></td><td style=\"text-align: center; width: 10.0478%;\"><span style=\"font-size: 11pt;\">Vd</span><br /><span style=\"font-size: 11pt;\">101 &plusmn; 17&nbsp;</span><br /><span style=\"font-size: 11pt;\">mm</span></td><td style=\"text-align: center; width: 8.45295%;\"><span style=\"font-size: 11pt;\">Vs</span><br /><span style=\"font-size: 11pt;\">37 &plusmn; 17</span><br /><span style=\"font-size: 11pt;\">mm</span></td><td style=\"text-align: center; width: 7.41627%;\"><span style=\"font-size: 11pt;\">Fs&nbsp;</span><br /><span style=\"font-size: 11pt;\">34 &plusmn; 6</span><br /><span style=\"font-size: 11pt;\">%</span></td><td style=\"text-align: center; width: 7.33652%;\"><span style=\"font-size: 11pt;\">EF</span><br /><span style=\"font-size: 11pt;\">63 &plusmn; 7</span><br /><span style=\"font-size: 11pt;\">%</span></td><td style=\"text-align: center; width: 7.41627%;\"><span style=\"font-size: 11pt;\">16 &plusmn; 4</span><br /><span style=\"font-size: 11pt;\">mm</span></td><td style=\"text-align: center; width: 7.9234%;\"><span style=\"font-size: 11pt;\">t.trg</span><br /><span style=\"font-size: 11pt;\">7.5 &plusmn; 1</span><br /><span style=\"font-size: 11pt;\">mm</span></td><td style=\"text-align: center; width: 7.33652%;\"><span style=\"font-size: 11pt;\">t.thu</span><br /><span style=\"font-size: 11pt;\">10 &plusmn; 2</span><br /><span style=\"font-size: 11pt;\">mm</span></td><td style=\"text-align: center; width: 6.33597%;\"><span style=\"font-size: 11pt;\">t.trg</span><br /><span style=\"font-size: 11pt;\">7 &plusmn; 1</span><br /><span style=\"font-size: 11pt;\">mm</span></td><td style=\"text-align: center; width: 7.41627%;\"><span style=\"font-size: 11pt;\">t.thu</span><br /><span style=\"font-size: 11pt;\">12 &plusmn; 1</span><br /><span style=\"font-size: 11pt;\">mm</span></td></tr><tr><td style=\"width: 8.45295%;\">&nbsp;</td><td style=\"width: 7.41627%;\">&nbsp;</td><td style=\"width: 7.39509%;\">&nbsp;</td><td style=\"width: 6.85805%;\">&nbsp;</td><td style=\"width: 10.0478%;\">&nbsp;</td><td style=\"width: 8.45295%;\">&nbsp;</td><td style=\"width: 7.41627%;\">&nbsp;</td><td style=\"width: 7.33652%;\">&nbsp;</td><td style=\"width: 7.41627%;\">&nbsp;</td><td style=\"width: 7.9234%;\">&nbsp;</td><td style=\"width: 7.33652%;\">&nbsp;</td><td style=\"width: 6.33597%;\">&nbsp;</td><td style=\"width: 7.41627%;\">&nbsp;</td></tr></tbody></table><table style=\"width: 100%; border-color: #000000;\"><tbody><tr><td style=\"width: 48.9496%; text-align: left; vertical-align: top;\"><div><span style=\"text-decoration: underline; font-size: 11pt;\"><strong>1. VAN HAI L&Aacute;:</strong></span></div><div><span style=\"font-size: 11pt;\">- Dạng di động: <strong>Ngược chiều</strong></span></div><div><span style=\"font-size: 11pt;\"><strong>-&nbsp;</strong>K.c&aacute;ch 2 bờ van:&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;mm</span></div><div><span style=\"font-size: 11pt;\">- T.trạng van: <strong>Thanh mảnh&nbsp;</strong></span></div><div><span style=\"font-size: 11pt;\">- D&acirc;y chằng:&nbsp;</span></div><div><span style=\"font-size: 11pt;\">- M&eacute;p van: </span></div></td><td style=\"width: 50.9378%; text-align: left;\"><div><span style=\"font-size: 11pt;\"><strong>Doppler <em>(Nhĩ - Thất tr&aacute;i)</em></strong></span></div><div><span style=\"font-size: 11pt;\">- Gradient: Tối đa:&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;mmHg</span></div><div><span style=\"font-size: 11pt;\">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Trung b&igrave;nh:&nbsp; &nbsp; &nbsp; &nbsp;mmHg</span></div><div><span style=\"font-size: 11pt;\">- Hở van hại l&aacute;: <strong>Ko</strong></span></div><div><span style=\"font-size: 11pt;\">- S HoHL - tr&ecirc;n trục dọc:&nbsp; &nbsp; &nbsp; cm&sup2;</span></div><div><span style=\"font-size: 11pt;\">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; - tr&ecirc;n 4 buồng:&nbsp; &nbsp; &nbsp; &nbsp;cm&sup2;</span></div><div><span style=\"font-size: 11pt;\">D.t&iacute;ch lỗ van:&nbsp; &nbsp; &nbsp; cm&sup2;(2D)&nbsp; &nbsp; &nbsp; cm&sup2;(PHT)</span></div></td></tr><tr><td style=\"width: 48.9496%; text-align: left; vertical-align: top;\"><div><span style=\"font-size: 11pt;\"><strong><span style=\"text-decoration: underline;\">2. VAN ĐỘNG MẠCH CHỦ:&nbsp;</span></strong></span></div><div><span style=\"font-size: 11pt;\">- T.trạng van: <strong>Thanh mảnh</strong></span></div><div><span style=\"font-size: 11pt;\">- Bi&ecirc;n độ mở van:&nbsp; &nbsp; &nbsp; &nbsp;mm</span></div><div><span style=\"font-size: 11pt;\">- ĐK ĐMC xuống ngang mức cơ ho&agrave;nh: mm</span></div><div><span style=\"font-size: 11pt;\">- ĐK ĐMC l&ecirc;n:&nbsp; &nbsp; &nbsp; mm</span></div></td><td style=\"width: 50.9378%; text-align: left;\"><div><div style=\"font-size: 17.3333px;\"><span style=\"font-weight: bolder; font-size: 11pt;\">Doppler&nbsp;<em>(Thất tr&aacute;i - ĐMC)</em></span></div><div style=\"font-size: 17.3333px;\"><span style=\"font-size: 11pt;\">- Gradient: Tối đa:&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;mmHg</span></div><div style=\"font-size: 17.3333px;\"><span style=\"font-size: 11pt;\">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Trung b&igrave;nh:&nbsp; &nbsp; &nbsp; &nbsp;mmHg</span></div><div style=\"font-size: 17.3333px;\"><span style=\"font-size: 11pt;\">- Hở van ĐMC:&nbsp;<span style=\"font-weight: bolder;\">Ko&nbsp;</span>(PHT: ms)</span></div><div style=\"font-size: 17.3333px;\"><span style=\"font-size: 11pt;\">- ĐKHoC/ĐRTT =&nbsp; &nbsp; &nbsp;mm/&nbsp; mm</span></div><div style=\"font-size: 17.3333px;\"><span style=\"font-size: 11pt;\">- Chiều d&agrave;i d&ograve;ng hở chủ:&nbsp; &nbsp; &nbsp;mm</span></div></div></td></tr><tr><td style=\"width: 48.9496%; text-align: left;\"><div><span style=\"font-size: 11pt;\"><strong><span style=\"text-decoration: underline;\">3. VAN ĐỘNG MẠCH PHỔI:&nbsp;</span></strong></span></div><div><span style=\"font-size: 11pt;\">- T.trạng van:&nbsp;<span style=\"font-weight: bolder;\">Thanh mảnh</span></span></div><div><span style=\"font-size: 11pt;\">- ĐK van ĐMP:&nbsp; &nbsp; &nbsp; mm; Th&acirc;n:&nbsp; &nbsp; &nbsp; mm</span></div><div><span style=\"font-size: 11pt;\">&nbsp; Nh&aacute;nh tr&aacute;i:&nbsp; &nbsp; &nbsp; mm; Nh&aacute;nh phải:&nbsp; &nbsp; &nbsp;mm</span></div><div><span style=\"font-size: 11pt;\">- &Aacute;p lực ĐMP(ước t&iacute;nh): t&acirc;m thu&nbsp; &nbsp; &nbsp; &nbsp;mmHg</span></div></td><td style=\"width: 50.9378%; text-align: left;\"><div><div style=\"font-size: 17.3333px;\"><span style=\"font-weight: bolder; font-size: 11pt;\">Doppler&nbsp;<em>(Thất phải - ĐMP)</em></span></div><div style=\"font-size: 17.3333px;\"><span style=\"font-size: 11pt;\">- Gradient: Tối đa:&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;mmHg</span></div><div style=\"font-size: 17.3333px;\"><span style=\"font-size: 11pt;\">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Trung b&igrave;nh:&nbsp; &nbsp; &nbsp; &nbsp;mmHg</span></div><div style=\"font-size: 17.3333px;\"><span style=\"font-size: 11pt;\">- Hở van ĐMP: <span style=\"font-weight: bolder;\">Ko&nbsp;</span></span></div><div style=\"font-size: 17.3333px;\"><span style=\"font-size: 11pt;\">- Cuối t&acirc;m trương:&nbsp; &nbsp; &nbsp; mmHg - TB:&nbsp; &nbsp; mmHg</span></div></div></td></tr><tr><td style=\"width: 48.9496%; text-align: left;\"><div><span style=\"text-decoration: underline; font-size: 11pt;\"><strong>4. VAN BA L&Aacute;:&nbsp;</strong></span></div><div><span style=\"font-size: 11pt;\">- T.trạng van: <strong>Thanh mảnh</strong></span></div><div><span style=\"font-size: 11pt;\">- TAPSE thất phải:&nbsp; &nbsp; &nbsp; &nbsp;mm</span></div></td><td style=\"width: 50.9378%; text-align: left;\"><div><span style=\"font-size: 11pt;\"><strong>Doppler <em>(Nhĩ - Thất phải)</em></strong></span></div><div><span style=\"font-size: 11pt;\">- Hở van ba l&aacute;: <strong>Ko</strong></span></div><div><span style=\"font-size: 11pt;\">- Gradient tối đa:&nbsp; &nbsp; &nbsp;mmHg(D&ograve;ng hở ba l&aacute;)</span></div></td></tr><tr><td style=\"text-align: left; width: 99.8874%;\" colspan=\"2\"><div><span style=\"font-size: 11pt;\"><strong>5. M&agrave;ng ngo&agrave;i tim, MP 2 b&ecirc;n: Kh&ocirc;ng c&oacute; dịch</strong></span></div></td></tr><tr><td style=\"text-align: left; width: 99.8874%;\" colspan=\"2\"><div><span style=\"font-size: 11pt;\"><strong>6. Nhận x&eacute;t kh&aacute;c: Tim co b&oacute;p được, kh&ocirc;ng rối loạn vận động v&ugrave;ng, ĐK quai ĐMC 19mm, PG qua eo ĐMC 4.0 mmHg</strong></span></div></td></tr></tbody></table>";
    private final String conclusionHTML = "<div>H&igrave;nh ảnh chụp Xquang ngực hiện tại b&igrave;nh thường</div>";


    private ReportDTO sampleReport() {
        ModalityDTO modality = new ModalityDTO();
        modality.setCode("XQ01");
        modality.setModalityType("DX");

        UserDTO creator = new UserDTO();
        creator.setCode("sys.quynhbtn");
        creator.setName("Bùi Thị Như Quỳnh");

        UserDTO approver = new UserDTO();
        approver.setCode("sys.quynhbtn");
        approver.setName("Bùi Thị Như Quỳnh");

        PatientDTO patient = new PatientDTO();
        patient.setPid("2100034166");
        patient.setFullname("NGUYỄN VĂN A");
        patient.setGender("M");
        patient.setBirthDate("20110101");
        patient.setPhone("3");
        patient.setAddress("2 ,Huyện Nam Đàn,Nghệ An");

        ReportDTO report = new ReportDTO();
        report.setId(100L);
        report.setOrderNumber("79963");
        report.setAccessionNumber("79963");
        report.setOrderDatetime("20220526111300");
        report.setRequestNumber("79963");
        report.setProcedureCode("XQ.1147");

        report.setProcedureName("Chụp Xquang khớp khuỷu thẳng, nghiêng hoặc chếch");
        report.setBodyHTML("Dong mot \\.br\\" +
                "Dong 2 \\.br\\" +
                "Dong 3 \\.br\\");
        report.setConclusionHTML("");
        report.setCreatedDatetime("20220526111300");
        report.setApprovedDatetime("20220526111300");

        report.setPatient(patient);
        report.setModality(modality);
        report.setCreator(creator);
        report.setApprover(approver);

        return report;
    }

    private MSH createMSH(MSH msh, ReportDTO report) throws HL7Exception {
        msh.getMsh1_FieldSeparator().setValue("|");
        msh.getMsh2_EncodingCharacters().setValue("^~\\&");
        msh.getMsh3_SendingApplication().getHd1_NamespaceID().setValue("PACS");
        msh.getMsh4_SendingFacility().getHd1_NamespaceID().setValue("0");
        msh.getMsh5_ReceivingApplication().getHd1_NamespaceID().setValue("HIS");
        String createdTime = Util.yyyyMMddHHmmss.get().format(new Date());
        msh.getMsh7_DateTimeOfMessage().setValue(createdTime);
        msh.getMsh9_MessageType().getMsg1_MessageCode().setValue("ORU");
        msh.getMsh9_MessageType().getMsg2_TriggerEvent().setValue("R01");
        msh.getMsh10_MessageControlID().setValue(report.getAccessionNumber() + "-" + createdTime);
        msh.getMsh11_ProcessingID().getPt1_ProcessingID().setValue("P");
        msh.getMsh12_VersionID().getVersionID().setValue("2.7");
        msh.getMsh17_CountryCode().setValue("VNM");
        msh.getMsh18_CharacterSet(0).setValue("UTF-8");

        String encode = msh.encode();
        return msh;
    }

    private PID createPID(PID pid, PatientDTO patientDTO) throws HL7Exception {
        pid.getPid1_SetIDPID().setValue("1");
        pid.getPid3_PatientIdentifierList(0).getCx1_IDNumber().setValue(patientDTO.getPid());
        pid.getPid5_PatientName(0).getXpn1_FamilyName().getFn1_Surname().setValue(patientDTO.getFullname());
        pid.getPid7_DateTimeOfBirth().setValue(patientDTO.getBirthDate());
        pid.getPid8_AdministrativeSex().getCwe1_Identifier().setValue(patientDTO.getGender());
        String encode = pid.encode();
        return pid;
    }

    private OBR createOBR(OBR obr, ReportDTO reportDTO) throws HL7Exception {
        obr.getObr1_SetIDOBR().setValue("1");
        reportDTO.getRequestNumber();
        obr.getObr2_PlacerOrderNumber().getEi1_EntityIdentifier().setValue(reportDTO.getRequestNumber());
        obr.getObr4_UniversalServiceIdentifier().getCwe1_Identifier().setValue(reportDTO.getProcedureCode());
        obr.getObr4_UniversalServiceIdentifier().getCwe2_Text().setValue(reportDTO.getProcedureName());
        obr.getObr6_RequestedDateTime().setValue(reportDTO.getCreatedDatetime());
        obr.getObr7_ObservationDateTime().setValue(reportDTO.getApprovedDatetime());
        obr.getObr24_DiagnosticServSectID().setValue(reportDTO.getModality().getModalityType());
        return obr;
    }

    private OBX createOBX(ORU_R01 oruR01, Integer count, ReportDTO reportDTO) throws HL7Exception {
        OBX obx = oruR01.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION(count - 1).getOBX();
        obx.getObx1_SetIDOBX().setValue(count.toString());
        obx.getObx2_ValueType().setValue("TX");
        obx.getObx3_ObservationIdentifier().getCwe1_Identifier().setValue(reportDTO.getModality().getModalityType());
        obx.getObx4_ObservationSubID().setValue(reportDTO.getStudyInstanceUID());

        ST st = new ST(oruR01);
        if (count == 1) {
            st.setValue(new String(Base64.getEncoder().encode(reportDTO.getBodyHTML().getBytes(StandardCharsets.UTF_8))));
        } else if (count == 2) {
            st.setValue(new String(Base64.getEncoder().encode(reportDTO.getConclusionHTML().getBytes(StandardCharsets.UTF_8))));
        } else if (count == 3) {
            String viewerUrl = "http://192.168.1.32:8080/conn/ws/rest/v1/hospital/31313/viewer/" + reportDTO.getOrderNumber();
            st.setValue(new String(Base64.getEncoder().encode(viewerUrl.getBytes(StandardCharsets.UTF_8))));
        } else if (count == 4) {
            String pdfUrl = "http://192.168.1.32:8080/conn/ws/rest/v1/hospital/31313/pdf/" + reportDTO.getId();
            st.setValue(new String(Base64.getEncoder().encode(pdfUrl.getBytes(StandardCharsets.UTF_8))));
        }

        obx.getObx5_ObservationValue(0).setData(st);
        obx.getObx11_ObservationResultStatus().setValue("F");
        obx.getObx14_DateTimeOfTheObservation().setValue(reportDTO.getApprovedDatetime());
        obx.getObx16_ResponsibleObserver(0).getXcn1_PersonIdentifier().setValue(reportDTO.getApprover().getCode());
        obx.getObx16_ResponsibleObserver(0).getXcn2_FamilyName().getSurname().setValue(reportDTO.getApprover().getName());

        return obx;
    }

    @Test
    void test() throws HL7Exception {
        ReportDTO reportDTO = sampleReport();
        ORU_R01 oruR01 = new ORU_R01();
        oruR01.getParser().getParserConfiguration().setValidating(false);

        createMSH(oruR01.getMSH(), reportDTO);
        createPID(oruR01.getPATIENT_RESULT().getPATIENT().getPID(), reportDTO.getPatient());
        createOBR(oruR01.getPATIENT_RESULT().getORDER_OBSERVATION().getOBR(), reportDTO);

        createOBX(oruR01, 1, reportDTO);
        createOBX(oruR01, 2, reportDTO);
        createOBX(oruR01, 3, reportDTO);
        createOBX(oruR01, 4, reportDTO);


        System.out.println("def");

    }

    @Test
    void test1() throws HL7Exception {
        ReportDTO reportDTO = sampleReport();
//        HisResponse report = hisReportService.createReport(reportDTO);
        System.out.println("def");
    }

    @Autowired
    private RisService risService;

    @Test
    void test2() throws Exception {
        String accessionNumber = "162614";
        byte[] signedPDF = risService.getSignedPDF(accessionNumber);
        System.out.println("def");


    }

    @Autowired
    private SignService signService;

    @Test
    void sign() throws Exception {
        ITSignDTO dto = new ITSignDTO();
        dto.setSecret("12345678");
        dto.setAgreementID("72A2DA14-F926-47AB-B4FB-7A86D9D0C6FF");
        dto.setFile(Files.readAllBytes(Paths.get("/home/phtran/24h.pdf")));
        byte[] sign = signService.sign(dto);

        IOUtils.write(sign, new FileOutputStream("/home/phtran/24h.signed.pdf"));
        System.out.println("def");
    }


    @Autowired
    private PatientHandler patientHandler;

    @Autowired
    private OrderHandler orderHandler;

    @Test
    void test10() throws Exception {
        String incomingMessageString = "MSH|^~\\&|HIS|INFOMED|||202308230813||OMI^O23^OMI_O23|20230829081347|P|2.7|||AL|NE|VNM|UNICODE UTF-8\rPID|||7828192||ABC test||19510716|F||Kinh|Xã Nam Giang,Huyện Nam Đàn,Nghệ An||0932347588\rPV1|1|I|K03.2,Khoa Nội B^Buồng 105^Giường 11.B|E\rIN1|1|HT2404017623893||||||||||20210101|20250630\rORC|NW|311371289|||SC|N||||||sys.dinhntk^Nguyễn Thị Kim Dinh|||20230829081347||PSADT^Nhà A3- Khoa Cận Lâm Sàng- Phòng Siêu âm||||||||||20230829081347||I\rTQ1|1|1|||||20230829081347||S\rOBR|1|311371289^311371289||SA.1030^Siêu âm khớp (gối, háng, khuỷu, cổ tay….) (Khớp vai Trái + Khớp cổ tay Trái)||20230829081347||||||||||sys.dinhntk^^Nguyễn Thị Kim Dinh||||||||US|||||||U55.021^Bán thân bất toại [Liệt cứng nửa người]\r";
        Map<String, Object> metadata = new HashMap();
        DefaultHapiContext context = new DefaultHapiContext();
        context.setValidationContext(ValidationContextFactory.noValidation());
        ApplicationRouter applicationRouter = new ApplicationRouterImpl(context.getGenericParser());

        applicationRouter.bindApplication(new AppRoutingDataImpl("ADT", "A06", "*", "*"), patientHandler);
        applicationRouter.bindApplication(new AppRoutingDataImpl("OMI", "O23", "*", "*"), orderHandler);


        Transportable transportable = applicationRouter.processMessage(new TransportableImpl(incomingMessageString, metadata));
        System.out.printf("def");
    }
    @Test
    void testClient() throws HL7Exception, IOException {
        String rawMessage = "MSH|^~\\&|HIS|0|PACS||20230904230126||OMI^O23^OMI_O23|139905-20230904230126|P|2.7|||||VNM|UNICODE UTF-8\r" +
                "PID|1||2200022271||NGUYỄN THỊ HIÊN||19900725|F|||Trường mầm non Hà Huy tập;  ,Phường Hà Huy Tập,Thành phố Vinh,Nghệ An||0976959661\r" +
                "PV1|1||K19,Khoa Ngoại tổng hợp|E\r" +
                "IN1||HC4404014023152||||||||||20220101|20241231\r" +
                "ORC|NW|139905||||||||||sys.hanhnt^Nguyễn Thị Hạnh|||20220707172600\r" +
                "OBR||139905^139905||SA.1012^Siêu âm ổ bung (gan mật, tụy, lách, thận, bàng quang)|Y|20220707172600||||||||||||||||||US|||||||U62.392.3^Yêu thống [Đau lưng]\r" +
                "OBR||139905^1399099||SA.1013^Siêu âm ổ bung so 1 (gan mật, tụy, lách, thận, bàng quang)|Y|20220707172600||||||||||||||||||US|||||||U62.392.3^Yêu thống [Đau co]";
        String host = "localhost";
        int port = 7000;
        String uri = "/";
        DefaultHapiContext context = new DefaultHapiContext();
        context.setValidationContext(ValidationContextFactory.noValidation());
        HohClientSimple client = new HohClientSimple(host, port, uri);
        Message parsedMessage = context.getGenericParser().parse(rawMessage);
        try {
            // sendAndReceive actually sends the message
            IReceivable<Message> receivable = client.sendAndReceiveMessage(parsedMessage);

            // receivavle.getRawMessage() provides the response
            Message message = receivable.getMessage();
            System.out.println("Response was:\n" + message.encode());

            // IReceivable also stores metadata about the message
            String remoteHostIp = (String) receivable.getMetadata().get(MessageMetadataKeys.REMOTE_HOST_ADDRESS);
            System.out.println("From:\n" + remoteHostIp);

            /*
             * Note that the client may be reused as many times as you like,
             * by calling sendAndReceiveMessage repeatedly
             */

        } catch (DecodeException e) {
            // Thrown if the response can't be read
            e.printStackTrace();
        } catch (IOException e) {
            // Thrown if communication fails
            e.printStackTrace();
        } catch (EncodeException e) {
            // Thrown if the message can't be encoded (generally a programming bug)
            e.printStackTrace();
        }
    }

    @Autowired
    private DefaultHapiContext context;

    @Autowired
    private Hl7MessageService hl7MessageService;
    @Autowired
    private WorklistService worklistService;
    @Autowired
    private WorkListMessageService workListMessageService;
    @Autowired
    private RisMessageService risMessageService;
    @Test
    void testCreateOrder() throws HL7Exception {
        String rawMessage = "MSH|^~\\&|HIS|0|PACS||20230904230126||OMI^O23^OMI_O23|139905-20230904230126|P|2.7|||||VNM|UNICODE UTF-8\r" +
                "PID|1||2200022271||NGUYỄN THỊ HIÊN||19900725|F|||Trường mầm non Hà Huy tập;  ,Phường Hà Huy Tập,Thành phố Vinh,Nghệ An||0976959661\r" +
                "PV1|1||K19,Khoa Ngoại tổng hợp|E\r" +
                "IN1||HC4404014023152||||||||||20220101|20241231\r" +
                "ORC|NW|139987||||||||||sys.hanhnt^Nguyễn Thị Hạnh|||20220707172600\r" +
                "OBR||139987^139987||SA.1012^Siêu âm ổ bung (gan mật, tụy, lách, thận, bàng quang)|Y|20220707172600||||||||||||||||||US|||||||U62.392.3^Yêu thống [Đau lưng]\r";
        // step 1: luu message raw vao bang hl7_message
//        String messageId = this.hl7MessageService.saveMessageRaw(UUID.randomUUID().toString(),rawMessage, this.context.getGenericParser().parse(rawMessage).getName());
//        assertNotNull(messageId);

        // step 2: parse sang OrderDTO
        Message message = this.context.getGenericParser().parse(rawMessage);
//        OrderDTO orderDTO = this.orderHandler.parseMessage((OMI_O23) message);
//        assertNotNull(orderDTO);

        // step 3: gui sang worklist va response la success
//        WorklistResponse workListResponse = this.worklistService.sendWorklist(new WorklistDTO(orderDTO));
//        log.info("[WL] workList response {}",workListResponse);
//        assertTrue(workListResponse.isSucceed());

        // step 4: luu worklist response vs message da gui vao bang wl_message
//        Long wlID = this.workListMessageService.createOrUpdate(messageId, workListResponse);
//        assertNotNull(wlID);

        // step 5: gui sang ris va response tra ve la 200
//        RisResponse risResponse = this.risService.sendOrder(orderDTO);
//        log.info("[RIS] ris response {}",risResponse);
//        assertEquals(200,risResponse.getHeader().getCode());

        // step 6: luu ris response vs message da gui vao bang ris_message
//        Long risID = this.risMessageService.createOrUpdate(messageId, risResponse);
//        assertNotNull(risID);

    }
    @Test
    void testMissingRequiredSegment() throws HL7Exception {
        String rawMessage = "MSH|^~\\&|HIS|0|PACS||20230904230126||OMI^O23^OMI_O23|139905-20230904230126|P|2.7|||||VNM|UNICODE UTF-8\r" +
                "PID|1||2200022271||NGUYỄN THỊ HIÊN||19900725|F|||Trường mầm non Hà Huy tập;  ,Phường Hà Huy Tập,Thành phố Vinh,Nghệ An||0976959661\r" +
                "PV1|1||K19,Khoa Ngoại tổng hợp|E\r" +
                "IN1||HC4404014023152||||||||||20220101|20241231\r" +
                "ORC|NW|139905||||||||||sys.hanhnt^Nguyễn Thị Hạnh|||20220707172600\r";
        // step 1: luu message raw vao bang hl7_message
//        String messageId = this.hl7MessageService.saveMessageRaw(UUID.randomUUID().toString(),rawMessage, this.context.getGenericParser().parse(rawMessage).getName());
//        assertNotNull(messageId);

        // step 2: parse sang OrderDTO => thieu OBR (required) => Throw exception
        Message message = this.context.getGenericParser().parse(rawMessage);
//        assertThrows(HL7Exception.class,()->this.orderHandler.parseMessage((OMI_O23) message));
    }
    @Test
    void testDeleteOrder() throws Exception {
        String rawMessage = "MSH|^~\\&|HIS|0|PACS||20230904230126||OMI^O23^OMI_O23|139905-20230904230126|P|2.7|||||VNM|UNICODE UTF-8\r" +
                "PID|1||2200022271||NGUYỄN THỊ HIÊN||19900725|F|||Trường mầm non Hà Huy tập;  ,Phường Hà Huy Tập,Thành phố Vinh,Nghệ An||0976959661\r" +
                "PV1|1||K19,Khoa Ngoại tổng hợp|E\r" +
                "IN1||HC4404014023152||||||||||20220101|20241231\r" +
                "ORC|CA|139987||||||||||sys.hanhnt^Nguyễn Thị Hạnh|||20220707172600\r" +
                "OBR||139987^139987||SA.1012^Siêu âm ổ bung (gan mật, tụy, lách, thận, bàng quang)|Y|20220707172600||||||||||||||||||US|||||||U62.392.3^Yêu thống [Đau lưng]\r";
        // step 1: luu message raw vao bang hl7_message
//        String messageId = this.hl7MessageService.c(UUID.randomUUID().toString(),rawMessage, this.context.getGenericParser().parse(rawMessage).getName());
//        assertNotNull(messageId);

        // step 2: parse sang OrderDTO
        Message message = this.context.getGenericParser().parse(rawMessage);
        OrderDTO orderDTO = this.orderHandler.parse((OMI_O23) message);
        assertNotNull(orderDTO);

        // step 3: gui sang worklist va response la success
        WorklistResponse workListResponse = this.worklistService.removeWorklist(orderDTO.getAccessionNumber());
        log.info("[WL] workList response {}",workListResponse);
        assertTrue(workListResponse.isSucceed());

        // step 4: luu worklist response vs message da gui vao bang wl_message
//        Long wlID = this.workListMessageService.createOrUpdate(messageId, workListResponse);
//        assertNotNull(wlID);

        // step 5: gui sang ris va response tra ve la 200
        RisResponse risResponse = this.risService.removeOrder(orderDTO);
        log.info("[RIS] ris response {}",risResponse);
        assertEquals(200,risResponse.getHeader().getCode());

        // step 6: luu ris response vs message da gui vao bang ris_message
//        Long risID = this.risMessageService.createOrUpdate(messageId, risResponse);
//        assertNotNull(risID);
    }
    @Test
    void testReturnReport() throws HL7Exception {
        ModalityDTO modality = new ModalityDTO();
        modality.setCode("XQ01");
        modality.setModalityType("DX");

        UserDTO creator = new UserDTO();
        creator.setCode("sys.quynhbtn");
        creator.setName("Bùi Thị Như Quỳnh");

        UserDTO approver = new UserDTO();
        approver.setCode("sys.quynhbtn");
        approver.setName("Bùi Thị Như Quỳnh");

        PatientDTO patient = new PatientDTO();
        patient.setPid("2100034166");
        patient.setFullname("NGUYỄN VĂN A");
        patient.setGender("M");
        patient.setBirthDate("20110101");
        patient.setPhone("3");
        patient.setAddress("2 ,Huyện Nam Đàn,Nghệ An");

        ReportDTO report = new ReportDTO();
        report.setId(100L);
        report.setOrderNumber("79963");
        report.setAccessionNumber("79963");
        report.setOrderDatetime("20220526111300");
        report.setRequestNumber("79963");
        report.setProcedureCode("XQ.1147");

        report.setProcedureName("Chụp Xquang khớp khuỷu thẳng, nghiêng hoặc chếch");
        report.setBodyHTML(bodyHTML);
        report.setConclusionHTML(conclusionHTML);
        report.setCreatedDatetime("20220526111300");
        report.setApprovedDatetime("20220526111300");

        report.setPatient(patient);
        report.setModality(modality);
        report.setCreator(creator);
        report.setApprover(approver);

        // step 1: parse sang report DTO
//        HisResponse report1 = this.hisReportService.createReport(report);
//        log.info("[Report] {}",report1);

    }

    @Test
    void testCreateSignedReport(){
        String html = "<table style=\\\"width: 100%;\\\">\\n<thead>\\n<tr>\\n<td>\\n<div class=\\\"header-space\\\" style=\\\"height: 107px;\\\">&nbsp;</div>\\n</td>\\n</tr>\\n</thead>\\n<tbody>\\n<tr>\\n<td>\\n<div class=\\\"content\\\" style=\\\"word-wrap: break-word;\\\">\\n<table style=\\\"border-collapse: collapse; width: 100%; line-height: 1.2; font-size: 12pt; font-family: 'times new roman', times; text-align: left; vertical-align: middle;\\\">\\n<tbody>\\n<tr>\\n<td style=\\\"width: 50%;\\\">Bệnh nh&acirc;n(Name):&nbsp;<span id=\\\"patientName\\\" style=\\\"font-weight: bold;\\\"></span></td>\\n<td style=\\\"width: 24.8636%;\\\">Năm sinh (DOB):&nbsp;<span id=\\\"patientBirthYear\\\"></span></td>\\n<td style=\\\"width: 25.1364%;\\\">Giới t&iacute;nh (Sex):&nbsp;<span id=\\\"patientGender\\\"></span></td>\\n</tr>\\n</tbody>\\n</table>\\n<table style=\\\"border-collapse: collapse; width: 100%; line-height: 1.2; font-size: 12pt; font-family: 'times new roman', times; text-align: left; vertical-align: middle;\\\">\\n<tbody>\\n<tr>\\n<td style=\\\"width: 50%;\\\">M&atilde; thẻ BHYT: <span id=\\\"insuranceNumber\\\"></span></td>\\n</tr>\\n</tbody>\\n</table>\\n<table style=\\\"border-collapse: collapse; width: 100%; line-height: 1.2; font-size: 12pt; font-family: 'times new roman', times; text-align: left; vertical-align: middle;\\\">\\n<tbody>\\n<tr>\\n<td>Địa chỉ(Address):&nbsp;&nbsp;&nbsp;<span id=\\\"patientAddress\\\"></span></td>\\n</tr>\\n</tbody>\\n</table>\\n<table style=\\\"border-collapse: collapse; width: 100%; line-height: 1.2; font-size: 12pt; font-family: 'times new roman', times; text-align: left; vertical-align: middle;\\\">\\n<tbody>\\n<tr>\\n<td>Chẩn đo&aacute;n l&acirc;m s&agrave;ng(Diagnosis):&nbsp;<span id=\\\"clinicalDiagnosis\\\"></span></td>\\n</tr>\\n</tbody>\\n</table>\\n<div>\\n<div>\\n<table style=\\\"border-collapse: collapse; width: 100%; line-height: 1.2; font-size: 12pt; font-family: 'times new roman', times; text-align: left; vertical-align: middle;\\\">\\n<tbody>\\n<tr><!-- <td style=\\\"width: 50%;\\\">STT KSK:</td> -->\\n<td style=\\\"width: 50%;\\\">Khoa(Department):&nbsp;<span id=\\\"clinicalDepartment\\\"></span></td>\\n</tr>\\n</tbody>\\n</table>\\n<table style=\\\"border-collapse: collapse; width: 100%; line-height: 1.2; font-size: 12pt; font-family: 'times new roman', times; text-align: left; vertical-align: middle;\\\">\\n<tbody>\\n<tr><!-- <td style=\\\"width: 50%;\\\">STT KSK:</td> -->\\n<td style=\\\"width: 50;\\\">Thời gian thực hiện: <span id=\\\"startTime\\\"></span></td>\\n</tr>\\n</tbody>\\n</table>\\n<table style=\\\"border-collapse: collapse; width: 100%; line-height: 1.2; font-size: 12pt; font-family: 'times new roman', times; text-align: left; vertical-align: middle;\\\">\\n<tbody>\\n<tr><!-- <td style=\\\"width: 50%;\\\">STT KSK:</td> -->\\n<td style=\\\"width: 50;\\\">Thời gian ho&agrave;n th&agrave;nh: <span id=\\\"endTime\\\"></span></td>\\n</tr>\\n</tbody>\\n</table>\\n<table style=\\\"border-collapse: collapse; width: 100%; line-height: 1.2; font-size: 12pt; font-family: 'times new roman', times; text-align: left; vertical-align: middle;\\\">\\n<tbody>\\n<tr><!-- <td style=\\\"width: 50%;\\\">STT KSK:</td> -->\\n<td style=\\\"width: 50;\\\">Thiết bị CĐHA: M&aacute;y si&ecirc;u &acirc;m m&agrave;u 4D ALOKA</td>\\n</tr>\\n</tbody>\\n</table>\\n<div>\\n<table style=\\\"border-collapse: collapse; width: 100%; line-height: 1.2; font-size: 12pt; font-family: 'times new roman', times; text-align: left; vertical-align: middle; height: 19.1875px;\\\">\\n<tbody>\\n<tr style=\\\"height: 19.1875px;\\\">\\n<td style=\\\"font-weight: 600; font-size: 12pt; height: 19.1875px;\\\">CHỈ ĐỊNH (Assignment):&nbsp;\\n<div id=\\\"orderProcedure\\\"></div>\\n</td>\\n</tr>\\n</tbody>\\n</table>\\n<table style=\\\"border-collapse: collapse; width: 100%; line-height: 1.2; font-size: 12pt; font-family: 'times new roman', times; text-align: left; vertical-align: middle;\\\">\\n<tbody style=\\\"text-align: center;\\\">\\n<tr>\\n<td style=\\\"font-family: 'times new roman', times; color: #0000ff; font-weight: 600; font-size: 13pt; letter-spacing: .5px;\\\">KẾT QUẢ X-QUANG</td>\\n</tr>\\n</tbody>\\n</table>\\n<div>\\n<table style=\\\"border-collapse: collapse; width: 100%;\\\">\\n<tbody>\\n<tr>\\n<td style=\\\"width: 100%; text-align: left;\\\"><span style=\\\"font-family: 'times new roman', times;\\\"><strong><span style=\\\"font-size: 12pt; color: #0000ff;\\\">1. M&Ocirc; TẢ (Describe/Finding):</span></strong></span></td>\\n</tr>\\n<tr>\\n<td style=\\\"width: 100%; font-size: 12pt; font-family: 'times new roman', times; text-align: left; vertical-align: top;\\\">\\n<div id=\\\"reportBody\\\"></div>\\n</td>\\n</tr>\\n</tbody>\\n</table>\\n</div>\\n<div>\\n<table style=\\\"border-collapse: collapse; width: 100%;\\\">\\n<tbody>\\n<tr>\\n<td style=\\\"color: #1921dc; font-size: 12pt; font-family: 'times new roman', times; text-align: left; vertical-align: top;\\\"><strong>2. KẾT LUẬN (Conclusion):</strong></td>\\n</tr>\\n<tr>\\n<td style=\\\"font-size: 12pt; font-weight: bold; font-family: 'times new roman', times; text-align: left; vertical-align: top;\\\">\\n<div id=\\\"reportConclusion\\\"></div>\\n</td>\\n</tr>\\n</tbody>\\n</table>\\n<div>\\n<table style=\\\"border-collapse: collapse; width: 100%;\\\">\\n<tbody>\\n<tr>\\n<td style=\\\"width: 100%; text-align: left; vertical-align: top; font-family: 'times new roman', times;\\\">\\n<div>&nbsp;</div>\\n<div><span style=\\\"color: #1921dc; font-size: 12pt; font-family: 'times new roman', times; text-align: left; vertical-align: top;\\\"><strong>3. ĐỀ NGHỊ (Proposal):&nbsp;</strong></span>\\n<div id=\\\"suggestion\\\" style=\\\"width: 78.7242%; font-size: 12pt;\\\"></div>\\n</div>\\n</td>\\n</tr>\\n</tbody>\\n</table>\\n</div>\\n</div>\\n</div>\\n</div>\\n</div>\\n</div>\\n</td>\\n</tr>\\n</tbody>\\n<tfoot>\\n<tr>\\n<td>\\n<div class=\\\"footer-space\\\" style=\\\"height: 145px;\\\">&nbsp;</div>\\n</td>\\n</tr>\\n</tfoot>\\n</table>\\n<div class=\\\"header\\\" style=\\\"height: 105px; position: fixed; top: 0; border-bottom: 1px solid #0000ff; width: 100%;\\\"><header>\\n<table style=\\\"border-collapse: collapse; width: 100%; line-height: 1; margin-top: 10pt; margin-bottom: 1.5pt;\\\">\\n<tbody>\\n<tr>\\n<td style=\\\"width: 100%;\\\">\\n<div style=\\\"position: relative;\\\">\\n<div style=\\\"position: absolute; top: 0; left: 0;\\\"><img id=\\\"logo\\\" style=\\\"text-align: right;\\\" width=\\\"100\\\" height=\\\"80\\\"></div>\\n<div style=\\\"padding-right: 50px;\\\">\\n<div style=\\\"text-align: center;\\\"><span style=\\\"font-family: 'times new roman', times; color: #0000ff;\\\"><strong>BỆNH VIỆN Y HỌC CỔ TRUYỀN NGHỆ AN</strong></span></div>\\n<div style=\\\"text-align: center;\\\"><span style=\\\"font-family: 'times new roman', times; color: #0000ff;\\\"><strong><span style=\\\"font-size: 10pt;\\\">Nghe An Traditional Medicine Hospital</span></strong></span></div>\\n<div style=\\\"text-align: center;\\\"><span style=\\\"font-size: 10pt; font-family: 'times new roman', times; color: #0000ff;\\\">&nbsp; &nbsp; &nbsp; Địa chỉ: Số 1, Đường Tuệ Tĩnh, Khối Trung H&ograve;a, P. H&agrave; Huy Tập, Th&agrave;nh phố Vinh, T. Nghệ An</span></div>\\n<div style=\\\"text-align: center;\\\"><span style=\\\"font-size: 10pt; font-family: 'times new roman', times; color: #0000ff;\\\">&nbsp; &nbsp; &nbsp; <span style=\\\"font-size: 8pt;\\\">Điện thoại: 0238.3522.444</span></span></div>\\n</div>\\n<div style=\\\"position: absolute; bottom: 5px; right: 0;\\\"><!-- div style=\\\"width: 180px;\\\"><img id=\\\"pIdBarcode\\\" style=\\\"padding-left: 10px; width: 100%; object-fit: cover;\\\" height=\\\"50\\\" /></div> --></div>\\n<div style=\\\"font-size: 10pt; position: absolute; bottom: -10px; right: 20px;\\\">\\n<div><span style=\\\"font-size: 10pt; font-family: 'times new roman', times;\\\"><span style=\\\"font-size: 12pt; font-family: 'times new roman', times;\\\">M&atilde; BN (ID No):&nbsp;</span><span id=\\\"patientId\\\"></span></span></div>\\n</div>\\n</div>\\n</td>\\n</tr>\\n</tbody>\\n</table>\\n</header></div>\\n<div class=\\\"footer\\\" style=\\\"height: 200px; position: fixed; bottom: 0; width: 100%;\\\">\\n<div>\\n<table style=\\\"border-collapse: collapse; width: 100%; line-height: 1; font-family: 'times new roman', times; margin-bottom: -45px;\\\">\\n<tbody>\\n<tr>\\n<td style=\\\"width: 5.06896%; text-align: left; vertical-align: top;\\\">\\n<p><img id=\\\"portalQR\\\" style=\\\"text-align: right;\\\" src=\\\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAiMAAAIjAQMAAADr5InyAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAGUExURQAAAP///6XZn90AAAAJcEhZcwAADsMAAA7DAcdvqGQAAAMySURBVHja7Z2xcqMwEECXSUGZ1hX6FH/a+dPyKb7qWpcpPOhiwVoSxhkzFs6ivK1wgOfVs2d2WBZHZuKP7+OLxvtT2PBjfMhjsZjiDOVSHwW72N0qxZHLirlcKG8+jeNIacOrw4WicRZ5D5TshE+RrgilTC5iKBfsYvd32xUoW7F7GktCO1LGRMfjvvbuvde3C5RzrDPGKK66FWEXu0soHXaxW4QyxEGuoX9aZHcJpStCkeoo2MUudrH7YrvJ1YRMLyEeX9GGKa4IpTGUiyUv2MUudovlksQxux+QbGaUJL7tKL2eUmZFXXVesOuxi10os5Sb0EqiwPdYPobNaUCBAqUuihiiNEUo2L1n18/FUD6cfzQMUY6GKH+xuyKlPrv1fUbYXZNSyovG/vLqM1aSgZJ1lM5Z+XDJ2xWhiCGKq44iRShddSuyRMHumhTs3qOESnCKFDcZCO31PTJE0oUKHcgSFFeEIoZyYUXYxS5erNtNRj61jZSEdpT8OBuaj4kebu4s10Lp8IJd7EJZya4YoszWIw0FKmUIpaQPEhShdEUoYoiCXexu1a6lz6ip7vti1q5co4nNpWsoUKeLkjEkZ4gihnLBLnahQFmTMrT5A6Udz+inD3rpMJDOBb3FA6FAWUhpilDE0IoEu9jdrN1sZ/6MWLgOuN4a0GOTXlCIsUf2PKUpQrG0ovpywS52sVtbLmFreE446wXlbSEfryY0kjrjDVGOhnLpsbtiLtjF7lbt2splZuc0dO/MbKif1KOfpwi5/JIVWcoFu/ZX1BnKxdb3xc9Fn11YyHjH+DwF6t6uCMUZooihXLCL3ddTdnhZkWLL7kzE0SNJZkOT34oImy6fmbVC2VW3IkuUtghFoGD35RTs3qckdwh8Vj40BkqyGSuJv9p9niLVUVx1dh0UKFBqpEx/O9rHSuLj3jwm3S0oUKBAgWKEosfps1/JZtILusyTQoECBQqUeij9zGyonqoUF/+zl4MCBcrTFGFF2P2pFSWRU/RqYn/bC3LZOQUo3hDlSC4rUv7xGa1IwS52l+ZyE1klGWI6F+Szyw4pQmkN5eKqy2VnKBfsYhe7RnKR3X8hYjOkLdFBjwAAAABJRU5ErkJggg==\\\" width=\\\"60\\\" height=\\\"60\\\"></p>\\n</td>\\n<td style=\\\"width: 58.4836%; text-align: left; vertical-align: top; line-height: 1;\\\">\\n<p style=\\\"margin-bottom: 0;\\\"><span style=\\\"font-size: 8pt; font-weight: 600; font-style: italic;\\\">T&agrave;i khoản : <span class=\\\"patientId\\\">abc</span> </span><br><span style=\\\"font-size: 8pt; font-weight: 600; font-style: italic;\\\">Mật khẩu : <span class=\\\"portalCode\\\">xyz</span></span></p>\\n<p style=\\\"margin-top: 5px;\\\"><span style=\\\"font-size: 8pt;\\\">Vui l&ograve;ng thay đổi mật khẩu khi đăng nhập lần đầu th&agrave;nh c&ocirc;ng!</span></p>\\n</td>\\n<td style=\\\"width: 33.3333%; text-align: center; vertical-align: top;\\\" rowspan=\\\"3\\\">\\n<p class=\\\"today\\\" style=\\\"margin-top: 0pt; margin-bottom: 0pt; font-weight: bold; color: #0000ff; font-family: 'Times New Roman'; font-size: 11pt;\\\">&nbsp;</p>\\n<p style=\\\"margin-top: 0pt; margin-bottom: 0pt; font-size: 11pt; color: #0000ff; font-family: 'times new roman', times;\\\"><span style=\\\"color: #0000ff;\\\"><strong>B&aacute;c sĩ chuy&ecirc;n khoa</strong></span></p>\\n<p style=\\\"margin-top: 0pt; margin-bottom: 0pt; font-size: 11pt; color: #0000ff font-family: 'times new roman', times;\\\"><span style=\\\"color: #0000ff;\\\"><strong>Speacialist doctor</strong></span></p>\\n<img id=\\\"signature\\\" style=\\\"text-align: right;\\\" width=\\\"90\\\" height=\\\"60\\\">\\n<p><strong id=\\\"doctorName\\\" style=\\\"margin-top: 0pt; margin-bottom: 0pt; color: #0000ff; font-family: 'Times New Roman'; font-weight: bold; font-size: 11pt;\\\"></strong></p>\\n</td>\\n</tr>\\n<tr>\\n<td style=\\\"width: 8.06896%; text-align: left; vertical-align: top;\\\">\\n<p><img id=\\\"logoIcon\\\" style=\\\"text-align: right;\\\" width=\\\"50\\\" height=\\\"50\\\"></p>\\n</td>\\n<td style=\\\"width: 58.4836%; text-align: left; vertical-align: top; line-height: 1;\\\">\\n<p><span style=\\\"font-size: 11pt;\\\">Người k&iacute;: <span class=\\\"doctorName\\\" style=\\\"text-transform: uppercase;\\\">abc</span> </span><br><span style=\\\"font-size: 11pt;\\\">Đơn vị: Bệnh viện y học cổ truyền Nghệ An</span><br><span style=\\\"font-size: 11pt;\\\">Thời gian k&iacute;: <span class=\\\"today\\\">abc</span></span></p>\\n</td>\\n</tr>\\n</tbody>\\n</table>\\n</div>\\n</div>";
        PdfReportDTO pdfReportDTO = new PdfReportDTO();
        pdfReportDTO.setData(html.getBytes());
        pdfReportDTO.setAccessionNumber("18.0203.000001");
        pdfReportDTO.setOrderNumber("18.0203.000001");
        pdfReportDTO.setProcedureCode("SA.1012");
        pdfReportDTO.setSigner(new UserDTO("Code","John"));

        this.risService.createPdfReport(pdfReportDTO);

        System.out.println("ad");


    }
    @Test
    void testConvertHtmlToText(){
        String text = this.convertToText("<div>-- H&igrave;nh ảnh nhu m&ocirc; phổi hai b&ecirc;n s&aacute;ng đều<br>-- Kh&ocirc;ng thấy tràn dịch tràn khí m&agrave;ng phổi.<br>-- Xương v&agrave; Phần mềm v&ugrave;ng ngực b&igrave;nh thường.<br>-- Bóng tim kh&ocirc;ng to.</div>");
        System.out.println("ABC");
    }
    public String convertToText(String htmlContent) {
        Document document = Jsoup.parse(htmlContent);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        String s = document.html().replaceAll("\\\\n", "\\\\X0D\\\\\\\\X0A\\\\");
        return Jsoup.clean(s, "", Whitelist.none(),new Document.OutputSettings().prettyPrint(false));
    }

    @Test
    void testConvertPath(){
        String path = "\\\\10.20.112.22\\FileSigned$\\PACS\\logs\\23678.pdf";

        String base64 = Base64Util.encode(path);

        byte[] decoded = Base64.getDecoder().decode(base64);
        String decodeString = new String(decoded);

        System.out.println("ABC");
    }

    @Test
    void testConvertHtml(){
        String html = "<div>- Gan: kh&ocirc;ng to, bờ đều, nhu m&ocirc; đều, kh&ocirc;ng thấy khối bất thường, TM cửa kh&ocirc;ng gi&atilde;n, kh&ocirc;ng thấy huyết khối.</div>\n" +
                "<div>- T&uacute;i mật: kh&ocirc;ng căng, th&agrave;nh mỏng, kh&ocirc;ng sỏi, dịch mật trong, kh&ocirc;ng thấy dịch quanh t&uacute;i mật.</div>\n" +
                "<div>- Đường mật: trong gan kh&ocirc;ng gi&atilde;n, kh&ocirc;ng sỏi, kh&ocirc;ng giun, OMC kh&ocirc;ng gi&atilde;n, kh&ocirc;ng sỏi.</div>\n" +
                "<div>- Tuỵ k&iacute;ch thước b&igrave;nh thường, bờ đều, nhu m&ocirc; đều, kh&ocirc;ng thấy khối bất thường, ống tuỵ kh&ocirc;ng gi&atilde;n, kh&ocirc;ng thấy dịch quanh tuỵ.</div>\n" +
                "<div>- L&aacute;ch: k&iacute;ch thước b&igrave;nh thường, nhu m&ocirc; đều, kh&ocirc;ng thấy khối bất thường, TM l&aacute;ch kh&ocirc;ng gi&atilde;n.</div>\n" +
                "<div>- Thận P : k&iacute;ch thước b&igrave;nh thường, bờ đều, nhu m&ocirc; đều, kh&ocirc;ng thấy khối bất thường, ranh giới tuỷ vỏ r&otilde;, đ&agrave;i bể thận kh&ocirc;ng gi&atilde;n, kh&ocirc;ng sỏi.</div>\n" +
                "<div>- Niệu quản P: kh&ocirc;ng gi&atilde;n.</div>\n" +
                "<div>- Thận T : k&iacute;ch thước b&igrave;nh thường, bờ đều, nhu m&ocirc; đều, kh&ocirc;ng thấy khối bất thường, ranh giới tuỷ vỏ r&otilde;, đ&agrave;i bể thận kh&ocirc;ng gi&atilde;n, kh&ocirc;ng sỏi.</div>\n" +
                "<div>- Niệu quản T: kh&ocirc;ng gi&atilde;n.</div>\n" +
                "<div>- B&agrave;ng quang : th&agrave;nh đều, kh&ocirc;ng thấy khối bất thường, nước tiểu trong, kh&ocirc;ng sỏi.</div>\n" +
                "<div>- Tử cung: k&iacute;ch thước kh&ocirc;ng to, nội mạc mỏng.</div>\n" +
                "<div>- Buồng trứng hai b&ecirc;n kh&ocirc;ng thấy bất thường.</div>\n" +
                "<div>\n" +
                "<div>\n" +
                "<ul>\n" +
                "<li>Kh&ocirc;ng c&oacute; rượu th&igrave; buồn n&ocirc;n. Phải 5l cơ mới tỉnh được</li>\n" +
                "</ul>\n" +
                "</div>\n" +
                "</div>";
        String text = HtmlUtil.getInstance().convertToText(html);
        System.out.println("abc");
    }


}
