/*
 */
package org.ave.realm.model;

import java.io.Serializable;

/**
 *
 */
public class Citizen implements Serializable {
    private int id;
    
    private boolean isMale;
    
    //position
    private int x, y;
    
    private float hungriness;
    private float happiness;
}
