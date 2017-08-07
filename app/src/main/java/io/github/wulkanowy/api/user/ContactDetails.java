package io.github.wulkanowy.api.user;

public class ContactDetails {

    private String phoneNumber = "";

    private String cellPhoneNumber = "";

    private String email = "";

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public ContactDetails setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public ContactDetails setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ContactDetails setEmail(String email) {
        this.email = email;
        return this;
    }
}
