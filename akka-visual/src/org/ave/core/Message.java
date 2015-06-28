/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ave.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import org.ave.core.exceptions.AVEInternaException;

/**
 * Message representation class in Visual Akka Represents visual message
 * container for user data. As akka insists, user should garantee that data
 * object must be immutable.
 *
 * @author Vladislav Larin
 */
public final class Message {

    public static final class Location {

        private final String unit;
        private final String node;
        private final int instanceId;
        private final boolean isRouted;

        public Location(String unit, String node, int instanceId, boolean isRouted) {
            this.unit = unit;
            this.node = node;
            this.instanceId = instanceId;
            this.isRouted = isRouted;
        }

        /**
         * @return the unit
         */
        public String getUnit() {
            return unit;
        }

        /**
         * @return the node
         */
        public String getNode() {
            return node;
        }

        /**
         * @return the instanceId
         */
        public int getInstanceId() {
            return instanceId;
        }

        /**
         * @return the isRouted
         */
        public boolean IsRouted() {
            return isRouted;
        }
    }

    public static final class Instance implements Comparable<Instance> {

        public static final String ROOTINSTANCE = "__ROOTINSTANCE";
        public static final String ENTRYPOINT = "__ENTRYPOINT";

        private final Deque<Location> callStack;
        private int hopesAmount;

        public Instance(String unit, String node, int instanceId, boolean isRouted) {
            callStack = new LinkedList<>();
            hopesAmount = 0;

            enterLocation(ROOTINSTANCE, ENTRYPOINT, 0, false);
            enterLocation(unit, node, instanceId, isRouted);
        }

        public Instance(Instance cloneFrom) {
            callStack = new LinkedList<>();
            callStack.addAll(cloneFrom.callStack);

            hopesAmount = cloneFrom.hopesAmount;
        }

        @Override
        public int compareTo(Instance o) {
            int scmp = Integer.compare(callStack.size(), o.callStack.size());
            if (scmp != 0) {
                return scmp;
            }

            return Integer.compare(hopesAmount, o.hopesAmount);
        }

        public Collection<Location> getStack() {
            return Collections.unmodifiableCollection(callStack);
        }

        public Location getTopLocation() throws AVEInternaException {
            Location top = callStack.peek();
            if (top == null) {
                throw new AVEInternaException("Instances stack is empty!");
            }

            return top;
        }

        private void enterLocation(String unit, String node,
                int instanceId, boolean isRouted) {
            callStack.push(new Location(unit, node, instanceId, isRouted));

            //We've made a hope into a unit instance
            hopesAmount++;
        }

        private void exitLocation() {
            callStack.pop();
        }
    }

    private Object data;
    private Instance instance;
    private String callback;

    public Message(Object data, String unit,
            String node, int instanceId, boolean isRouted) {
        this.data = data;
        this.instance = new Instance(unit, node, instanceId, isRouted);
    }

    public Message(Object date, Instance instance) {
        this.data = date;
        this.instance = new Instance(instance);
    }
    
    public Message create(Object data) {
        Message t = new Message(data, getInstance());
        t.callback = callback;
        return t;
    }

    public Message createCallback(Object data, String callback) {
        Message t = new Message(data, getInstance());
        t.getInstance().exitLocation();
        t.callback = callback;

        return t;
    }

    public Message createCallback(String callback) {
        return createCallback(data, callback);
    }

    public Message createDirectCall(Object data, String unit,
            String node, int instanceId, boolean isRouted) {
        Message t = new Message(data, getInstance());
        t.getInstance().enterLocation(unit, node, instanceId, isRouted);

        return t;
    }

    public Message createDirectCall(String unit, String node,
            int instanceId, boolean isRouted) {
        return createDirectCall(data, unit, node, instanceId, isRouted);
    }

    public String getTargetUnit() throws AVEInternaException {
        return instance.getTopLocation().unit;
    }

    public String getTargetNode() throws AVEInternaException {
        return instance.getTopLocation().node;
    }

    public int getTargetId() throws AVEInternaException {
        return instance.getTopLocation().instanceId;
    }
    
    public boolean isTargetRouted() throws AVEInternaException {
        return instance.getTopLocation().isRouted;
    }

    /**
     * Get the user's data. Must be immutable!
     *
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @return the instance
     */
    public Instance getInstance() {
        return instance;
    }

    public boolean isCallback() {
        return callback != null && !callback.isEmpty();
    }

    /**
     * @return the callback
     */
    public String getCallback() {
        return callback;
    }
}
