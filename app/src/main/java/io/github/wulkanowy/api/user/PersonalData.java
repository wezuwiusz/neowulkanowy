package io.github.wulkanowy.api.user;

public class PersonalData {

    private String names;

    private String dateAndBirthPlace;

    private String pesel;

    private String gender;

    private String isPolishCitizenship;

    private String familyName;

    private String parentsNames;

    public PersonalData setNames(String names) {
        this.names = names;

        return this;
    }

    public PersonalData setDateAndBirthPlace(String dateAndBirthPlace) {
        this.dateAndBirthPlace = dateAndBirthPlace;

        return this;
    }

    public PersonalData setPesel(String pesel) {
        this.pesel = pesel;

        return this;
    }

    public PersonalData setGender(String gender) {
        this.gender = gender;

        return this;
    }

    public PersonalData setPolishCitizenship(String polishCitizenship) {
        isPolishCitizenship = polishCitizenship;

        return this;
    }

    public PersonalData setFamilyName(String familyName) {
        this.familyName = familyName;

        return this;
    }

    public PersonalData setParentsNames(String parentsNames) {
        this.parentsNames = parentsNames;

        return this;
    }

    public String getNames() {
        return names;
    }

    public String getFirstName() {
        String[] name = names.split(" ");

        return name[0];
    }

    public String getSurname() {
        String[] name = names.split(" ");

        return name[name.length - 1];
    }

    public String getFirstAndLastName() {
        return getFirstName() + " " + getSurname();
    }

    public String getDateAndBirthPlace() {
        return dateAndBirthPlace;
    }

    public String getPesel() {
        return pesel;
    }

    public String getGender() {
        return gender;
    }

    public boolean isPolishCitizenship() {
        return isPolishCitizenship.equals("Tak");
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getParentsNames() {
        return parentsNames;
    }
}
