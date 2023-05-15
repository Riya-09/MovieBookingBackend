package main.java.com.booking.registration;

import com.booking.exceptions.PasswordMisMatchException;
import com.booking.exceptions.UserAlreadyRegisteredException;
import com.booking.handlers.models.ErrorResponse;
import com.booking.users.User;
import com.booking.registration.view.UserRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Registration")
@RestController
@RequestMapping("/register")
public class UserRegistrationController {

    private final UserRegistrationService userRegistrationService;

    public UserRegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }
    @PostMapping(name = "/register")
    @ApiOperation(value = "Register user")
    @ResponseStatus(code = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User registered successfully"),
            @ApiResponse(code = 404, message = "Record not found", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Server cannot process request due to client error", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Something failed in the server", response = ErrorResponse.class)
    })
    public User book(@Valid @RequestBody UserRequest userRequest) throws UserAlreadyRegisteredException, PasswordMisMatchException {
        return userRegistrationService.create(userRequest.getName(), userRequest.getEmail(), userRequest.getMobileNumber(), userRequest.getPassword(), userRequest.getReenterPassword());
    }
}
