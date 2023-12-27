package vn.com.itechcorp.util;

import org.junit.jupiter.api.Test;
import vn.com.itechcorp.ris.dto.ConsumableDTO;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextUtilTest {
    List<ConsumableDTO> consumableDTOS = new ArrayList<>();

    @Test
    void listToString() {
        ConsumableDTO consumableDTO = new ConsumableDTO();
        consumableDTO.setCode("Code 1");
        consumableDTO.setName("Name 1");
        consumableDTO.setQuantity(1);
        consumableDTO.setType("Type 1");

        ConsumableDTO consumableDTO1 = new ConsumableDTO();
        consumableDTO1.setCode("Code 2");
        consumableDTO1.setName("Name 2");
        consumableDTO1.setQuantity(2);
        consumableDTO1.setType("Type 2");
        consumableDTOS.add(consumableDTO);
        consumableDTOS.add(consumableDTO1);


        System.out.println("sad");

    }
}