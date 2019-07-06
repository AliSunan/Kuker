package com.edfadsfxample.petik.kuker.Model;

public class Users {
    private String id, password, phone, address, image;

    public Users()
    {

    }


    public Users(String id, String password, String phone, String address, String image) {
        this.id = id;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.image = image;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}