package test.java.com.booking.bookings.serializers.date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class DateStringConverterTest {
    private DateStringConverter dateStringConverter;

    @BeforeEach
    public void beforeEach() {
        dateStringConverter = new DateStringConverter();
    }

    @Test
    public void should_convert_date_correctly_to_string() {
        Date date = Date.valueOf("2020-01-01");

        String actualSerializedDate = dateStringConverter.convert(date);

        String expectedSerializedDate = "2020-01-01";
        assertThat(actualSerializedDate, is(equalTo(expectedSerializedDate)));
    }
}