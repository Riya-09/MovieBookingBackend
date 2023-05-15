package main.java.com.booking.shows;

import com.booking.exceptions.NextSevenDayException;
import com.booking.exceptions.PreviousDateException;
import com.booking.movieGateway.MovieGateway;
import com.booking.movieGateway.exceptions.FormatException;
import com.booking.movieGateway.models.Movie;
import com.booking.registration.Roles;
import com.booking.shows.respository.Show;
import com.booking.shows.respository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class ShowService {
    private final ShowRepository showRepository;
    private final MovieGateway movieGateway;

    @Autowired
    public ShowService(ShowRepository showRepository, MovieGateway movieGateway) {
        this.showRepository = showRepository;
        this.movieGateway = movieGateway;
    }

    public List<Show> fetchAll(Date date, Roles role) throws PreviousDateException, NextSevenDayException {
        LocalDate currentDate = LocalDate.now();
        if ((role == null || role == Roles.CUSTOMER) && date.compareTo(Date.valueOf(currentDate)) < 0) {
            throw new PreviousDateException("You can view shows from " + Date.valueOf(currentDate));
        }
        LocalDate sevenDaysFromCurrentDate = currentDate.plusDays(6);
        if ((role == null || role == Roles.CUSTOMER) && date.compareTo(Date.valueOf(sevenDaysFromCurrentDate)) > 0) {
            throw new NextSevenDayException("You can view shows till " + Date.valueOf(sevenDaysFromCurrentDate));
        }
        return showRepository.findByDate(date);
    }

    public Movie getMovieById(String movieId) throws IOException, FormatException {
        return movieGateway.getMovieFromId(movieId);
    }

    public Show getShowByIdAndDate(Long id, Date date) {
        return showRepository.findByIdAndDate(id, date);
    }
}
