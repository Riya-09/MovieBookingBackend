package main.java.com.booking.users;

import com.booking.registration.Roles;
import com.booking.users.profile.PasswordHistory;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
@Table(name = "usertable")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty
    @NotBlank(message = "User name must be provided")
    @Column(nullable = false, unique = true)
    @ApiModelProperty(name = "username", value = "Name of user (must be unique)", required = true, example = "user_name", position = 1)
    private String username;

    @JsonIgnore
    @NotBlank(message = "Password name must be provided")
    @Column(nullable = false)
    @ApiModelProperty(name = "password", value = "Password of the user", required = true, example = "password", position = 2)
    private String password;

    @JsonProperty
    @NotBlank(message = "Email must be provided")
    @Column(nullable = false, unique = true)
    @ApiModelProperty(name = "email", value = "Email of the user", required = true, example = "abc@gamil.com", position = 3)
    private String email;

    @JsonProperty
    @Column(nullable = false)
    @ApiModelProperty(name = "mobileNumber", value = "Mobile number of the user", required = true, example = "1234567890", position = 4)
    private Long mobileNumber;

    @JsonProperty
    @ApiModelProperty(name = "role", value = "Role of the user", example = "CUSTOMER", position = 5)
    @Enumerated(EnumType.STRING)
    private Roles role;

    @JsonBackReference
    @OneToOne(orphanRemoval = true,mappedBy = "user",fetch = FetchType.LAZY)
    private PasswordHistory passwordHistory;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User(String username, String password, String email, Long mobileNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.mobileNumber = mobileNumber;
    }

    public User(String username, String password, String email, Long mobileNumber, Roles role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.role = role;
    }

    public Long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(Long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", mobileNumber=" + mobileNumber +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username) && password.equals(user.password) && email.equals(user.email) && mobileNumber.equals(user.mobileNumber) && role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email, mobileNumber, role);
    }

    public PasswordHistory getPasswordHistory() {
        return passwordHistory;
    }

    public void setPasswordHistory(PasswordHistory passwordHistory) {
        this.passwordHistory = passwordHistory;
    }
}
