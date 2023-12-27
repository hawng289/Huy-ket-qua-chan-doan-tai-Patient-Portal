package vn.com.itechcorp.module.notification.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.ris.dto.Dto;

import java.util.Date;

@Getter @Setter @NoArgsConstructor
public class TokenResponse extends Dto {

    private String access_token;

    private String token_type;

    private Long expires_in;

    private String userName;

    private String userId;

    private Long now = new Date().getTime();

    public boolean isExpired() {
        long currentTime = new Date().getTime();
        return currentTime > now + this.expires_in;
    }

}
