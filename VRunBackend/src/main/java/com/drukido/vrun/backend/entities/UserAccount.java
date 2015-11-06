package com.drukido.vrun.backend.entities;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;

@Entity
public class UserAccount {

    /**
     * Unique identifier of this Entity in the database.
     */
    @Id
    private Long key;

    /**
     * The user first name.
     */
    private String firstName;

    /**
     * The user last name.
     */
    private String lastName;

    /**
     * The user email.
     */
    private String email;

    /**
     * The user's user name
     */
    private String userName;

    /**
     * The user's password;
     */
    private String password;

    /**
     * The user's phone number.
     */
    private String phoneNumber;

    /**
     * The time that the user signed up.
     */
    private Date joinTime;

    /**
     * Returns a boolean indicating if the user is an admin or not.
     * @param user to check.
     * @return the user authorization level.
     */
    public static boolean isAdmin(final User user) {
        return false;
    }

    /**
     * Returns the user first name.
     * @return the user first name
     */
    public final String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user first name.
     * @param pFirstName the first name to set for this user.
     */
    public final void setFirstName(final String pFirstName) {
        this.firstName = pFirstName;
    }

    /**
     * Returns the user last name.
     * @return the user last name.
     */
    public final String getLastName() {
        return lastName;
    }

    /**
     * Sets the user last name.
     * @param pLastName the user last name to set.
     */
    public final void setLastName(final String pLastName) {
        this.lastName = pLastName;
    }

    /**
     * Returns the user email.
     * @return the user email.
     */
    public final String getEmail() {
        return email;
    }

    /**
     * Sets the user email.
     * @param pEmail the user email to set.
     */
    public final void setEmail(final String pEmail) {
        this.email = pEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }
}
