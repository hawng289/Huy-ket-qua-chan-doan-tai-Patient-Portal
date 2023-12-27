package vn.com.itechcorp.ris.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModalityDTO extends Dto {

    private String id;

    private String code;

    private String name;

    private String modalityType;

    private String roomCode;

}