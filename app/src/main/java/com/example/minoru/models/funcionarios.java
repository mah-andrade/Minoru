package com.example.minoru.models;

import com.google.firebase.database.Exclude;

public class funcionarios {
    private String documentId;
    private String vaga;

    public funcionarios(){
        //CONSTRUCTOR


    }
    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getVaga() {
        return vaga;
    }
}
