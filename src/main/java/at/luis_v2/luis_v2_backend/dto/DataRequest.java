package at.luis_v2.luis_v2_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.time.LocalDate;
import java.util.List;

@Data
public class DataRequest {

    @NotNull(message = "Station must not be blank")
    @Schema(example = "164", description = "Measurement station id")
    private Integer station;

    @NotEmpty(message = "At least one component must be provided")
    @Schema(example = "[\"125\", \"8\"]", description = "List of component ids to retrieve")
    private List<Integer> components;

    @NotNull(message = "Start date is required")
    @Schema(example = "2025-01-01", description = "Start date in ISO format (yyyy-MM-dd)")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Schema(example = "2025-01-31", description = "End date in ISO format (yyyy-MM-dd)")
    private LocalDate endDate;

    @NotBlank(message = "Interval must be specified")
    @Schema(example = "daily", description = "Interval of the data (e.g., half-hourly, hourly, daily)")
    private String interval;

    @NotNull
    @Schema(example = "false", description = "Should the data be interpolated?")
    private boolean interpolate;

    @NotBlank(message = "Format must be specified")
    @Schema(example = "csv", description = "Format of the exported data")
    private String fileFormat;

}
