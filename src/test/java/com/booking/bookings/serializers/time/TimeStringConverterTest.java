package test.java.com.booking.bookings.serializers.time;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Time;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class TimeStringConverterTest {

    private TimeStringConverter timeStringConverter;

    @BeforeEach
    public void beforeEach() {
        timeStringConverter = new TimeStringConverter();
    }

    @Test
    public void should_convert_time_in_am() {
        Time time = Time.valueOf("06:30:00");

        String actualSerializedString = timeStringConverter.convert(time);

        String expectedSerializedString = "6:30 AM";
        assertThat(actualSerializedString, is(equalTo(expectedSerializedString)));
    }

    @Test
    public void should_convert_time_in_pm() {
        Time time = Time.valueOf("16:45:00");

        String actualSerializedString = timeStringConverter.convert(time);

        String expectedSerializedString = "4:45 PM";
        assertThat(actualSerializedString, is(equalTo(expectedSerializedString)));
    }
}