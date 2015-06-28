package org.ave.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.ave.core.Message.Instance;
import org.ave.core.exceptions.AVEInternaException;

/**
 *
 * @author Vladislav Larin
 */
public final class MethodGroup {

    private VisualActor owner;
    private List<String> group;

    private int readyCount;
    private Map<String, Queue<Message>> msgQueue;
    private boolean initialized;

    public MethodGroup(VisualActor owner, List<String> group) {
        msgQueue = new HashMap<>();
        initialized = false;
        readyCount = 0;

        this.group = group;
        this.owner = owner;
    }

    public List<?> getArgs() {
        return group;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void initialize() throws AVEInternaException {
        //Create group Nodes check and signals stack construction
        for (int i = 0; i < group.size(); i++) {
            if (group.get(i).isEmpty()) {
                throw new AVEInternaException("One of the group members is empty!");
            }

            msgQueue.put(group.get(i), new LinkedList<Message>());
        }

        initialized = true;
    }

    public void pushMessageInGroup(Message msg) throws AVEInternaException {
        if (!isInitialized()) {
            initialize();
        }

        msgQueue.get(msg.getTargetNode()).offer(msg);
        updateReadyState();
    }

    public Message[] extractAvaitingMessages() throws AVEInternaException {
        //ULogger.Instance.LogSystemDebug("Method of " + owner.getName() + " started!");

        if (!isGroupReady()) {
            throw new AVEInternaException("Barrier not fueld out for extracting signals!");
        }

        int counter = 0;
        Message[] resultingPackage = new Message[readyCount];
        for (String node : group) {
            Message sig = msgQueue.get(node).poll();
            resultingPackage[counter] = sig;
            counter++;
        }

        updateReadyState();
        return resultingPackage;
    }

    public boolean isGroupReady() throws AVEInternaException {
        if (!isInitialized()) {
            //ULogger.Instance.LogSystemDebug("Instance " + instance + " initialized!");
            initialize();
        }

        return readyCount == group.size();
    }

    private void updateReadyState() throws AVEInternaException {
        readyCount = 0;
        String group = null;
        Instance sample = null;

        for (String nodeId : msgQueue.keySet()) {
            if (sample == null && msgQueue.get(nodeId).size() > 0) {
                sample = msgQueue.get(nodeId).peek().getInstance();
                break;
            }
        }

        for (String nodeId : msgQueue.keySet()) {
            if (group == null) {
                group = nodeId;
            }

            if (msgQueue.get(nodeId).size() == 0) {
                if (sample != null) {
                    Node nd = owner.getNode(nodeId);

                    if (nd.isFixed()) {
                        Object def = nd.spawnDefaultValue();
                        //ULogger.Instance.LogSystemDebug(siganlsStack.Key + ' ' + nd.constantValue);

                        if (def != null) {
                            Message cons = new Message(def, sample);
                            msgQueue.get(nodeId).offer(cons);
                            readyCount++;
                        }
                    }
                }
            } else {
                readyCount++;
            }
        }
    }
}
