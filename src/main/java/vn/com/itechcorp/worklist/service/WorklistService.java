package vn.com.itechcorp.worklist.service;

import org.dcm4che3.data.Attributes;
import vn.com.itechcorp.worklist.dto.MwlItemFilter;
import vn.com.itechcorp.worklist.dto.WorklistDTO;
import vn.com.itechcorp.worklist.dto.WorklistResponse;

import java.util.List;

public interface WorklistService {

    WorklistResponse sendWorklist(WorklistDTO object);

    WorklistResponse removeWorklist(String accessionNumber);

    String searchWorkList(MwlItemFilter mwlItemFilter);

    List<Attributes> getWorkList(MwlItemFilter mwlItemFilter);
}
