package at.luis_v2.luis_v2_backend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataComponent {
    public Integer id;
    public String name = "";
    public String unit;
    public List<DataPoint> dataPoints;
}
