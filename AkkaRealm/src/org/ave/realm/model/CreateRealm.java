/*
 */
package org.ave.realm.model;

import java.io.Serializable;

/**
 *
 */
public class CreateRealm implements Serializable {
    
    private final int citizensCount;

    public CreateRealm(int citizensCount) {
        this.citizensCount = citizensCount;
    } 
    
    
    public boolean getInitialWorldState() {
        return false;
    }

    /**
     * @return the citizensCount
     */
    public int getCitizensCount() {
        return citizensCount;
    }
}
