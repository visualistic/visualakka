/*
 */
package org.ave.realm.model;

import java.io.Serializable;

/**
 *
 */
public class Citizen implements Serializable {
    private final int id;
    
    private final boolean isMale;

    public Citizen(int id, boolean isMale, int x, int y) {
        this.id = id;
        this.isMale = isMale;
        this.x = x;
        this.y = y;
        this.hungriness = 0.5f;
        this.happiness = 0.5f;
    }    
    
    public Citizen(int id, boolean isMale, int x, int y, float hungriness, float happiness) {
        this.id = id;
        this.isMale = isMale;
        this.x = x;
        this.y = y;
        this.hungriness = hungriness;
        this.happiness = happiness;
    }    
    
    public Citizen newState(float hungriness, float happiness) {        
        return new Citizen(id, isMale, x, y, hungriness, happiness);
    }
    
    //position
    private final int x, y;
    
    private final float hungriness;
    private final float happiness;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the isMale
     */
    public boolean isIsMale() {
        return isMale;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @return the hungriness
     */
    public float getHungriness() {
        return hungriness;
    }

    /**
     * @return the happiness
     */
    public float getHappiness() {
        return happiness;
    }
}
