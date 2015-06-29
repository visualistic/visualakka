
package org.ave.realm;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.ave.core.Flow;
import org.ave.core.Message;
import org.ave.core.Node;
import org.ave.core.VisualActor;
import org.ave.core.VisualActorImpl;
import org.ave.core.VisualProps;
import org.ave.core.exceptions.AVEInternaException;


/**
 * Generated code! DO NOT MODIFY!
 * 
 */
public class WorldActor
    extends VisualActor
{

    private WorldActorImpl userLogic;
    private ActorRef UserCodeBlocks6;

    public WorldActor(VisualProps vprops, String methodName, String[] methodArgs) {
        userCodeImplementation = new WorldActorImpl();
        userLogic = ((WorldActorImpl) userCodeImplementation);
        LinkedList<List<String>> methods = new LinkedList<List<String>>();
        LinkedList<Node> nodes = new LinkedList<Node>();
        switch (methodName) {
            case "_NEW":
                nodes.add(new Node("Update", "java.lang.Boolean", "", false));
                methods.add(Arrays.asList(new String[] {"Update"}));
                break;
            case "_UCB_GenerateWorldEvent":
                nodes.add(new Node("GenerateWorldEvent", "java.lang.Boolean", "", false));
                methods.add(Arrays.asList(new String[] {"GenerateWorldEvent"}));
                break;
            case "Update":
                nodes.add(new Node("Update", "java.lang.Boolean", "", false));
                methods.add(Arrays.asList(new String[] {"Update"}));
                break;
        }
        if (methodName.equals("_NEW")) {
            initActorFabric(vprops, methods, nodes);
        } else {
            initSupervisor(vprops.getSupStrategy(), vprops.getSupNumOfRetries(), vprops.getSupDuration());
            init(vprops.getParentPath(), methods, nodes, "DEFAULT");
        }
    }

    /**
     * Callback handler for visual actor.
     * 
     * @param callback
     *     Message that was returned by called child before
     * @throws AVEInternaException
     *     Throwed when message has invalid instance
     */
    @Override
    protected void handleCallback(Message callback)
        throws AVEInternaException
    {
        switch (callback.getCallback()) {
            case "__GenerateWorldEvent6__IsEventGenerated__callback":
                getSelfRouted(callback).tell(callback.createDirectCall("WorldActor", "Update", 5, false), getSelfRouted(callback));
                break;
        }
    }

    /**
     * Creates actors children instances on demand.
     * 
     */
    @Override
    protected void postInitialize() {
        Props UserCodeBlocks6_decl = Props.create(org.ave.realm.WorldActor.class, new org.ave.realm.WorldActor.Creator(new VisualProps(getPath(), "Restart", 2147483647, "-1"), "_UCB_GenerateWorldEvent", new String[] { }));
        UserCodeBlocks6 = getContext().actorOf(UserCodeBlocks6_decl, "UserCodeBlocks6");
    }

    /**
     * Virtual method handler for GenerateWorldEvent method.
     * 
     * @throws AVEInternaException
     *     Throws when message has an invalid instance.
     */
    public void GenerateWorldEvent(Message GenerateWorldEvent)
        throws AVEInternaException
    {
        userLogic.GenerateWorldEvent(((Boolean) GenerateWorldEvent.getData()), new Flow<Boolean>(getSelfRouted(GenerateWorldEvent), getParent(), GenerateWorldEvent.createCallback("__GenerateWorldEvent6__IsEventGenerated__callback")));
    }

    /**
     * Method call handler for Update method.
     * 
     * @param Update
     *     Input node Update the method
     * @throws AVEInternaException
     *     Throws when message has invalid instance.
     */
    public void Update(Message Update)
        throws AVEInternaException
    {
        UserCodeBlocks6 .tell(Update.createDirectCall("UserCodeBlocks", "GenerateWorldEvent", 6, false), getSelfRouted(Update));
    }

    /**
     * Spawn new sub-instance of that actor
     * 
     * @param methodName
     *     Method to spawn
     */
    @Override
    protected ActorRef createSelfInstance(String methodName) {
        return getContext().actorOf(Props.create(WorldActor.class, new WorldActor.Creator(fabricProps, methodName, new String[] { })), "instance");
    }

    public static class Creator
        implements akka.japi.Creator<WorldActor>
    {

        private final VisualProps vprops;
        private final String methodName;
        private final String[] methodArgs;

        public Creator(VisualProps vprops, String methodName, String[] methodArgs) {
            this.vprops = vprops;
            this.methodName = methodName;
            this.methodArgs = methodArgs;
        }

        @Override
        public WorldActor create() {
            return new WorldActor(vprops, methodName, methodArgs);
        }

    }

    public static abstract class UserLogic
        extends VisualActorImpl
    {


        /**
         * User code block handler - GenerateWorldEvent.
         * 
         * @param GenerateWorldEvent
         *     Unboxed message data
         * @param IsEventGenerated
         *     Block results flow
         */
        public abstract void GenerateWorldEvent(Boolean GenerateWorldEvent, Flow<Boolean> IsEventGenerated);

    }

}
