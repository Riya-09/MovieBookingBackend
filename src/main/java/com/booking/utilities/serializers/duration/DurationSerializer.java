package main.java.com.booking.utilities.serializers.duration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class DurationSerializer extends StdSerializer<Duration> {

    private final DurationStringConverter durationStringConverter;

    @Autowired
    public DurationSerializer(DurationStringConverter durationStringConverter) {
        this(null, durationStringConverter);
    }

    private DurationSerializer(Class<Duration> t, DurationStringConverter durationStringConverter) {
        super(t);
        this.durationStringConverter = durationStringConverter;
    }

    @Override
    public void serialize(Duration duration, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(durationStringConverter.convert(duration));
    }
}
