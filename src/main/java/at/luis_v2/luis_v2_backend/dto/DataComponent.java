package at.luis_v2.luis_v2_backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class DataComponent {
    public Integer id;
    public String name;
    public String unit;
    public List<DataPoint> dataPoints;
}
