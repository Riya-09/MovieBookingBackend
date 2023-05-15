package main.java.com.booking.utilities.serializers.time;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Time;

@Component
public class TimeSerializer extends StdSerializer<Time> {

    private final TimeStringConverter timeStringConverter;

    @Autowired
    public TimeSerializer(TimeStringConverter timeStringConverter) {
        this(null, timeStringConverter);
    }

    private TimeSerializer(Class<Time> t, TimeStringConverter timeStringConverter) {
        super(t);
        this.timeStringConverter = timeStringConverter;
    }

    @Override
    public void serialize(Time time, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(timeStringConverter.convert(time));
    }
}
