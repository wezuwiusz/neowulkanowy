package io.github.wulkanowy.api.school;

public class SchoolData {

    private String name = "";
    private String address = "";
    private String phoneNumber = "";
    private String headmaster = "";
    private String[] pedagogue;

    public String getName() {
        return name;
    }

    public SchoolData setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public SchoolData setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public SchoolData setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getHeadmaster() {
        return headmaster;
    }

    public SchoolData setHeadmaster(String headmaster) {
        this.headmaster = headmaster;
        return this;
    }

    public String[] getPedagogues() {
        return pedagogue;
    }

    public SchoolData setPedagogue(String[] pedagogue) {
        this.pedagogue = pedagogue;
        return this;
    }
}
