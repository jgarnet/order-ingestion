package org.myshop.order;

import org.apache.commons.lang3.StringUtils;
import org.myshop.exception.ValidationException;

public class Customer {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String emailAddress;

    public Customer() {}

    public Customer(String firstName, String lastName, String phoneNumber, String emailAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    public void validate() throws ValidationException {
        if (StringUtils.isBlank(this.firstName)) {
            throw new ValidationException("First name is missing.");
        }
        if (StringUtils.isBlank(this.lastName)) {
            throw new ValidationException("Last name is missing.");
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public Customer setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Customer setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Customer setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public Customer setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }
}
