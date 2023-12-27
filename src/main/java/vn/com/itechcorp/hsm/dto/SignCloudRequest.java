package vn.com.itechcorp.hsm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.ris.dto.Dto;

@Getter
@Setter
@NoArgsConstructor
public class SignCloudRequest extends Dto {

    private String relyingParty;

    private String agreementUUID;

    private byte[] signingFileData;

    private String signingFileName;

    private String mimeType;

    private String authorizeCode;

    private int authorizeMethod;

    private int messagingMode;

    private boolean certificateRequired = false;

    private SignCloudMetaData signCloudMetaData;

    private CredentialData credentialData;

    @Override
    public String toString() {
        return "SignCloudRequest{" +
                "relyingParty='" + relyingParty + '\'' +
                ", agreementUUID='" + agreementUUID + '\'' +
                ", signingFileDataSize=" + (signingFileData == null ? "0" : signingFileData.length) +
                ", signingFileName='" + signingFileName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", authorizeCode='" + authorizeCode + '\'' +
                ", authorizeMethod=" + authorizeMethod +
                ", messagingMode=" + messagingMode +
                ", certificateRequired=" + certificateRequired +
                ", signCloudMetaData=" + signCloudMetaData +
                ", credentialData=" + credentialData +
                '}';
    }
}
