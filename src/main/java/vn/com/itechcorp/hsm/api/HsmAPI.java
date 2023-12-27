package vn.com.itechcorp.hsm.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.com.itechcorp.hsm.dto.ITSignDTO;
import vn.com.itechcorp.hsm.method.SignAPIMethod;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@Api(value = "hsm-api", tags = "hsm-api")
@RestController
public class HsmAPI {

    @Autowired
    private SignAPIMethod signAPIMethod;

    @ApiOperation(value = "Sign PDF")
    @PostMapping("/hsm")
    public CompletableFuture<ResponseEntity<byte[]>> signPdf(@Valid @RequestBody ITSignDTO signDTO) {
        return signAPIMethod.signAsync(signDTO);
    }

}
