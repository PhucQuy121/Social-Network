package com.example.project_fakebook.model;

import java.util.List;

public class error {
    private List<String> email;
    private List<String> passord;
    private List<String> last_name;
    private List<String> first_name;
    private List<String> phone;

    public List<String> getEmail() {
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public List<String> getPassord() {
        return passord;
    }

    public void setPassord(List<String> passord) {
        this.passord = passord;
    }

    public List<String> getLast_name() {
        return last_name;
    }

    public void setLast_name(List<String> last_name) {
        this.last_name = last_name;
    }

    public List<String> getFirst_name() {
        return first_name;
    }

    public void setFirst_name(List<String> first_name) {
        this.first_name = first_name;
    }

    public List<String> getPhone() {
        return phone;
    }

    public void setPhone(List<String> phone) {
        this.phone = phone;
    }
}
