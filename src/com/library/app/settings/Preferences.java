package com.library.app.settings;


import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Preferences {
    public static final String CONFIG_FILE = "config.txt";
    int nDaysWithoutFine;
    float finePerDay;

    public Preferences() {
        nDaysWithoutFine = 14;
        finePerDay = 2;
    }
    
    public int getnDaysWithoutFine(){
        return nDaysWithoutFine;
    }
    
    public void setnDaysWithoutFine(int nDaysWithoutFine){
        this.nDaysWithoutFine = nDaysWithoutFine;
    }
    
    public float getfinePerDay(){
        return finePerDay;
    }
    
    public void setfinePerDay(float finePerDay){
        this.finePerDay = finePerDay;
    }
    
    public static void initConfig()
    {
        Preferences preferences = new Preferences();
        Gson gson = new Gson();
        Writer writer = null;
        try {
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preferences, writer);
        } catch (IOException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static Preferences getPreferences()
    {
        Gson gson = new Gson();
        Preferences preferences = new Preferences();
        try {
            preferences = gson.fromJson(new FileReader(CONFIG_FILE), Preferences.class);
        } catch (FileNotFoundException ex) {
            initConfig();
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        }
        return preferences;
    }
    
    public static Boolean writePrefrencesToFile(Preferences preferences)
    {
        Gson gson = new Gson();
        Writer writer = null;
        try {
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preferences, writer);
            return true;
            
        } catch (IOException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }finally{
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
