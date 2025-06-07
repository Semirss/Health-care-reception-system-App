package com.example.hcrs.wrapper;

public class DeletePatientRequest {
    private int patientID;

    public DeletePatientRequest(int patientID) {
        this.patientID = patientID;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }
}