package vn.com.itechcorp.hsm.method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.hsm.dto.ITSignDTO;
import vn.com.itechcorp.hsm.service.SignService;

import java.util.concurrent.CompletableFuture;

@Service("signAPIMethod")
public class SignAPIMethod {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SignService signService;

    @Async
    public CompletableFuture<ResponseEntity<byte[]>> signAsync(ITSignDTO request) {
        return CompletableFuture.completedFuture(sign(request));
    }

    public ResponseEntity<byte[]> sign(ITSignDTO request) {
        try {
            byte[] response = signService.sign(request);
            if (response != null) {
                logger.info("[RIS-API][AgreementID-{}] Response size from signService: {}", request.getAgreementID(), response.length);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            logger.info("[RIS-API][AgreementID-{}] Response size from signService: failed", request.getAgreementID());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
