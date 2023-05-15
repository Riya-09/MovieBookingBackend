package main.java.com.booking.utilities.serializers.date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Date;

@Component
public class DateSerializer extends StdSerializer<Date> {

    private final DateStringConverter dateStringConverter;

    @Autowired
    public DateSerializer(DateStringConverter dateStringConverter) {
        this(null, dateStringConverter);
    }

    private DateSerializer(Class<Date> t, DateStringConverter dateStringConverter) {
        super(t);
        this.dateStringConverter = dateStringConverter;
    }

    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(dateStringConverter.convert(date));
    }
}
