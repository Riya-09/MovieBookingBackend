package main.java.com.booking.users.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

public class PasswordUpdateRequest {

    @JsonProperty
    @NotBlank(message = "Current password must be provided")
    @ApiModelProperty(name = "currentPassword", value = "Current password of the user", required = true, example = "password", position = 1)
    private String currentPassword;

    @JsonProperty
    @NotBlank(message = "New password must be provided")
    @ApiModelProperty(name = "newPassword", value = "New password of the user", required = true, example = "newPassword", position = 2)
    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}




