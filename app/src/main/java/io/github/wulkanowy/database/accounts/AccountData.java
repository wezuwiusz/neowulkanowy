package io.github.wulkanowy.database.accounts;


public class AccountData {

    private int id;

    private String name;

    private String email;

    private String password;

    private String county;

    public int getId() {
        return id;
    }

    public AccountData setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public AccountData setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public AccountData setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public AccountData setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getCounty() {
        return county;
    }

    public AccountData setCounty(String county) {
        this.county = county;
        return this;
    }
}
