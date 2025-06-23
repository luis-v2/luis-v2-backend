package at.luis_v2.luis_v2_backend.repository;

import at.luis_v2.luis_v2_backend.dto.Forecast;
import at.luis_v2.luis_v2_backend.dto.ForecastId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ForecastRepository extends JpaRepository<Forecast, ForecastId> {

    List<Forecast> findByStationAndComponentAndTimestampBetween(
            String station, String component, LocalDateTime start, LocalDateTime end
    );
}

