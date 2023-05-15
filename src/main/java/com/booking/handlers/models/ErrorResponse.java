package main.java.com.booking.handlers.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class ErrorResponse {

    @JsonProperty
    @ApiModelProperty(name = "message", value = "Main error message", required = true, position = 1)
    private final String message;

    @JsonProperty
    @ApiModelProperty(name = "details", value = "Details of the error", required = true, position = 2)
    private final List<String> details;

    public ErrorResponse(String message, List<String> details) {
        this.message = message;
        this.details = details;
    }
}
