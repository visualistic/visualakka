
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
import org.ave.realm.model.Citizen;


/**
 * Generated code! DO NOT MODIFY!
 * 
 */
public class RealmActor
    extends VisualActor
{

    private RealmActorImpl userLogic;
    private ActorRef WorldActor7;
    private ActorRef UserCodeBlocks8;
    private ActorRef CitizenActor19;

    public RealmActor(VisualProps vprops, String methodName, String[] methodArgs) {
        userCodeImplementation = new RealmActorImpl();
        userLogic = ((RealmActorImpl) userCodeImplementation);
        LinkedList<List<String>> methods = new LinkedList<List<String>>();
        LinkedList<Node> nodes = new LinkedList<Node>();
        switch (methodName) {
            case "_NEW":
                nodes.add(new Node("Start", "org.ave.realm.model.CreateRealm", "", false));
                methods.add(Arrays.asList(new String[] {"Start"}));
                break;
            case "_UCB_GenerateCitizens":
                nodes.add(new Node("GenerateCitizens", "org.ave.realm.model.CreateRealm", "", false));
                methods.add(Arrays.asList(new String[] {"GenerateCitizens"}));
                break;
            case "Start":
                nodes.add(new Node("Start", "org.ave.realm.model.CreateRealm", "", false));
                methods.add(Arrays.asList(new String[] {"Start"}));
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
            case "__GenerateCitizens8__Output__callback":
                CitizenActor19 .tell(callback.createDirectCall("CitizenActor", "SelectAction", 19, false), getSelfRouted(callback));
                break;
        }
    }

    /**
     * Creates actors children instances on demand.
     * 
     */
    @Override
    protected void postInitialize() {
        Props WorldActor7_decl = Props.create(WorldActor.class, new org.ave.realm.WorldActor.Creator(new VisualProps(getPath(), "Resume", 2147483647, "-1"), "Update", new String[] { }));
        WorldActor7 = getContext().actorOf(WorldActor7_decl, "WorldActor7");
        Props UserCodeBlocks8_decl = Props.create(org.ave.realm.RealmActor.class, new org.ave.realm.RealmActor.Creator(new VisualProps(getPath(), "Escalate", 2147483647, "-1"), "_UCB_GenerateCitizens", new String[] { }));
        UserCodeBlocks8 = getContext().actorOf(UserCodeBlocks8_decl, "UserCodeBlocks8");
        Props CitizenActor19_decl = Props.create(CitizenActor.class, new org.ave.realm.CitizenActor.Creator(new VisualProps(getPath(), "Escalate", 2147483647, "-1"), "SelectAction", new String[] { }));
        CitizenActor19 = getContext().actorOf(CitizenActor19_decl, "CitizenActor19");
    }

    /**
     * Virtual method handler for GenerateCitizens method.
     * 
     * @throws AVEInternaException
     *     Throws when message has an invalid instance.
     */
    public void GenerateCitizens(Message GenerateCitizens)
        throws AVEInternaException
    {
        userLogic.GenerateCitizens(((org.ave.realm.model.CreateRealm) GenerateCitizens.getData()), new Flow<Citizen>(getSelfRouted(GenerateCitizens), getParent(), GenerateCitizens.createCallback("__GenerateCitizens8__Output__callback")));
    }

    /**
     * Method call handler for Start method.
     * 
     * @param Start
     *     Input node Start the method
     * @throws AVEInternaException
     *     Throws when message has invalid instance.
     */
    public void Start(Message Start)
        throws AVEInternaException
    {
        WorldActor7 .tell(Start.createDirectCall(((org.ave.realm.model.CreateRealm) Start.getData()).getInitialWorldState(), "WorldActor", "Update", 7, false), getSelfRouted(Start));
        UserCodeBlocks8 .tell(Start.createDirectCall("UserCodeBlocks", "GenerateCitizens", 8, false), getSelfRouted(Start));
    }

    /**
     * Spawn new sub-instance of that actor
     * 
     * @param methodName
     *     Method to spawn
     */
    @Override
    protected ActorRef createSelfInstance(String methodName) {
        return getContext().actorOf(Props.create(RealmActor.class, new RealmActor.Creator(fabricProps, methodName, new String[] { })), "instance");
    }

    public static class Creator
        implements akka.japi.Creator<RealmActor>
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
        public RealmActor create() {
            return new RealmActor(vprops, methodName, methodArgs);
        }

    }

    public static abstract class UserLogic
        extends VisualActorImpl
    {


        /**
         * User code block handler - GenerateCitizens.
         * 
         * @param Output
         *     Block results flow
         * @param GenerateCitizens
         *     Unboxed message data
         */
        public abstract void GenerateCitizens(org.ave.realm.model.CreateRealm GenerateCitizens, Flow<Citizen> Output);

    }

}
