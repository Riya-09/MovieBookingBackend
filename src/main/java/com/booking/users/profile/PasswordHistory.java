package main.java.com.booking.users.profile;

import com.booking.users.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
@Table(name = "user_password_history")
public class PasswordHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty
    @NotBlank(message = "old password")
    @Column(nullable = false, name = "old_password")
    private String oldPassword = "";

    @JsonProperty
    @NotBlank(message = "older password")
    @Column(nullable = false, name = "older_password")
    private String olderPassword = "";

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public PasswordHistory(String oldPassword, String olderPassword) {
        this.oldPassword = oldPassword;
        this.olderPassword = olderPassword;
    }

    public PasswordHistory() {
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getOlderPassword() {
        return olderPassword;
    }

    public void setOlderPassword(String olderPassword) {
        this.olderPassword = olderPassword;
    }

    @Override
    public String toString() {
        return "PasswordHistory{" +
                "id=" + id +
                ", oldPassword='" + oldPassword + '\'' +
                ", olderPassword='" + olderPassword + '\'' +
                ", user=" + user +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordHistory that = (PasswordHistory) o;
        return Objects.equals(id, that.id) && Objects.equals(oldPassword, that.oldPassword) && Objects.equals(olderPassword, that.olderPassword) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, oldPassword, olderPassword, user);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}



