package vn.com.itechcorp.hsm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.ris.dto.Dto;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class SignCloudMetaData extends Dto {

    private Map<String, String> singletonSigning;

    private Map<String, String> counterSigning;
}
