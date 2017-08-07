package io.github.wulkanowy.api.user;

public class FamilyMember {

    private String name = "";

    private String kinship = "";

    private String address = "";

    private String telephones = "";

    private String email = "";

    public String getName() {
        return name;
    }

    public FamilyMember setName(String name) {
        this.name = name;
        return this;
    }

    public String getKinship() {
        return kinship;
    }

    public FamilyMember setKinship(String kinship) {
        this.kinship = kinship;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public FamilyMember setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getTelephones() {
        return telephones;
    }

    public FamilyMember setTelephones(String telephones) {
        this.telephones = telephones;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public FamilyMember setEmail(String email) {
        this.email = email;
        return this;
    }
}
