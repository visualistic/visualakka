/*
 */
package org.ave.core;

import java.util.List;

/**
 *
 * @author Vladislav Larin
 */
public abstract class VisualActorImpl {
    
       /**
     * User overridable hook. Can be used to maintain custom in-code logic.
     * Starts BEFORE method call!
     *
     * @param arguments list of message arguments, packed in the messages
     */
    public void preMethodStart(List<Message> arguments) {
    }

    /**
     * User overridable hook. Can be used to maintain custom in-code logic.
     * Starts AFTER method call!
     *
     * @param arguments list of message arguments, packed in the messages
     */
    public void postMethodStart(List<Message> arguments) {
    }
    
     /**
     * Handles unrecognized message.
     *
     * @param msg Some message that doesn't instance of Message class
     * @return TRUE if message could be accounted as recognized, FALSE otherwise
     */
    protected boolean tryHandleUnrecognized(Object msg) {
        return false;
    }
    
}
