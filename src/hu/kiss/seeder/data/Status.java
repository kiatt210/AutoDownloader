/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.data;

/**
 *
 * @author KICSI
 */
public enum Status {
    
        SEED("Seeding"),
        PAUSED("Paused"),
        DOWNLOAD("Downloading"),
        QUEUED("Queued"),
        ERROR("Error");
    
    private String delugeString;
    
    private Status(String string){
        this.delugeString = string;
    }
    
    public static Status findByStr(String str){
        for(Status s : Status.values()){
            if(s.delugeString.equals(str)){
                return s;
            }
        }
        return null;
        
    }
    
    @Override
    public String toString(){
        return delugeString;
    }
    
}
