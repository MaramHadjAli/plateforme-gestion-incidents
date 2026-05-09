package tn.enicarthage.plate_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoomHeatmapEntry {
    private String idSalle;
    private String nomSalle;
    private String batiment;
    private long incidentCount;
}

