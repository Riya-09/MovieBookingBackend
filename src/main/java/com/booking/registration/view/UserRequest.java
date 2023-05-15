package main.java.com.booking.registration.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import static com.booking.registration.view.Constants.*;

public class UserRequest {
    @JsonProperty
    @ApiModelProperty(name = "name", value = "Name of costumer", required = true, position = 1)
    @Size(min=2, max=64)
    @Pattern(regexp=ONLY_ALPHABETS_REGEX, message="must contain alphabets")
    private String name;

    @JsonProperty
    @ApiModelProperty(name = "email", value = "Email of costumer", required = true, position = 2)
    @Email(regexp = EMAIL_REGEX)
    private String email;

    @JsonProperty
    @ApiModelProperty(name = "mobileNumber", value = "mobileNumber of costumer", required = true, position = 3)
    @Digits(integer = 10,fraction = 0,message = " is not valid")
    @Min(value = 2000000000, message = " is not valid")
    private Long mobileNumber;

    @JsonProperty
    @ApiModelProperty(name = "password", value = "password of costumer", required = true, position = 4)
    @Pattern(regexp=PASSWORD_REGEX, message="must contain maximum length of 64 characters,at least 8 characters, 1 number, 1 uppercase letter, 1 lowercase letter and 1 special character")
    private String  password;

    @JsonProperty
    @ApiModelProperty(name = "reenterPassword", value = "reenterPassword of costumer", required = true, position = 5)
    @Pattern(regexp=PASSWORD_REGEX, message="must contain maximum length of 64 characters,at least 8 characters, 1 number, 1 uppercase letter, 1 lowercase letter and 1 special character")
    private String reenterPassword;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(Long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getReenterPassword() {
        return reenterPassword;
    }

    public void setReenterPassword(String reenterPassword) {
        this.reenterPassword = reenterPassword;
    }

    @Override
    public String toString() {
        return "UserRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", mobileNumber=" + mobileNumber +
                ", password='" + password + '\'' +
                ", reenterPassword='" + reenterPassword + '\'' +
                '}';
    }
}
