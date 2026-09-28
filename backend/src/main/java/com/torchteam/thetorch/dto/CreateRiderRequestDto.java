package com.torchteam.thetorch.dto;

public class CreateRiderRequestDto {
    private String name;
    private int age;
    private String gender;
    private String tuidToken;

    // Standard Constructors
    public CreateRiderRequestDto() {}

    public CreateRiderRequestDto(String name, int age, String gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getTuidToken() { return tuidToken; }
    public void setTuidToken(String tuidToken) { this.tuidToken = tuidToken; }
}