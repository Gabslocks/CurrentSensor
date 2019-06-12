package com.example.currentsensor_IoT;

public class Lamps {
    private String name;
    private String cond;

    public Lamps(String name,  String cond){
        this.name=name;
        this.cond=cond;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getCond(){
        return this.cond;
    }
    public void setCond(String cond){
        this.name = cond;
    }


}
