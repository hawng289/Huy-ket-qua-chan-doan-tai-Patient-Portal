package vn.com.itechcorp.hsm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.ris.dto.Dto;

@Getter
@Setter
@NoArgsConstructor
public class CredentialData extends Dto {

    private String username;

    private String password;

    private String timestamp;

    private String signature;

    private String pkcs1Signature;
}
