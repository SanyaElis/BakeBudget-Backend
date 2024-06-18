package cs.vsu.ru.tpbakebudget.dto.request.orders;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportRequestDTO {

    @Schema(description = "Start date of order creation period", example = "2024-05-01", pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startCreatedAt;

    @Schema(description = "Ebd date of order creation period", example = "2024-05-02", pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endCreatedAt;

    @Schema(description = "Start date of finishing order period", example = "2024-05-03", pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startFinishedAt;

    @Schema(description = "End date of order finishing order period", example = "2024-05-04", pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endFinishedAt;
}
