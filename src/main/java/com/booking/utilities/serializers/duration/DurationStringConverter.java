package main.java.com.booking.utilities.serializers.duration;

import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class DurationStringConverter {

    public String convert(Duration duration) {
        int hoursPart = duration.toHoursPart();
        int minutesPart = duration.toMinutesPart();
        return hoursPart + "h " + minutesPart + "m";
    }
}
