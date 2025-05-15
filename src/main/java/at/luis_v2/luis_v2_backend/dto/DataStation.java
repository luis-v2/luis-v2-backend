package at.luis_v2.luis_v2_backend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataStation {
    private int id;
    private String name;
    private List<DataComponent> components;
}
