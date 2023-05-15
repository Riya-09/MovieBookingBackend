package main.java.com.booking.bookings.view;

import com.booking.customers.repository.Customer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.util.ArrayList;

import static com.booking.shows.respository.Constants.MAX_NO_OF_SEATS_PER_BOOKING;

public class BookingRequest {
    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(name = "date", value = "Date of booking (yyyy-MM-dd)", dataType = "java.lang.String", required = true, example = "2020-01-01", position = 1)
    private Date date;

    @JsonProperty
    @ApiModelProperty(name = "showId", value = "The show id", required = true, position = 2)
    private Long showId;

    @JsonProperty
    @ApiModelProperty(name = "customer", value = "Customer requesting booking", required = true, position = 3)
    private Customer customer;

    @JsonProperty
    @DecimalMax(value = MAX_NO_OF_SEATS_PER_BOOKING, message = "Maximum {value} seats allowed per booking")
    @ApiModelProperty(name = "no of seats", value = "Number of seats requested to be booked", example = "3", required = true, position = 4)
    private int noOfSeats;

    @JsonProperty
    @Size(min = 1, max = 15, message = "Maximum 15 seats allowed per booking")
    @ApiModelProperty(name = "bookCases", value = "List of Seats requested to be booked", example = "[A1,A2,A3]", required = true, position = 5)
    private ArrayList<String> bookCases;


    public Date getDate() {
        return date;
    }

    public Long getShowId() {
        return showId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getNoOfSeats() {
        return noOfSeats;
    }

    public ArrayList<String> getBookcases() {
        return bookCases;
    }

    public BookingRequest() {
    }

    public BookingRequest(Date date, Long showId, Customer customer, int noOfSeats, ArrayList<String> bookcases) {
        this.date = date;
        this.showId = showId;
        this.customer = customer;
        this.noOfSeats = noOfSeats;
        this.bookCases = bookcases;
    }

    @Override
    public String toString() {
        return "BookingRequest{" +
                "date=" + date +
                ", showId=" + showId +
                ", customer=" + customer +
                ", noOfSeats=" + noOfSeats +
                ", bookCases=" + bookCases +
                '}';
    }
}
