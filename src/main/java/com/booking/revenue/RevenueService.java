package main.java.com.booking.revenue;

import com.booking.bookings.repository.BookingRepository;
import com.booking.shows.respository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;

@Service
public class RevenueService {

    private final ShowRepository showRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public RevenueService(ShowRepository showRepository, BookingRepository bookingRepository) {
        this.showRepository = showRepository;
        this.bookingRepository = bookingRepository;
    }

    public BigDecimal revenueByDate(Date date) {
        final var shows = showRepository.findByDate(date);
        if(shows.size() == 0)
            return BigDecimal.valueOf(0);
        final var revenue = bookingRepository.bookingAmountByShows(shows);
        return revenue == null ? BigDecimal.valueOf(0) : revenue;
    }
}
