package vn.com.itechcorp.ris.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO extends Dto {

    private Long id;

    private String code;

    private String name;


    public UserDTO(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
