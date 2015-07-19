
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
public class Sum
    extends VisualActor
{

    private SumImpl userLogic;
    private ActorRef UserCodeBlocks45;

    public Sum(VisualProps vprops, String methodName, String[] methodArgs) {
        userCodeImplementation = new SumImpl();
        userLogic = ((SumImpl) userCodeImplementation);
        LinkedList<List<String>> methods = new LinkedList<List<String>>();
        LinkedList<Node> nodes = new LinkedList<Node>();
        switch (methodName) {
            case "_NEW":
                nodes.add(new Node("A", "java.lang.Float", "", false));
                nodes.add(new Node("B", "java.lang.Float", "", false));
                methods.add(Arrays.asList(new String[] {"A", "B"}));
                break;
            case "_UCB_MakeSum":
                nodes.add(new Node("MakeSum", "java.lang.Float", "", false));
                nodes.add(new Node("B", "java.lang.Float", "", false));
                methods.add(Arrays.asList(new String[] {"MakeSum", "B"}));
                break;
            case "A":
                nodes.add(new Node("A", "java.lang.Float", "", false));
                nodes.add(new Node("B", "java.lang.Float", "", false));
                methods.add(Arrays.asList(new String[] {"A", "B"}));
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
            case "__MakeSum45__Result__callback":
            {
                String nnm44_0 = ("__A"+(callback.getTargetId()+"__Result__callback"));
                getParent().tell(callback.createCallback(nnm44_0), getSelfRouted(callback));
                break;
            }
        }
    }

    /**
     * Creates actors children instances on demand.
     * 
     */
    @Override
    protected void postInitialize() {
        Props UserCodeBlocks45_decl = Props.create(org.ave.realm.Sum.class, new org.ave.realm.Sum.Creator(new VisualProps(getPath(), "Escalate", 2147483647, "-1"), "_UCB_MakeSum", new String[] { }));
        UserCodeBlocks45 = getContext().actorOf(UserCodeBlocks45_decl, "UserCodeBlocks45");
    }

    /**
     * Virtual method handler for MakeSum method.
     * 
     * @throws AVEInternaException
     *     Throws when message has an invalid instance.
     */
    public void MakeSum(Message MakeSum, Message B)
        throws AVEInternaException
    {
        userLogic.MakeSum(((Float) MakeSum.getData()), ((Float) B.getData()), new Flow<Float>(getSelfRouted(MakeSum), getParent(), MakeSum.createCallback("__MakeSum45__Result__callback")));
    }

    /**
     * Method call handler for A method.
     * 
     * @param A
     *     Input node A the method
     * @param B
     *     Input node B the method
     * @throws AVEInternaException
     *     Throws when message has invalid instance.
     */
    public void A(Message A, Message B)
        throws AVEInternaException
    {
        UserCodeBlocks45 .tell(A.createDirectCall("UserCodeBlocks", "MakeSum", 45, false), getSelfRouted(A));
        UserCodeBlocks45 .tell(B.createDirectCall("UserCodeBlocks", "B", 45, false), getSelfRouted(B));
    }

    /**
     * Spawn new sub-instance of that actor
     * 
     * @param methodName
     *     Method to spawn
     */
    @Override
    protected ActorRef createSelfInstance(String methodName) {
        return getContext().actorOf(Props.create(Sum.class, new Sum.Creator(fabricProps, methodName, new String[] { })));
    }

    public static class Creator
        implements akka.japi.Creator<Sum>
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
        public Sum create() {
            return new Sum(vprops, methodName, methodArgs);
        }

    }

    public static abstract class UserLogic
        extends VisualActorImpl
    {


        /**
         * User code block handler - MakeSum.
         * 
         * @param B
         *     Unboxed message data
         * @param MakeSum
         *     Unboxed message data
         * @param Result
         *     Block results flow
         */
        public abstract void MakeSum(Float MakeSum, Float B, Flow<Float> Result);

    }

}
