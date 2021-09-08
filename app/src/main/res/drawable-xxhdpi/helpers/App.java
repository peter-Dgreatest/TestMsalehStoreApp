package com.itcrusaders.msaleh.helpers;

import android.app.Application;

public class App extends Application {

    private String username,email,account_type,firstname,activeEmp_no,tempAccount_type,delieveryId;
    private int rf1,rf2,lf1,lf2;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLf1(int lf1) {
        this.lf1 = lf1;
    }

    public int getLf1() {
        return lf1;
    }

    public String getActiveEmp_no() {
        return activeEmp_no;
    }

    public void setActiveEmp_no(String activeEmp_no) {
        this.activeEmp_no = activeEmp_no;
    }

    public void setLf2(int lf2) {
        this.lf2 = lf2;
    }

    public int getLf2() {
        return lf2;
    }

    public void setRf1(int rf1) {
        this.rf1 = rf1;
    }

    public int getRf1() {
        return rf1;
    }

    public void setRf2(int rf2) {
        this.rf2 = rf2;
    }

    public int getRf2() {
        return rf2;
    }

    public void setTempAccount_type(String tempAccount_type) {
        this.tempAccount_type = tempAccount_type;
    }

    public String getTempAccount_type() {
        return tempAccount_type;
    }

    public String getDelieveryId() {
        return delieveryId;
    }

    public void setDelieveryId(String delieveryId) {
        this.delieveryId = delieveryId;
    }
}
