package tn.enicarthage.plate_be.dtos.logs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class LogsPageResponse {
    private List<TraceLoginDTO> logs;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private boolean hasNext;
    private boolean hasPrevious;

    public LogsPageResponse(List<TraceLoginDTO> logs, int currentPage, int totalPages, long totalElements, boolean hasNext, boolean hasPrevious) {
        this.logs = logs;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

}

