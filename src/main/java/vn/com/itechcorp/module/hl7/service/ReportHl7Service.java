package vn.com.itechcorp.module.hl7.service;

import ca.uhn.hl7v2.HL7Exception;
import vn.com.itechcorp.his.dto.HisRequest;
import vn.com.itechcorp.module.report.constants.ReportType;
import vn.com.itechcorp.module.report.service.dto.report.ReportDTOGet;

public interface ReportHl7Service {

    HisRequest createReport(ReportDTOGet object, String pdfPath, ReportType type) throws HL7Exception;

    HisRequest deleteReport(String accessionNumber, String procedureCode);
}
