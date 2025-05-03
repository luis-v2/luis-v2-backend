package at.luis_v2.luis_v2_backend.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataPoint {
    public Instant timestamp;
    public Double value;
}
