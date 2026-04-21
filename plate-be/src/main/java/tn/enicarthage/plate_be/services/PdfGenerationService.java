package tn.enicarthage.plate_be.services;

import tn.enicarthage.plate_be.dtos.TicketResponseDTO;
import java.io.IOException;
import java.util.List;

public interface PdfGenerationService {
    byte[] generateDemandePrixPdf(List<TicketResponseDTO> tickets, int dpNumber) throws IOException;
}