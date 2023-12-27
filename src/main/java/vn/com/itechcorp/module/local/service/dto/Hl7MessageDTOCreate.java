package vn.com.itechcorp.module.local.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.SerialIDDtoCreate;
import vn.com.itechcorp.module.local.entity.Hl7Message;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Hl7MessageDTOCreate extends SerialIDDtoCreate<Hl7Message> {

    private String hl7;

    private String messageType;

    private String messageID;

    public Hl7MessageDTOCreate(String hl7, String messageType, String messageID) {
        this.hl7 = hl7;
        this.messageType = messageType;
        this.messageID = messageID;
    }

    @Override
    public Hl7Message toEntry() {
        Hl7Message hl7Message = new Hl7Message();
        hl7Message.setMessageID(this.messageID);
        hl7Message.setHl7(this.hl7);
        hl7Message.setMessageType(this.messageType);
        hl7Message.setMessageTime(new Date());
        return hl7Message;
    }
}
