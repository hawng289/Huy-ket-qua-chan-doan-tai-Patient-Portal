package vn.com.itechcorp.module.local.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.itechcorp.base.api.response.APIListResponse;
import vn.com.itechcorp.base.service.filter.PaginationInfo;
import vn.com.itechcorp.his.dto.HisResponse;
import vn.com.itechcorp.module.local.api.method.Hl7MessageAsyncMethod;
import vn.com.itechcorp.module.local.service.ResendService;
import vn.com.itechcorp.module.local.service.dto.Hl7MessageDTOGet;
import vn.com.itechcorp.module.local.service.dto.Hl7MessageFilter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/conn/messages")
@Api(value = "message-api", tags = "message-api")
public class LocalMessageController {

    @Autowired
    private Hl7MessageAsyncMethod hl7MessageAsyncMethod;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ResendService resendService;

    @ApiOperation(value = "View a list of messages")
    @GetMapping
    public CompletableFuture<ResponseEntity<APIListResponse<List<Hl7MessageDTOGet>>>> retrieveMessage(@RequestParam(required = false, name = "orderBy", defaultValue = "messageTime") String orderBy,
                                                                                                      @RequestParam(required = false, name = "offset", defaultValue = "0") int offset,
                                                                                                      @RequestParam(required = false, name = "limit", defaultValue = "100") int limit) {
        return this.hl7MessageAsyncMethod.getListAsync(new PaginationInfo(offset, limit, orderBy));
    }

    @ApiOperation(value = "Search message")
    @PostMapping("/search")
    public ResponseEntity<APIListResponse<List<Hl7MessageDTOGet>>> search(@RequestBody Hl7MessageFilter hl7MessageFilter) {
        return this.hl7MessageAsyncMethod.search(hl7MessageFilter, new PaginationInfo(0, 25));
    }


    @ApiOperation(value = "Re-Send message to ris")
    @PostMapping("/resend/ris/{messageId}")
    public HisResponse resendMessageToRis(@PathVariable Long messageId) {
        logger.info("[MsgID-{}] Resend Hl7 message", messageId);
        try {
            boolean resend = this.resendService.resendToRis(messageId);
            return new HisResponse(resend);
        } catch (Exception e) {
            logger.error("[MsgID-{}] FAILED Resend Hl7 message {}", messageId, e.getMessage());
            return new HisResponse(false);
        }
    }

    @ApiOperation(value = "Re-Send message to wl")
    @PostMapping("/resend/wl/{messageId}")
    public HisResponse resendMessageToWL(@PathVariable Long messageId) {
        logger.info("[MsgID-{}] Resend Hl7 message", messageId);
        try {
            boolean resend = this.resendService.resendToWl(messageId);
            return new HisResponse(resend);
        } catch (Exception e) {
            logger.error("[MsgID-{}] FAILED Resend Hl7 message {}", messageId, e.getMessage());
            return new HisResponse(false);
        }
    }

}
