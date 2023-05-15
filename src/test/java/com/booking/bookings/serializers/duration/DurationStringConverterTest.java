package test.java.com.booking.bookings.serializers.duration;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class DurationStringConverterTest {

    @Test
    public void should_convert_a_duration_to_a_string_in_hours_and_minutes() {
        Duration duration = Duration.ofHours(1).plusMinutes(30);
        DurationStringConverter durationStringConverter = new DurationStringConverter();

        String actualConvertedDuration = durationStringConverter.convert(duration);

        String expectedConvertedDuration = "1h 30m";
        assertThat(actualConvertedDuration, is(equalTo(expectedConvertedDuration)));
    }

    @Test
    public void should_convert_a_duration_to_a_string_ignoring_the_seconds_part() {
        Duration duration = Duration.ofHours(1).plusMinutes(45).plusSeconds(43);
        DurationStringConverter durationStringConverter = new DurationStringConverter();

        String actualConvertedDuration = durationStringConverter.convert(duration);

        String expectedConvertedDuration = "1h 45m";
        assertThat(actualConvertedDuration, is(equalTo(expectedConvertedDuration)));
    }
}
