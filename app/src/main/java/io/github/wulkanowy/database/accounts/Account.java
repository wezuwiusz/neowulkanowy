package io.github.wulkanowy.database.accounts;


public class Account {

    private int id;

    private String name;

    private String email;

    private String password;

    private String county;

    public int getId() {
        return id;
    }

    public Account setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Account setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Account setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Account setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getCounty() {
        return county;
    }

    public Account setCounty(String county) {
        this.county = county;
        return this;
    }
}
