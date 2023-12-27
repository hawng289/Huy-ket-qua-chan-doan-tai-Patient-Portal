package vn.com.itechcorp.module.portal.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.Dto;
import vn.com.itechcorp.ris.dto.UserDTO;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PortalUser extends Dto {

    private Long id;

    private String code;

    private String name;

    private String title;

    public PortalUser(UserDTO object) {
        this.id = object.getId();
        this.code = object.getCode();
        this.name = object.getName();
    }

}