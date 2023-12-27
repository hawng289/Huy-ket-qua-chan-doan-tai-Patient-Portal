package vn.com.itechcorp.ris.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileDTO extends Dto {

    private String filename;

    private String contentType;

    private byte[] data;

    @Override
    public String toString() {
        return "ITFileDTO{" +
                "filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", dataSize=" + (data == null ? 0 : data.length) +
                '}';
    }
}
