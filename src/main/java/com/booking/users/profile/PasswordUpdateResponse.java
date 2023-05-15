package main.java.com.booking.users.profile;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PasswordUpdateResponse {

    @JsonProperty
    private boolean successful = false;
    @JsonProperty
    private String message;

    public PasswordUpdateResponse() {
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
