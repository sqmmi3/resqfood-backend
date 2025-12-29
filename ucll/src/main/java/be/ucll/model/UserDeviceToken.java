package be.ucll.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_device_tokens", schema = "resqfood")
public class UserDeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false, length = 512, unique = true)
    private String token;

    @Column(name = "device_name")
    private String deviceName;

    protected UserDeviceToken() {
    }

    public UserDeviceToken(User user, String token, String deviceName) {
        setUser(user);
        setToken(token);
        setDeviceName(deviceName);
    }

    // Getters
    public Long getId() {
        return this.id;
    }

    public User getUser() {
        return this.user;
    }

    public String getToken() {
        return this.token;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    // Setters
    public void setId(Long newId) {
        this.id = newId;
    }

    public void setUser(User newUser) {
        this.user = newUser;
    }

    public void setToken(String newToken) {
        this.token = newToken;
    }

    public void setDeviceName(String newDeviceName) {
        this.deviceName = newDeviceName;
    }

    @Override
    public String toString() {
        return "UserDeviceToken{id=" + id + ", user=" + user.getUsername() + ", token=" + token + ", deviceName="
                + deviceName + "}";
    }
}
