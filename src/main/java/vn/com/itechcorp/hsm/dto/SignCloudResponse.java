package vn.com.itechcorp.hsm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.ris.dto.Dto;

@Getter
@Setter
@NoArgsConstructor
public class SignCloudResponse extends Dto {

    private long cloudCertificateOwnerID;

    private long cloudCertificateID;

    private int responseCode;

    private String responseMessage;

    private String billCode;

    private long timestamp;

    private int logInstance;

    private int remainingCounter;

    private byte[] signedFileData;

    private String signedFileName;

    private String mimeType;

    private int authorizeMethod;

    private int certificateStateID;

    private int credentialExpireIn;

    private int signingCounter;

    @Override
    public String toString() {
        return "SignCloudResponse{" +
                "cloudCertificateOwnerID=" + cloudCertificateOwnerID +
                ", cloudCertificateID=" + cloudCertificateID +
                ", responseCode=" + responseCode +
                ", responseMessage='" + responseMessage + '\'' +
                ", billCode='" + billCode + '\'' +
                ", timestamp=" + timestamp +
                ", logInstance=" + logInstance +
                ", remainingCounter=" + remainingCounter +
                ", signedFileDataSize=" + (signedFileData == null ? '0' : signedFileData.length) +
                ", signedFileName='" + signedFileName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", authorizeMethod=" + authorizeMethod +
                ", certificateStateID=" + certificateStateID +
                ", credentialExpireIn=" + credentialExpireIn +
                ", signingCounter=" + signingCounter +
                '}';
    }
}
