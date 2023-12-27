package vn.com.itechcorp.hsm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.com.itechcorp.hsm.dto.*;
import vn.com.itechcorp.hsm.remote.CloudHsmProxy;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service("signService")
public class SignServiceImpl implements SignService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${cloud.hsm.config.relyingParty}")
    private String relyingParty;

    @Value("${cloud.hsm.config.relyingPartyPassword}")
    private String relyingPartyPassword;

    @Value("${cloud.hsm.config.relyingPartyKeyStorePassword}")
    private String relyingPartyKeyStorePassword;

    @Value("${cloud.hsm.config.keyPath}")
    private String keyPath;

    @Value("${cloud.hsm.config.keyAlias}")
    private String keyAlias;

    @Value("${cloud.hsm.config.relyingPartyUser}")
    private String relyingPartyUser;

    @Value("${cloud.hsm.config.relyingPartySignature}")
    private String relyingPartySignature;

    @Value("${cloud.hsm.config.authorizeCode}")
    private String authorizeCode;

    @Value("${cloud.hsm.config.enabled}")
    private boolean enabled;

    @Autowired
    private CloudHsmProxy cloudHsmProxy;

    @Value("${cloud.hsm.config.positionidentifier}")
    private String identifier;

    @Value("${cloud.hsm.config.rectangleoffset}")
    private String offset;

    @Value("${cloud.hsm.config.rectanglesize}")
    private String size;

    PrivateKey privateKeyEntry;

    @Override
    public byte[] sign(ITSignDTO request) {
        if (!enabled) return request.getFile();
        try {
            logger.info("[HSM-Sign][AGREEMENTID-{}] Request from RIS: {}", request.getAgreementID(), request);
            SignCloudRequest signCloudRequest = parseSignCloudRequest(request);
            logger.info("[HSM-Sign][AGREEMENTID-{}] Request to HSM server: {}", request.getAgreementID(), signCloudRequest);
            ResponseEntity<SignCloudResponse> response = cloudHsmProxy.sign(signCloudRequest);
            logger.info("[HSM-Sign][AGREEMENTID-{}] Response from HSM: {}", request.getAgreementID(), response);
            if (response != null && response.getBody() != null) {
                return response.getBody().getSignedFileData();
            }
            return null;
        } catch (Exception ex) {
            logger.error("[HSM-Sign][AGREEMENTID-{}] Fail to sign on HSM server: {}", request.getAgreementID(), ex.getMessage());
            return null;
        }
    }

    private CredentialData createCredential() throws Exception {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        FileInputStream inputStream = new FileInputStream(keyPath);
        ks.load(inputStream, relyingPartyKeyStorePassword.toCharArray());
        privateKeyEntry = (PrivateKey) ks.getKey(keyAlias, relyingPartyKeyStorePassword.toCharArray());

        CredentialData credentialData = new CredentialData();
        credentialData.setTimestamp(System.currentTimeMillis() + "");
        String data2sign = relyingPartyUser + relyingPartyPassword + relyingPartySignature + credentialData.getTimestamp();

        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(privateKeyEntry);
        signature.update(data2sign.getBytes(StandardCharsets.UTF_8));

        credentialData.setPkcs1Signature(Base64.getEncoder().encodeToString(signature.sign()));
        credentialData.setUsername(relyingPartyUser);
        credentialData.setPassword(relyingPartyPassword);
        credentialData.setSignature(relyingPartySignature);

        return credentialData;
    }

    private SignCloudMetaData createSignCloudMetaData() {
        SignCloudMetaData signCloudMetaData = new SignCloudMetaData();
        Map<String, String> singletonSigning = new HashMap<>();

        singletonSigning.put(SingletonSigningKey.PAGENO.name(), "Last");
        singletonSigning.put(SingletonSigningKey.POSITIONIDENTIFIER.name(), identifier);
        singletonSigning.put(SingletonSigningKey.RECTANGLEOFFSET.name(), offset);
        singletonSigning.put(SingletonSigningKey.RECTANGLESIZE.name(), size);

        singletonSigning.put(SingletonSigningKey.VISIBLESIGNATURE.name(), "True");
        singletonSigning.put(SingletonSigningKey.SHOWSIGNERINFO.name(), "True");
        singletonSigning.put(SingletonSigningKey.SIGNERINFOPREFIX.name(), "Ký bởi:");

        singletonSigning.put(SingletonSigningKey.DATETIMEFORMAT.name(), "dd-MM-yyyy HH:mm:ss");
        singletonSigning.put(SingletonSigningKey.SHOWDATETIME.name(), "True");
        singletonSigning.put(SingletonSigningKey.DATETIMEPREFIX.name(), "Ký ngày:");
        singletonSigning.put(SingletonSigningKey.SHOWREASON.name(), "True");

//        singletonSigning.put(SingletonSigningKey.SIGNREASONPREFIX.name(), "Lý do:");
//        singletonSigning.put(SingletonSigningKey.SIGNREASON.name(), "Đồng ý");

        singletonSigning.put(SingletonSigningKey.SHOWLOCATION.name(),"True");
        singletonSigning.put(SingletonSigningKey.LOCATIONPREFIX.name(),"Nơi ký:");
        singletonSigning.put(SingletonSigningKey.LOCATION.name(),"Bệnh viện Bình Định");

        signCloudMetaData.setSingletonSigning(singletonSigning);

        return signCloudMetaData;
    }

    private SignCloudRequest parseSignCloudRequest(ITSignDTO request) throws Exception {
        SignCloudRequest signCloudRequest = new SignCloudRequest();
        signCloudRequest.setAgreementUUID(request.getAgreementID());
        signCloudRequest.setAuthorizeCode(request.getSecret());
        signCloudRequest.setSigningFileData(request.getFile());

        signCloudRequest.setRelyingParty(relyingParty);
        signCloudRequest.setAuthorizeMethod(ESignCloudConstant.AUTHORISATION_METHOD_PASSCODE);
        signCloudRequest.setMessagingMode(ESignCloudConstant.SYNCHRONOUS);
        signCloudRequest.setCertificateRequired(true);

        signCloudRequest.setAuthorizeCode(request.getSecret() == null ? authorizeCode : request.getSecret());
        signCloudRequest.setMimeType(ESignCloudConstant.MIMETYPE_PDF);
        signCloudRequest.setSignCloudMetaData(createSignCloudMetaData());
        signCloudRequest.setCredentialData(createCredential());

        return signCloudRequest;
    }
}
