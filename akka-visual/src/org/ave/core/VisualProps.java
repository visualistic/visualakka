package org.ave.core;

import java.io.Serializable;

/**
 * Properties of the visual actor.
 * @author Larin Vladislav
 */
public class VisualProps implements Serializable {

    private final String parentPath;

    private final String supStrategy;
    private final int supNumOfRetries;
    private final String supDuration;

    public VisualProps(String parentPath, String supStrategy,
            int supNumOfRetries, String supDuration) {
        this.parentPath = parentPath;
        this.supStrategy = supStrategy;
        this.supNumOfRetries = supNumOfRetries;
        this.supDuration = supDuration;
    }

    /**
     * @return the parentPath
     */
    public String getParentPath() {
        return parentPath;
    }

    /**
     * @return the supStrategy
     */
    public String getSupStrategy() {
        return supStrategy;
    }

    /**
     * @return the supNumOfRetries
     */
    public int getSupNumOfRetries() {
        return supNumOfRetries;
    }

    /**
     * @return the supDuration
     */
    public String getSupDuration() {
        return supDuration;
    }
}
