package com.peerlender.security.user.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class User {

    @Id
    @GeneratedValue
    private long id;
    @JoinColumn
    @OneToOne(cascade = CascadeType.ALL)
    private UserDetailsImpl userDetails;

    public User() {}

    public User(UserDetailsImpl userDetails) {
        this.userDetails = userDetails;
    }

    public long getId() {
        return id;
    }

    public UserDetailsImpl getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetailsImpl userDetails) {
        this.userDetails = userDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(userDetails, user.userDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userDetails);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userDetails=" + userDetails +
                '}';
    }
}
