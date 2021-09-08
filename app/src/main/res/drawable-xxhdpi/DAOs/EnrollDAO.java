package com.itcrusaders.msaleh.database;

import java.io.Serializable;

public class EnrollDAO implements Serializable {

    private String staffName,staffno,branch,depart,job_description,supervisor,staffTitle;
    private String staffId, staffaddress,staffno2,staffMail,staffBday;
    private String staffStateOfOrigin, staffLGA,staffMaritalStatus;
    private String nxtOfKinName,nxtOfKinTitle,nxtOfKinAddress;
    private String guarantorName,guarantorTitle,guarantorPhone1,guarantorPhone2,guarantormail;

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffno() {
        return staffno;
    }

    public void setStaffno(String staffno) {
        this.staffno = staffno;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getJob_description() {
        return job_description;
    }

    public void setJob_description(String job_description) {
        this.job_description = job_description;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getStaffno2() {
        return staffno2;
    }

    public void setStaffno2(String staffno2) {
        this.staffno2 = staffno2;
    }

    public String getStaffMail() {
        return staffMail;
    }

    public void setStaffMail(String staffMail) {
        this.staffMail = staffMail;
    }

    public String getStaffBday() {
        return staffBday;
    }

    public void setStaffBday(String staffBday) {
        this.staffBday = staffBday;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffaddress() {
        return staffaddress;
    }

    public void setStaffaddress(String staffaddress) {
        this.staffaddress = staffaddress;
    }

    public String getStaffStateOfOrigin() {
        return staffStateOfOrigin;
    }

    public void setStaffStateOfOrigin(String staffStateOfOrigin) {
        this.staffStateOfOrigin = staffStateOfOrigin;
    }

    public String getStaffLGA() {
        return staffLGA;
    }

    public void setStaffLGA(String staffLGA) {
        this.staffLGA = staffLGA;
    }

    public String getStaffMaritalStatus() {
        return staffMaritalStatus;
    }

    public void setStaffMaritalStatus(String staffMaritalStatus) {
        this.staffMaritalStatus = staffMaritalStatus;
    }

    public String getNxtOfKinAddress() {
        return nxtOfKinAddress;
    }

    public void setNxtOfKinAddress(String nxtOfKinAddress) {
        this.nxtOfKinAddress = nxtOfKinAddress;
    }

    public String getGuarantormail() {
        return guarantormail;
    }

    public void setGuarantormail(String guarantormail) {
        this.guarantormail = guarantormail;
    }

    public String getGuarantorPhone2() {
        return guarantorPhone2;
    }

    public void setGuarantorPhone2(String guarantorPhone2) {
        this.guarantorPhone2 = guarantorPhone2;
    }

    public String getGuarantorPhone1() {
        return guarantorPhone1;
    }

    public void setGuarantorPhone1(String guarantorPhone1) {
        this.guarantorPhone1 = guarantorPhone1;
    }

    public String getNxtOfKinTitle() {
        return nxtOfKinTitle;
    }

    public void setNxtOfKinTitle(String nxtOfKinTitle) {
        this.nxtOfKinTitle = nxtOfKinTitle;
    }

    public String getGuarantorTitle() {
        return guarantorTitle;
    }

    public void setGuarantorTitle(String guarantorTitle) {
        this.guarantorTitle = guarantorTitle;
    }

    public String getGuarantorName() {
        return guarantorName;
    }

    public void setGuarantorName(String guarantorName) {
        this.guarantorName = guarantorName;
    }

    public String getNxtOfKinName() {
        return nxtOfKinName;
    }

    public void setNxtOfKinName(String nxtOfKinName) {
        this.nxtOfKinName = nxtOfKinName;
    }


    public String getStaffTitle() {
        return staffTitle;
    }

    public void setStaffTitle(String staffTitle) {
        this.staffTitle = staffTitle;
    }
}
