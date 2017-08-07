package io.github.wulkanowy.api.user;

public class PersonalData {

    private String name = "";

    private String firstName = "";

    private String surname = "";

    private String firstAndLastName = "";

    private String dateAndBirthPlace = "";

    private String pesel = "";

    private String gender = "";

    private boolean isPolishCitizenship;

    private String familyName = "";

    private String parentsNames = "";

    public String getName() {
        return name;
    }

    public PersonalData setName(String name) {
        this.name = name;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public PersonalData setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getSurname() {
        return surname;
    }

    public PersonalData setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public String getFirstAndLastName() {
        return firstAndLastName;
    }

    public PersonalData setFirstAndLastName(String firstAndLastName) {
        this.firstAndLastName = firstAndLastName;
        return this;
    }

    public String getDateAndBirthPlace() {
        return dateAndBirthPlace;
    }

    public PersonalData setDateAndBirthPlace(String dateAndBirthPlace) {
        this.dateAndBirthPlace = dateAndBirthPlace;
        return this;
    }

    public String getPesel() {
        return pesel;
    }

    public PersonalData setPesel(String pesel) {
        this.pesel = pesel;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public PersonalData setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public boolean isPolishCitizenship() {
        return isPolishCitizenship;
    }

    public PersonalData setPolishCitizenship(boolean polishCitizenship) {
        isPolishCitizenship = polishCitizenship;
        return this;
    }

    public String getFamilyName() {
        return familyName;
    }

    public PersonalData setFamilyName(String familyName) {
        this.familyName = familyName;
        return this;
    }

    public String getParentsNames() {
        return parentsNames;
    }

    public PersonalData setParentsNames(String parentsNames) {
        this.parentsNames = parentsNames;
        return this;
    }
}
