package com.nn.palestadio.android_java;

public class UserInformation {

    public String cedula;

    public UserInformation(String cedula) {
        this.cedula = cedula;
    }

    public UserInformation(){

    }


    public void setCedula(String cedula)
    {
        this.cedula = cedula;
    }

    public String getCedula() {
        return cedula;
    }
}
