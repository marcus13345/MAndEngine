package MAndEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * also a bit of an app helper class thing
 * well... i renamed it so, now it just is an app helper class thing.
 * @author Marcus
 *
 */
public class AppHelper implements Runnable{

	private static String[] namesLookupTable;
	private static String[] classNamesLookupTable;
	
	private BasicApp[] apps;
    private boolean done = false;
    private int progress = 0;
    private int TOTAL_PROGRESS = 1;

    private String[] classes;
    
    public AppHelper(String[] classes) {
    	this.classes = classes;
    }
    
	public void run() {
        ArrayList<BasicApp> apps = new ArrayList<BasicApp>();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        
        try{
        	
            
            for(int i = 0; i < classes.length; i ++) {
                String str = classes[i];
                
                Class _class = classLoader.loadClass(str);
                Object obj = _class.newInstance();

                //if it actually is one of us, repeatedly say one of us...
                //one of us...
                //one of us...
                if(obj instanceof BasicApp) {
                    apps.add((BasicApp)(obj));
                }
                progress++;
            }

            this.apps = new BasicApp[apps.size()];

            for(int i = 0; i < this.apps.length; i ++) {
                this.apps[i] = apps.get(i);
            }
            
        	classNamesLookupTable = new String[this.apps.length];
        	namesLookupTable = new String[this.apps.length];
            for(int i = 0; i < this.apps.length; i ++) {
            	
            	classNamesLookupTable[i] = classes[i];
            	namesLookupTable[i] = this.apps[i].getTitle();
            	
            }
            //progress = 1;
            done = true;
        }catch(Exception e) {
        	
        	e.printStackTrace();
        	
            System.exit(1);
        }
	}

    public BasicApp[] getApps(){
        return apps;
    }

    public boolean getDone(){
        return done;
    }

    public double getProgress(){
        return (double)progress/TOTAL_PROGRESS;
    }
    
    public static int getIDbyClass(String className) {
    	for(int i = 0; i < namesLookupTable.length; i ++) {
    		if(namesLookupTable[i].equalsIgnoreCase(className)){
    			
    			return i;
    			
    		}
    	}
    	return -1;
    }
    
    /**
     * iterative method, probs shouldn't call this during a
     * tick or something. like do it in a thread during a splash screen
     * or something.
     * @param appName
     * @return
     */
    public static int getIDbyName(String appName) {
    	for(int i = 0; i < namesLookupTable.length; i ++) {
    		if(namesLookupTable[i].equals(appName)){
    			
    			return i;
    			
    		}
    	}
    	return -1;
    }
}
