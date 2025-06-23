package at.luis_v2.luis_v2_backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
public class ForecastId implements Serializable {

    private LocalDateTime timestamp;
    private String station;
    private String component;
}

