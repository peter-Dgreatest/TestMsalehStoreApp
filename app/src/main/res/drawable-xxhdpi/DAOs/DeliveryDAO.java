package com.itcrusaders.msaleh.database;

import java.io.Serializable;

public class DeliveryDAO implements Serializable {

    private String equipmentbarcode,clientname,clientaddress,purchasedate, deliveryinstructions,
            officialmail, clientphone, altphone, receiptno, pickupbranch, attendingStaffId,
            attendingStaffName, pickupaddress, receivername, receiverdesignation, receivermail, 
            receiverphone1, receiverphone2, approvedby,dateCreated,deliveryId,equipmentName,status;

    public String getEquipmentbarcode() {
        return equipmentbarcode;
    }

    public void setEquipmentbarcode(String equipmentbarcode) {
        this.equipmentbarcode = equipmentbarcode;
    }

    public String getClientname() {
        return clientname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }

    public String getClientaddress() {
        return clientaddress;
    }

    public void setClientaddress(String clientaddress) {
        this.clientaddress = clientaddress;
    }

    public String getDeliveryinstructions() {
        return deliveryinstructions;
    }

    public void setDeliveryinstructions(String deliveryinstructions) {
        this.deliveryinstructions = deliveryinstructions;
    }

    public String getPurchasedate() {
        return purchasedate;
    }

    public void setPurchasedate(String purchasedate) {
        this.purchasedate = purchasedate;
    }

    public String getAttendingStaffId() {
        return attendingStaffId;
    }

    public void setAttendingStaffId(String attendingStaffId) {
        this.attendingStaffId = attendingStaffId;
    }

    public String getPickupbranch() {
        return pickupbranch;
    }

    public void setPickupbranch(String pickupbranch) {
        this.pickupbranch = pickupbranch;
    }

    public String getReceiptno() {
        return receiptno;
    }

    public void setReceiptno(String receiptno) {
        this.receiptno = receiptno;
    }

    public String getAltphone() {
        return altphone;
    }

    public void setAltphone(String altphone) {
        this.altphone = altphone;
    }

    public String getClientphone() {
        return clientphone;
    }

    public void setClientphone(String clientphone) {
        this.clientphone = clientphone;
    }

    public String getOfficialmail() {
        return officialmail;
    }

    public void setOfficialmail(String officialmail) {
        this.officialmail = officialmail;
    }

    public String getPickupaddress() {
        return pickupaddress;
    }

    public void setPickupaddress(String pickupaddress) {
        this.pickupaddress = pickupaddress;
    }

    public String getAttendingStaffName() {
        return attendingStaffName;
    }

    public void setAttendingStaffName(String attendingStaffName) {
        this.attendingStaffName = attendingStaffName;
    }

    public String getReceivername() {
        return receivername;
    }

    public void setReceivername(String receivername) {
        this.receivername = receivername;
    }

    public String getReceiverdesignation() {
        return receiverdesignation;
    }

    public void setReceiverdesignation(String receiverdesignation) {
        this.receiverdesignation = receiverdesignation;
    }

    public String getReceivermail() {
        return receivermail;
    }

    public void setReceivermail(String receivermail) {
        this.receivermail = receivermail;
    }

    public String getApprovedby() {
        return approvedby;
    }

    public void setApprovedby(String approvedby) {
        this.approvedby = approvedby;
    }

    public String getReceiverphone2() {
        return receiverphone2;
    }

    public void setReceiverphone2(String receiverphone2) {
        this.receiverphone2 = receiverphone2;
    }

    public String getReceiverphone1() {
        return receiverphone1;
    }

    public void setReceiverphone1(String receiverphone1) {
        this.receiverphone1 = receiverphone1;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
