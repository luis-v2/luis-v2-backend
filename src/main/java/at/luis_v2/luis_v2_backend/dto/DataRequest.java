package at.luis_v2.luis_v2_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.time.LocalDate;
import java.util.List;

@Data
public class DataRequest {

    @NotBlank(message = "Station must not be blank")
    private String station;

    @NotEmpty(message = "At least one component must be provided")
    private List<String> components;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotBlank(message = "Interval must be specified")
    private String interval;

    @NotNull
    private boolean interpolate;

    @NotBlank(message = "Format must be specified")
    private String fileFormat;

}
