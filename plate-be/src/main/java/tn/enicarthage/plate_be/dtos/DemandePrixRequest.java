package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandePrixRequest {
    private Date deadline;
    private String message;
}
