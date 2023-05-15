package main.java.com.booking.movieGateway.models;

import com.booking.utilities.serializers.duration.DurationSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.Duration;
import java.util.Objects;

@ApiModel(value = "Movie")
public class Movie {

    @JsonProperty
    @ApiModelProperty(name = "id", value = "The movie id", example = "title_1", position = 1)
    private final String id;

    @JsonProperty
    @ApiModelProperty(name = "name", value = "Name of the movie", required = true, example = "Movie", position = 2)
    private final String name;

    @JsonProperty
    @JsonSerialize(using = DurationSerializer.class)
    @ApiModelProperty(name = "name", dataType = "java.lang.String", value = "Duration of the movie", required = true, example = "1h 30m", position = 3)
    private final Duration duration;

    @JsonProperty
    @ApiModelProperty(name = "description", value = "Description of the movie", required = true, example = "Movie Description", position = 4)
    private final String plot;

    @JsonProperty
    @ApiModelProperty(name = "imdbRating", value = "IMDB rating of the movie", required = true, example = "7.5", position = 5)
    private final String imdbRating;

    @JsonProperty
    @ApiModelProperty(name = "genre", value = "Movie genre", required = true, example = "Drama, Horror, Sci-Fi", position = 6)
    private final String genre;

    @JsonProperty
    @ApiModelProperty(name = "poster", value = "Poster URL", required = true, example = "Movie Image", position = 7)
    private final String poster;

    @JsonProperty
    @ApiModelProperty(name = "rated", value = "Movie Certificate Rating", required = true, example = "UA", position = 8)
    private final String rated;

    public Movie(String id, String name, Duration duration, String plot, String imdbRating, String genre, String poster, String rated) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.plot = plot;
        this.imdbRating = imdbRating;
        this.genre = genre;
        this.poster = poster;
        this.rated = rated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return id.equals(movie.id) &&
                name.equals(movie.name) &&
                duration.equals(movie.duration) &&
                plot.equals(movie.plot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, duration, plot);
    }
}
