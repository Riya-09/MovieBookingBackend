package main.java.com.booking.slots.repository;

import com.booking.utilities.serializers.time.TimeSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Time;

@Entity
@Table(name = "slot")
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    @ApiModelProperty(name = "id", value = "The slot id", example = "0", position = 1)
    private Integer id;

    @Column(nullable = false)
    @JsonProperty
    @NotBlank(message = "Slot name must be provided")
    @ApiModelProperty(name = "slot name", value = "Name of the slot", required = true, example = "Slot name", position = 2)
    private String name;

    @Column(nullable = false)
    @JsonProperty
    @JsonSerialize(using = TimeSerializer.class)
    @NotNull(message = "Start time must be provided")
    @ApiModelProperty(name = "start time", value = "Start time of the slot", dataType = "java.lang.String", required = true, example = "13:30", position = 3)
    private Time startTime;

    @Column(nullable = false)
    @JsonProperty
    @JsonSerialize(using = TimeSerializer.class)
    @NotNull(message = "End time must be provided")
    @ApiModelProperty(name = "end time", value = "End time of the slot", dataType = "java.lang.String", required = true, example = "16:00", position = 4)
    private Time endTime;

    public Slot() {
    }

    public Slot(String name, Time startTime, Time endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Integer getId() {
        return id;
    }

    public Time getStartTime() {
        return startTime;
    }
}
