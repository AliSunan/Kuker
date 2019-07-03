package com.edfadsfxample.petik.kuker.Model;

public class Users
{
    private String id, email, password, phone, adress;

    public Users()
    {

    }


    public Users(String id, String email, String password, String phone, String adress) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.adress = adress;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }
}
