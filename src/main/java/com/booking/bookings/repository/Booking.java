package main.java.com.booking.bookings.repository;

import com.booking.bookings.view.BookingConfirmationResponse;
import com.booking.customers.repository.Customer;
import com.booking.shows.respository.Show;
import com.booking.utilities.serializers.date.DateSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    @ApiModelProperty(name = "id", value = "The booking id", example = "0", position = 1)
    private Long id;

    @Column(nullable = false)
    @JsonProperty
    @JsonSerialize(using = DateSerializer.class)
    @NotNull(message = "Date must be provided")
    @ApiModelProperty(name = "date", value = "Date of booking (yyyy-MM-dd)", dataType = "java.lang.String", required = true, example = "2020-01-01", position = 2)
    private Date date;

    @OneToOne
    @JoinColumn(name = "show_id")
    @JsonIgnore
    @NotNull(message = "Show must be provided")
    private Show show;

    @OneToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    @NotNull(message = "Customer must be provided")
    private Customer customer;

    @Column(name = "no_of_seats", nullable = false)
    @JsonProperty
    @DecimalMin(value = "0", inclusive = false, message = "Seats should be greater than {value}")
    @NotNull(message = "No of seats must be provided")
    @ApiModelProperty(name = "no of seats", value = "Number of seats/tickets for show", required = true, example = "3", position = 3)
    private Integer noOfSeats;

    @Column(name = "amount_paid", nullable = false)
    @JsonProperty
    @DecimalMin(value = "0", inclusive = false, message = "Amount paid should be greater than {value}")
    @NotNull(message = "Amount paid must be provided")
    @ApiModelProperty(name = "amount paid", value = "Amount paid for seats", required = true, example = "300.44", position = 4)
    private BigDecimal amountPaid;

    @Column(name = "bookcases")
    @JsonProperty
    @NotNull(message = "List of seats requested to be booked must be provided")
    @ApiModelProperty(name = "book_Seats", value = "Seats to be booked", required = true, example = "{A1,A2,A3}", position = 5)
    private String bookcases;

    public Booking(Date date, Show show, Customer customer, Integer noOfSeats, BigDecimal amountPaid) {
        this.date = date;
        this.show = show;
        this.customer = customer;
        this.noOfSeats = noOfSeats;
        this.amountPaid = amountPaid;
    }

    public Booking(Date date, Show show, Customer customer, Integer noOfSeats, BigDecimal amountPaid, String bookcases) {
        this.date = date;
        this.show = show;
        this.customer = customer;
        this.noOfSeats = noOfSeats;
        this.amountPaid = amountPaid;
        this.bookcases = bookcases;
    }

    public Booking() {
    }

    public BookingConfirmationResponse constructBookingConfirmation() {
        ArrayList<String> bookedSeats = new ArrayList<>(Arrays.asList(bookcases.split(",")));
        return new BookingConfirmationResponse(
                id,
                customer.getName(),
                date,
                show.getSlot().getStartTime(),
                amountPaid,
                noOfSeats,
                bookedSeats
        );
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Show getShow() {
        return show;
    }

    public Date getDate() {
        return date;
    }

    public Integer getNoOfSeats() {
        return noOfSeats;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public String getBookcases() {
        return bookcases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id) &&
                Objects.equals(date, booking.date) &&
                Objects.equals(show, booking.show) &&
                Objects.equals(customer, booking.customer) &&
                Objects.equals(noOfSeats, booking.noOfSeats) &&
                Objects.equals(amountPaid, booking.amountPaid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, show, customer, noOfSeats, amountPaid);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", date=" + date +
                ", show=" + show +
                ", customer=" + customer +
                ", noOfSeats=" + noOfSeats +
                ", amountPaid=" + amountPaid +
                ", bookcases="  + bookcases +
                '}';
    }
}
