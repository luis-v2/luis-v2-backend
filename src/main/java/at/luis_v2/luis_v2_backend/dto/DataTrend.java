package at.luis_v2.luis_v2_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataTrend {
    private DataComponent component;
    private double previousValue;
    private double currentValue;
}
