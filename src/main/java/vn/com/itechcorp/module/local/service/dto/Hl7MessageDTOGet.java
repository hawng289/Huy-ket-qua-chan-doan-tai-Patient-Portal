package vn.com.itechcorp.module.local.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.dto.DtoGet;
import vn.com.itechcorp.module.local.entity.Hl7Message;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class Hl7MessageDTOGet extends DtoGet<Hl7Message, Long> {

    private String hl7;

    private String messageID;

    private String messageType;

    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private Date messageTime;


    public Hl7MessageDTOGet(Hl7Message hl7Message) {
        super(hl7Message);
    }

    @Override
    public void parse(Hl7Message hl7Message) {
        this.messageID = hl7Message.getMessageID();
        this.hl7 = hl7Message.getHl7();
        this.messageType = hl7Message.getMessageType();
        this.messageTime = hl7Message.getMessageTime();
    }
}
