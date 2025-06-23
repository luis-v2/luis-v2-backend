package at.luis_v2.luis_v2_backend.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "forecasts")
@IdClass(ForecastId.class)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Forecast {

    @Id
    @Column(name = "fc_timestamp")
    private LocalDateTime timestamp;

    @Id
    @Column(name = "fc_station")
    private String station;

    @Id
    @Column(name = "fc_component")
    private String component;

    @Column(name = "fc_value")
    private String value;
}
