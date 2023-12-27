package vn.com.itechcorp.his.service;

import vn.com.itechcorp.his.dto.HisRequest;
import vn.com.itechcorp.his.dto.HisResponse;

public interface HisService {
    HisResponse processMessage(HisRequest object);

    HisResponse sendReport(HisRequest object);

    HisResponse deleteReport(HisRequest object);
}
