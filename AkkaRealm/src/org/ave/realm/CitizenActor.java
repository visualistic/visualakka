
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
public class CitizenActor
    extends VisualActor
{

    private CitizenActorImpl userLogic;
    private ActorRef UserCodeBlocks16;
    private ActorRef UserCodeBlocks14;
    private ActorRef UserCodeBlocks17;
    private ActorRef UserCodeBlocks11;
    private ActorRef UserCodeBlocks12;
    private ActorRef CitizenActor18;
    private ActorRef UserCodeBlocks13;
    private ActorRef UserCodeBlocks10;

    public CitizenActor(VisualProps vprops, String methodName, String[] methodArgs) {
        userCodeImplementation = new CitizenActorImpl();
        userLogic = ((CitizenActorImpl) userCodeImplementation);
        LinkedList<List<String>> methods = new LinkedList<List<String>>();
        LinkedList<Node> nodes = new LinkedList<Node>();
        switch (methodName) {
            case "_NEW":
                nodes.add(new Node("SelectAction", "org.ave.realm.model.Citizen", "", false));
                methods.add(Arrays.asList(new String[] {"SelectAction"}));
                break;
            case "_UCB_SelectHighestPriority":
                nodes.add(new Node("SelectHighestPriority", "org.ave.realm.model.Citizen", "", false));
                methods.add(Arrays.asList(new String[] {"SelectHighestPriority"}));
                break;
            case "_UCB_GoEat":
                nodes.add(new Node("GoEat", "org.ave.realm.model.Citizen", "", false));
                methods.add(Arrays.asList(new String[] {"GoEat"}));
                break;
            case "_UCB_GoSleep":
                nodes.add(new Node("GoSleep", "org.ave.realm.model.Citizen", "", false));
                methods.add(Arrays.asList(new String[] {"GoSleep"}));
                break;
            case "_UCB_GoHunt":
                nodes.add(new Node("GoHunt", "org.ave.realm.model.Citizen", "", false));
                methods.add(Arrays.asList(new String[] {"GoHunt"}));
                break;
            case "_UCB_Wandering":
                nodes.add(new Node("Wandering", "org.ave.realm.model.Citizen", "", false));
                methods.add(Arrays.asList(new String[] {"Wandering"}));
                break;
            case "_UCB_Communicate":
                nodes.add(new Node("Communicate", "org.ave.realm.model.Citizen", "", false));
                methods.add(Arrays.asList(new String[] {"Communicate"}));
                break;
            case "_UCB_MakeFight":
                nodes.add(new Node("MakeFight", "org.ave.realm.model.Citizen", "", false));
                methods.add(Arrays.asList(new String[] {"MakeFight"}));
                break;
            case "SelectAction":
                nodes.add(new Node("SelectAction", "org.ave.realm.model.Citizen", "", false));
                methods.add(Arrays.asList(new String[] {"SelectAction"}));
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
            case "__SelectHighestPriority10__Communicate__callback":
                UserCodeBlocks16 .tell(callback.createDirectCall("UserCodeBlocks", "Communicate", 16, false), getSelfRouted(callback));
                break;
            case "__SelectHighestPriority10__Wander__callback":
                UserCodeBlocks14 .tell(callback.createDirectCall("UserCodeBlocks", "Wandering", 14, false), getSelfRouted(callback));
                break;
            case "__SelectHighestPriority10__Fight__callback":
                UserCodeBlocks17 .tell(callback.createDirectCall("UserCodeBlocks", "MakeFight", 17, false), getSelfRouted(callback));
                break;
            case "__SelectHighestPriority10__Eat__callback":
                UserCodeBlocks11 .tell(callback.createDirectCall("UserCodeBlocks", "GoEat", 11, false), getSelfRouted(callback));
                break;
            case "__SelectHighestPriority10__Sleep__callback":
                UserCodeBlocks12 .tell(callback.createDirectCall("UserCodeBlocks", "GoSleep", 12, false), getSelfRouted(callback));
                break;
            case "__GoHunt13__Output__callback":
                getSelfRouted(callback).tell(callback.createDirectCall("CitizenActor", "SelectAction", 9, false), getSelfRouted(callback));
                break;
            case "__GoEat11__Output__callback":
                getSelfRouted(callback).tell(callback.createDirectCall("CitizenActor", "SelectAction", 9, false), getSelfRouted(callback));
                break;
            case "__Communicate16__Output__callback":
                getSelfRouted(callback).tell(callback.createDirectCall("CitizenActor", "SelectAction", 15, false), getSelfRouted(callback));
                break;
            case "__Wandering14__Output__callback":
                getSelfRouted(callback).tell(callback.createDirectCall("CitizenActor", "SelectAction", 15, false), getSelfRouted(callback));
                break;
            case "__SelectHighestPriority10__Child__callback":
                CitizenActor18 .tell(callback.createDirectCall("CitizenActor", "SelectAction", 18, false), getSelfRouted(callback));
                break;
            case "__SelectHighestPriority10__Hunt__callback":
                UserCodeBlocks13 .tell(callback.createDirectCall("UserCodeBlocks", "GoHunt", 13, false), getSelfRouted(callback));
                break;
            case "__MakeFight17__Output__callback":
                getSelfRouted(callback).tell(callback.createDirectCall("CitizenActor", "SelectAction", 15, false), getSelfRouted(callback));
                break;
            case "__GoSleep12__Output__callback":
                getSelfRouted(callback).tell(callback.createDirectCall("CitizenActor", "SelectAction", 9, false), getSelfRouted(callback));
                break;
        }
    }

    /**
     * Creates actors children instances on demand.
     * 
     */
    @Override
    protected void postInitialize() {
        Props UserCodeBlocks16_decl = Props.create(org.ave.realm.CitizenActor.class, new org.ave.realm.CitizenActor.Creator(new VisualProps(getPath(), "Resume", 2147483647, "-1"), "_UCB_Communicate", new String[] { }));
        UserCodeBlocks16 = getContext().actorOf(UserCodeBlocks16_decl, "UserCodeBlocks16");
        Props UserCodeBlocks14_decl = Props.create(org.ave.realm.CitizenActor.class, new org.ave.realm.CitizenActor.Creator(new VisualProps(getPath(), "Resume", 2147483647, "-1"), "_UCB_Wandering", new String[] { }));
        UserCodeBlocks14 = getContext().actorOf(UserCodeBlocks14_decl, "UserCodeBlocks14");
        Props UserCodeBlocks17_decl = Props.create(org.ave.realm.CitizenActor.class, new org.ave.realm.CitizenActor.Creator(new VisualProps(getPath(), "Resume", 2147483647, "-1"), "_UCB_MakeFight", new String[] { }));
        UserCodeBlocks17 = getContext().actorOf(UserCodeBlocks17_decl, "UserCodeBlocks17");
        Props UserCodeBlocks11_decl = Props.create(org.ave.realm.CitizenActor.class, new org.ave.realm.CitizenActor.Creator(new VisualProps(getPath(), "Resume", 2147483647, "-1"), "_UCB_GoEat", new String[] { }));
        UserCodeBlocks11 = getContext().actorOf(UserCodeBlocks11_decl, "UserCodeBlocks11");
        Props UserCodeBlocks12_decl = Props.create(org.ave.realm.CitizenActor.class, new org.ave.realm.CitizenActor.Creator(new VisualProps(getPath(), "Resume", 2147483647, "-1"), "_UCB_GoSleep", new String[] { }));
        UserCodeBlocks12 = getContext().actorOf(UserCodeBlocks12_decl, "UserCodeBlocks12");
        Props CitizenActor18_decl = Props.create(org.ave.realm.CitizenActor.class, new org.ave.realm.CitizenActor.Creator(new VisualProps(getPath(), "Resume", 2147483647, "-1"), "_NEW", new String[] { }));
        CitizenActor18 = getContext().actorOf(CitizenActor18_decl, "CitizenActor18");
        Props UserCodeBlocks13_decl = Props.create(org.ave.realm.CitizenActor.class, new org.ave.realm.CitizenActor.Creator(new VisualProps(getPath(), "Resume", 2147483647, "-1"), "_UCB_GoHunt", new String[] { }));
        UserCodeBlocks13 = getContext().actorOf(UserCodeBlocks13_decl, "UserCodeBlocks13");
        Props UserCodeBlocks10_decl = Props.create(org.ave.realm.CitizenActor.class, new org.ave.realm.CitizenActor.Creator(new VisualProps(getPath(), "Resume", 2147483647, "-1"), "_UCB_SelectHighestPriority", new String[] { }));
        UserCodeBlocks10 = getContext().actorOf(UserCodeBlocks10_decl, "UserCodeBlocks10");
    }

    /**
     * Virtual method handler for SelectHighestPriority method.
     * 
     * @throws AVEInternaException
     *     Throws when message has an invalid instance.
     */
    public void SelectHighestPriority(Message SelectHighestPriority)
        throws AVEInternaException
    {
        userLogic.SelectHighestPriority(((org.ave.realm.model.Citizen) SelectHighestPriority.getData()), new Flow<org.ave.realm.model.Citizen>(getSelfRouted(SelectHighestPriority), getParent(), SelectHighestPriority.createCallback("__SelectHighestPriority10__Eat__callback")), new Flow<org.ave.realm.model.Citizen>(getSelfRouted(SelectHighestPriority), getParent(), SelectHighestPriority.createCallback("__SelectHighestPriority10__Sleep__callback")), new Flow<org.ave.realm.model.Citizen>(getSelfRouted(SelectHighestPriority), getParent(), SelectHighestPriority.createCallback("__SelectHighestPriority10__Hunt__callback")), new Flow<org.ave.realm.model.Citizen>(getSelfRouted(SelectHighestPriority), getParent(), SelectHighestPriority.createCallback("__SelectHighestPriority10__Wander__callback")), new Flow<org.ave.realm.model.Citizen>(getSelfRouted(SelectHighestPriority), getParent(), SelectHighestPriority.createCallback("__SelectHighestPriority10__Communicate__callback")), new Flow<org.ave.realm.model.Citizen>(getSelfRouted(SelectHighestPriority), getParent(), SelectHighestPriority.createCallback("__SelectHighestPriority10__Fight__callback")), new Flow<org.ave.realm.model.Citizen>(getSelfRouted(SelectHighestPriority), getParent(), SelectHighestPriority.createCallback("__SelectHighestPriority10__Child__callback")));
    }

    /**
     * Virtual method handler for GoEat method.
     * 
     * @throws AVEInternaException
     *     Throws when message has an invalid instance.
     */
    public void GoEat(Message GoEat)
        throws AVEInternaException
    {
        userLogic.GoEat(((org.ave.realm.model.Citizen) GoEat.getData()), new Flow<org.ave.realm.model.Citizen>(getSelfRouted(GoEat), getParent(), GoEat.createCallback("__GoEat11__Output__callback")));
    }

    /**
     * Virtual method handler for GoSleep method.
     * 
     * @throws AVEInternaException
     *     Throws when message has an invalid instance.
     */
    public void GoSleep(Message GoSleep)
        throws AVEInternaException
    {
        userLogic.GoSleep(((org.ave.realm.model.Citizen) GoSleep.getData()), new Flow<org.ave.realm.model.Citizen>(getSelfRouted(GoSleep), getParent(), GoSleep.createCallback("__GoSleep12__Output__callback")));
    }

    /**
     * Virtual method handler for GoHunt method.
     * 
     * @throws AVEInternaException
     *     Throws when message has an invalid instance.
     */
    public void GoHunt(Message GoHunt)
        throws AVEInternaException
    {
        userLogic.GoHunt(((org.ave.realm.model.Citizen) GoHunt.getData()), new Flow<org.ave.realm.model.Citizen>(getSelfRouted(GoHunt), getParent(), GoHunt.createCallback("__GoHunt13__Output__callback")));
    }

    /**
     * Virtual method handler for Wandering method.
     * 
     * @throws AVEInternaException
     *     Throws when message has an invalid instance.
     */
    public void Wandering(Message Wandering)
        throws AVEInternaException
    {
        userLogic.Wandering(((org.ave.realm.model.Citizen) Wandering.getData()), new Flow<org.ave.realm.model.Citizen>(getSelfRouted(Wandering), getParent(), Wandering.createCallback("__Wandering14__Output__callback")));
    }

    /**
     * Virtual method handler for Communicate method.
     * 
     * @throws AVEInternaException
     *     Throws when message has an invalid instance.
     */
    public void Communicate(Message Communicate)
        throws AVEInternaException
    {
        userLogic.Communicate(((org.ave.realm.model.Citizen) Communicate.getData()), new Flow<org.ave.realm.model.Citizen>(getSelfRouted(Communicate), getParent(), Communicate.createCallback("__Communicate16__Output__callback")));
    }

    /**
     * Virtual method handler for MakeFight method.
     * 
     * @throws AVEInternaException
     *     Throws when message has an invalid instance.
     */
    public void MakeFight(Message MakeFight)
        throws AVEInternaException
    {
        userLogic.MakeFight(((org.ave.realm.model.Citizen) MakeFight.getData()), new Flow<org.ave.realm.model.Citizen>(getSelfRouted(MakeFight), getParent(), MakeFight.createCallback("__MakeFight17__Output__callback")));
    }

    /**
     * Method call handler for SelectAction method.
     * 
     * @param SelectAction
     *     Input node SelectAction the method
     * @throws AVEInternaException
     *     Throws when message has invalid instance.
     */
    public void SelectAction(Message SelectAction)
        throws AVEInternaException
    {
        UserCodeBlocks10 .tell(SelectAction.createDirectCall("UserCodeBlocks", "SelectHighestPriority", 10, false), getSelfRouted(SelectAction));
    }

    /**
     * Spawn new sub-instance of that actor
     * 
     * @param methodName
     *     Method to spawn
     */
    @Override
    protected ActorRef createSelfInstance(String methodName) {
        return getContext().actorOf(Props.create(CitizenActor.class, new CitizenActor.Creator(fabricProps, methodName, new String[] { })));
    }

    public static class Creator
        implements akka.japi.Creator<CitizenActor>
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
        public CitizenActor create() {
            return new CitizenActor(vprops, methodName, methodArgs);
        }

    }

    public static abstract class UserLogic
        extends VisualActorImpl
    {


        /**
         * User code block handler - Communicate.
         * 
         * @param Output
         *     Block results flow
         * @param Communicate
         *     Unboxed message data
         */
        public abstract void Communicate(org.ave.realm.model.Citizen Communicate, Flow<org.ave.realm.model.Citizen> Output);

        /**
         * User code block handler - Wandering.
         * 
         * @param Output
         *     Block results flow
         * @param Wandering
         *     Unboxed message data
         */
        public abstract void Wandering(org.ave.realm.model.Citizen Wandering, Flow<org.ave.realm.model.Citizen> Output);

        /**
         * User code block handler - MakeFight.
         * 
         * @param MakeFight
         *     Unboxed message data
         * @param Output
         *     Block results flow
         */
        public abstract void MakeFight(org.ave.realm.model.Citizen MakeFight, Flow<org.ave.realm.model.Citizen> Output);

        /**
         * User code block handler - GoEat.
         * 
         * @param Output
         *     Block results flow
         * @param GoEat
         *     Unboxed message data
         */
        public abstract void GoEat(org.ave.realm.model.Citizen GoEat, Flow<org.ave.realm.model.Citizen> Output);

        /**
         * User code block handler - GoSleep.
         * 
         * @param GoSleep
         *     Unboxed message data
         * @param Output
         *     Block results flow
         */
        public abstract void GoSleep(org.ave.realm.model.Citizen GoSleep, Flow<org.ave.realm.model.Citizen> Output);

        /**
         * User code block handler - GoHunt.
         * 
         * @param Output
         *     Block results flow
         * @param GoHunt
         *     Unboxed message data
         */
        public abstract void GoHunt(org.ave.realm.model.Citizen GoHunt, Flow<org.ave.realm.model.Citizen> Output);

        /**
         * User code block handler - SelectHighestPriority.
         * 
         * @param Hunt
         *     Block results flow
         * @param Sleep
         *     Block results flow
         * @param Eat
         *     Block results flow
         * @param Communicate
         *     Block results flow
         * @param SelectHighestPriority
         *     Unboxed message data
         * @param Fight
         *     Block results flow
         * @param Wander
         *     Block results flow
         * @param Child
         *     Block results flow
         */
        public abstract void SelectHighestPriority(org.ave.realm.model.Citizen SelectHighestPriority, Flow<org.ave.realm.model.Citizen> Eat, Flow<org.ave.realm.model.Citizen> Sleep, Flow<org.ave.realm.model.Citizen> Hunt, Flow<org.ave.realm.model.Citizen> Wander, Flow<org.ave.realm.model.Citizen> Communicate, Flow<org.ave.realm.model.Citizen> Fight, Flow<org.ave.realm.model.Citizen> Child);

    }

}
