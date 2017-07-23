package io.github.wulkanowy.api.user;

public class AddressData {

    private String address;

    private String registeredAddress;

    private String correspondenceAddress;

    public AddressData setAddress(String address) {
        this.address = address;

        return this;
    }

    public AddressData setRegisteredAddress(String registeredAddress) {
        this.registeredAddress = registeredAddress;

        return this;
    }

    public AddressData setCorrespondenceAddress(String correspondenceAddress) {
        this.correspondenceAddress = correspondenceAddress;

        return this;
    }

    public String getAddress() {
        return address;
    }

    public String getRegisteredAddress() {
        return registeredAddress;
    }

    public String getCorrespondenceAddress() {
        return correspondenceAddress;
    }
}
