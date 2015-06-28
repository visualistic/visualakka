package org.ave.core;

import akka.actor.ActorRef;

/**
 * Flow abstraction. Used in user methods implementations
 * NOTE! Flow destionation actor doesn't equals flow logical destination!
 * @author Vladislav Larin
 * @param <T> Data type of the Flow
 */
public class Flow<T> {

    private final ActorRef sender;
    private final ActorRef destination;
    private final Message prototype;

    public Flow(ActorRef sender, ActorRef destination, Message prototype) {
        this.sender = sender;
        this.destination = destination;
        this.prototype = prototype;
    }
    
    /**
     * Send immediately value to the destination.
     * Automatically packs in the prototype message
     * @param value Value to send
     */
    public void send(T value) {
        getDestination().tell(getPrototype().create(value), getSender());
    }

    /**
     * @return the sender
     */
    public ActorRef getSender() {
        return sender;
    }

    /**
     * @return the destination
     */
    public ActorRef getDestination() {
        return destination;
    }

    /**
     * @return the prototype
     */
    public Message getPrototype() {
        return prototype;
    }
}
