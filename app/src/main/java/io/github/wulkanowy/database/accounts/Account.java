package io.github.wulkanowy.database.accounts;


public class Account {

    private int id;

    private String name;

    private String email;

    private String password;

    private String symbol;

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

    public String getSymbol() {
        return symbol;
    }

    public Account setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }
}
