package vn.com.itechcorp.ris.service;

import vn.com.itechcorp.ris.dto.*;

public interface RisService {

    RisResponse sendOrder(OrderDTO object);

    RisResponse removeOrder(OrderDTO wml);

    RisMWLResponse removeMWL(String accessionNumber);

    RisMWLResponse createReport(ReportDTO object);

    String getViewerUrl(String accessionNumber);

    byte[] getSignedPDF(String accessionNumber) throws Exception;

    RisResponse createPatient(PatientDTO patient);

    RisResponse updatePatient(PatientDTO patient);

    RisResponse createStudy(StudyDTO study);

    RisMWLResponse createPdfReport(PdfReportDTO object);

    RisMWLResponse removeReport(String accessionNumber, String procedureCode);

    RisMWLResponse lockOrder(String orderNumber);

    RisResponse updateHisReportStatus(String orderNumber, String procedureCode, HisReportStatusUpdate object);
}
