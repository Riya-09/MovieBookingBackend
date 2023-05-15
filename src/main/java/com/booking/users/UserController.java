package main.java.com.booking.users;

import com.booking.exceptions.IncorrectCurrentPasswordException;
import com.booking.exceptions.SamePasswordException;
import com.booking.handlers.models.ErrorResponse;
import com.booking.users.profile.PasswordUpdateRequest;
import com.booking.users.profile.PasswordUpdateResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "Users")
@RestController
public class UserController {

    private final UserPrincipalService userPrincipalService;

    @Autowired
    public UserController(UserPrincipalService userPrincipalService) {
        this.userPrincipalService = userPrincipalService;
    }

    @GetMapping("/login")
    Map<String, Object> login(Principal principal) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userPrincipal.getUser();
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("email", user.getEmail());
        userDetails.put("username", user.getUsername());
        userDetails.put("role", user.getRole());
        return userDetails;
    }


    @PutMapping(value = "/profile/user", consumes = "application/json")
    @ApiOperation(value = "Update Password")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Current password is not correct/password can't be same as last 3 passwords", response = ErrorResponse.class),
    })
    PasswordUpdateResponse updateUserProfile(@Valid @RequestBody PasswordUpdateRequest updateRequest) throws IncorrectCurrentPasswordException, SamePasswordException {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userPrincipal.getUser();
        userPrincipalService.updateUserPassword(user.getEmail(), updateRequest.getCurrentPassword(), updateRequest.getNewPassword());

        PasswordUpdateResponse passwordUpdateResponse = new PasswordUpdateResponse();
        passwordUpdateResponse.setSuccessful(true);
        passwordUpdateResponse.setMessage("password reset successfully");
        return passwordUpdateResponse;
    }

    @GetMapping("/user")
    @ApiOperation(value = "Get user details")
    @ResponseStatus(code = HttpStatus.OK)
    User getUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String emailId = userPrincipal.getUser().getEmail();
        User user = userPrincipalService.findUserByEmail(emailId);
        return user;
    }

}
