package vn.com.itechcorp.hsm.service;

import vn.com.itechcorp.hsm.dto.ITSignDTO;

public interface SignService {

    byte[] sign(ITSignDTO request);

}
