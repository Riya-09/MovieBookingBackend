package main.java.com.booking.shows.view.models;

import com.booking.movieGateway.models.Movie;
import com.booking.shows.respository.Show;
import com.booking.slots.repository.Slot;
import com.booking.utilities.serializers.date.DateSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.sql.Date;

@ApiModel("Show Response")
public class ShowResponse {
    @JsonProperty
    @ApiModelProperty(name = "id", value = "The show id", example = "0", position = 1)
    private Long id;
    @JsonProperty
    @JsonSerialize(using = DateSerializer.class)
    @ApiModelProperty(name = "date", value = "Date of the show (yyyy-MM-dd)", dataType = "java.lang.String", required = true, example = "2020-01-01", position = 2)
    private Date date;
    @JsonProperty
    @ApiModelProperty(name = "cost", value = "Cost of the show", required = true, example = "249.99", position = 3)
    private BigDecimal cost;
    @JsonProperty
    @ApiModelProperty(required = true, position = 4)
    private final Movie movie;
    @JsonProperty
    @ApiModelProperty(required = true, position = 5)
    private final Slot slot;

    @JsonProperty
    @ApiModelProperty(required = true, position = 6)
    private int noOfBookedSeats = 0;


    public ShowResponse(Movie movie, Slot slot, Show show, Integer noOfBookedSeats) {
        this.movie = movie;
        this.slot = slot;
        this.id = show.getId();
        this.date = show.getDate();
        this.cost = show.getCost();
        this.noOfBookedSeats = noOfBookedSeats;

    }

    public int getNoOfBookedSeats() {
        return noOfBookedSeats;
    }
}
