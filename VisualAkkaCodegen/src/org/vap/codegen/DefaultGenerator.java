/* 
 */
package org.vap.codegen;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.Creator;
import com.sun.codemodel.JArray;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JSwitch;
import com.sun.codemodel.JVar;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.openide.util.lookup.ServiceProvider;
import org.ave.core.Flow;

import org.ave.core.Message;
import org.ave.core.VisualActor;
import org.ave.core.Node;
import org.ave.core.VisualActorImpl;
import org.ave.core.VisualProps;
import org.ave.core.exceptions.AVEInternaException;

import org.vap.core.codeexecuter.AbstractGenerator;
import org.vap.core.model.macro.ConcreticisedMethod;
import org.vap.core.model.macro.Connection;
import org.vap.core.model.macro.Entry;
import org.vap.core.model.macro.Exit;
import org.vap.core.model.macro.StateSetter;
import org.vap.core.model.macro.UserCodeBlock;
import org.vap.core.model.macro.VFLayer;
import org.vap.core.model.macro.Workspace;
import org.vap.core.model.macro.WorkspaceObject;
import org.vap.core.model.micro.Argument;
import org.vap.core.model.micro.Result;

/**
 *
 * @author Vladislav Larin
 */
@ServiceProvider(service = AbstractGenerator.class)
public class DefaultGenerator implements AbstractGenerator {

    private static final String CREATECALLBACK = "createCallback";
    private static final String CREATEDIRECT = "createDirectCall";

    private static final String NEW_DECL = "_NEW";
    private static final String UCB_DECLPREFIX = "_UCB_";
    private static final String UCB_ABSTRACT = "UserLogic";
    private static final String UCB_ABSTRACT_FIELD = "userLogic";
    private static final String UCB_IMPLSUFF = "Impl";

    private int cuid = 0;
    private HashMap<String, JVar> vmethargs;

    private JDefinedClass creator = null;

    private UnitObject compile(Workspace rawUnit) {

        UnitObject comp = new UnitObject(rawUnit);

        //TODO: add various optimizations
        return comp;
    }

    private void build(UnitObject unit, String rootFolder) {
        try {
            vmethargs = new HashMap<String, JVar>();
            cuid = 0;

            /* Creating java code model classes */
            JCodeModel jm = new JCodeModel();

            /* Adding packages here */
            JPackage jp = jm._package(unit.modulePackage);

            /* Giving Class Name to Generate */
            JDefinedClass jc = jp._class(unit.moduleName)._extends(jm.ref(VisualActor.class));

            File UCBImpl = new File(rootFolder + "/" + unit.modulePackage.replace('.', '/') + "/" + unit.moduleName + UCB_IMPLSUFF + ".java");
            JDefinedClass uiabs = jc._class(JMod.PUBLIC | JMod.STATIC | JMod.ABSTRACT, UCB_ABSTRACT)._extends(jm.ref(VisualActorImpl.class));
            JFieldRef ucbIntVar = JExpr.ref("userCodeImplementation");

            /* Adding class level coment */
            JDocComment jDocComment = jc.javadoc();
            jDocComment.add("Generated code! DO NOT MODIFY!");

            //boolean isFixedExists = false;
            //JInvocation defthis = JExpr.invoke("this");
            /* Def method enum */
            //methodsEnum = jc._enum("MethodName");
            generateActorCreator(jm, jc);

            JMethod cons = jc.constructor(JMod.PUBLIC);

            JDefinedClass uimpl = null;
            JClass ucbType;
            if (!UCBImpl.exists()) {
                uimpl = jp._class(JMod.PUBLIC, unit.moduleName + UCB_IMPLSUFF)._extends(uiabs);
                uimpl.javadoc().add("User code logic implementation");

                ucbType = uimpl;

            } else {
                ucbType = jm.ref(unit.modulePackage + '.' + unit.moduleName + UCB_IMPLSUFF);
            }

            JVar ucbVar = jc.field(JMod.PRIVATE, ucbType, UCB_ABSTRACT_FIELD);

            cons.body().assign(ucbIntVar, JExpr._new(ucbType));
            cons.body().assign(ucbVar, JExpr.cast(ucbType, ucbIntVar));

            JVar visualProps = cons.param(jm.ref(VisualProps.class), "vprops");
            JVar selectedMethod = cons.param(jm.ref(String.class), "methodName");
            JVar selectedArgs = cons.param(jm.ref(String.class).array(), "methodArgs");

            JClass _ca1tp = jm.ref(LinkedList.class).narrow(jm.ref(List.class).narrow(String.class));
            JClass _ca2tp = jm.ref(LinkedList.class).narrow(jm.ref(Node.class));

            JVar _ca1 = cons.body().decl(_ca1tp, "methods", JExpr._new(_ca1tp));
            JVar _ca2 = cons.body().decl(_ca2tp, "nodes", JExpr._new(_ca2tp));

            JSwitch methodSwitch = cons.body()._switch(selectedMethod);
            JBlock registryInit = methodSwitch._case(JExpr.lit(NEW_DECL)).body();

            JMethod callbackHandler = jc.method(JMod.PROTECTED, jm.VOID, "handleCallback");
            callbackHandler.annotate(Override.class);
            callbackHandler._throws(AVEInternaException.class);
            JVar callback = callbackHandler.param(jm.ref(Message.class), "callback");

            callbackHandler.javadoc().add("Callback handler for visual actor.");
            callbackHandler.javadoc().addParam(callback).
                    add("Message that was returned by called child before");
            callbackHandler.javadoc().addThrows(AVEInternaException.class).
                    add("Throwed when message has invalid instance");

            JMethod postInitHandler = jc.method(JMod.PROTECTED, jm.VOID, "postInitialize");
            postInitHandler.annotate(Override.class);
            postInitHandler.javadoc().add("Creates actors children instances on demand.");

            /* Adding callback body */
            JSwitch callbackBody = callbackHandler.body()._switch(callback.invoke("getCallback"));

            //Dictionary with registered entries
            Map<String, JBlock> ifBlocks = new HashMap<String, JBlock>();
            Map<JBlock, JConditional> stateIfBlocks = new HashMap<JBlock, JConditional>();

            Map<String, JVar> fields = new HashMap<String, JVar>();
            Map<String, JMethod> virtualMethods = new HashMap<String, JMethod>();

            List<String> entriesNames = new LinkedList<String>();

            for (VFLayer layer : unit.layers) {

                for (ConcreticisedMethod block : layer.units) {
                    if (block instanceof UserCodeBlock) {

                        introduceUserCodeBlock(jc, jm, block, methodSwitch, registryInit, _ca2, _ca1, virtualMethods);
                    }
                }

                String methCodeName = layer.methodName.replace(' ', '_');

                JBlock currMethSwith = methodSwitch._case(JExpr.lit(methCodeName)).body();

                List<String> methodNames = new LinkedList<String>();
                int entryId = 0;
                for (Entry e : layer.entries) {
                    String entryName = e.getRefArg().getName();
                    if (entriesNames.contains(entryName)) {
                        continue;
                    }

                    String defaultV = e.getRefArg().getDefaultValue();

                    JInvocation nodeInv;

                    if (e.getRefArg().isFixed()) {
                        //JVar evar = cons.param(String.class, e.getRefArg().getIdentificator());
//                        defthis.arg(defaultV == null ? "" : defaultV);

                        nodeInv = _ca2.invoke("add").arg(
                                JExpr._new(jm.ref(Node.class))
                                .arg(entryName)
                                .arg(e.getRefArg().getType())
                                .arg(selectedArgs.component(JExpr.lit(entryId)))
                                .arg(JExpr.lit(true)));

                        entryId++;

                    } else {
                        nodeInv = _ca2.invoke("add").arg(
                                JExpr._new(jm.ref(Node.class))
                                .arg(entryName)
                                .arg(e.getRefArg().getType())
                                .arg(defaultV == null ? "" : defaultV)
                                .arg(JExpr.lit(false)));
                    }

                    currMethSwith.add(nodeInv);
                    registryInit.add(nodeInv);
                    entriesNames.add(entryName);
                    methodNames.add(entryName);
                }

                JArray mthNames = JExpr.newArray(jm.ref(String.class));

                for (String name : methodNames) {
                    mthNames.add(JExpr.lit(name));
                }

                JInvocation methInd = _ca1.invoke("add").arg(jm.ref(Arrays.class)
                        .staticInvoke("asList").arg(mthNames));

                currMethSwith.add(methInd);
                registryInit.add(methInd);

                currMethSwith._break();

                /* Adding method to the Class */
                JMethod methHandler = jc.method(JMod.PUBLIC, jm.VOID, layer.methodName.replace(' ', '_'));
                methHandler._throws(AVEInternaException.class);
                methHandler.javadoc().add("Method call handler for " + layer.methodName + " method.");
                methHandler.javadoc().addThrows(AVEInternaException.class).
                        add("Throws when message has invalid instance.");

                Map<String, JVar> args = new HashMap<String, JVar>();
                for (Entry in : layer.entries) {
                    String id = in.getRefArg().getIdentificator();
                    args.put(id, methHandler.param(jm.ref(Message.class), id));
                    methHandler.javadoc().addParam(id).add("Input node " + id + " the method");
                }

                for (Connection conn : layer.getConnections()) {
                    WorkspaceObject in = layer.getObjectById(conn.sourceCMID);
                    WorkspaceObject out = layer.getObjectById(conn.targetCMID);

                    boolean isStarter = false;

                    String entryName;
                    if (in instanceof Entry) {
                        entryName = conn.getSourcePinID();
                        isStarter = true;
                    } else if (in instanceof ConcreticisedMethod) {
                        entryName = "__" + ((ConcreticisedMethod) in).getMethodName()
                                + in.getCmID() + "__" + conn.getSourcePinID() + "__" + "callback";
                    } else {
                        //TODO: error log
                        throw new AVEInternaException("Invalid connection!");
                    }

                    JBlock sendb = methHandler.body();

                    JVar sendv = isStarter ? args.get(entryName) : callback;
                    //Callback handler
                    if (!isStarter) {

                        JBlock cond;
                        if (!ifBlocks.containsKey(entryName)) {
                            cond = callbackBody._case(JExpr.lit(entryName)).body();

                            ifBlocks.put(entryName, cond);
                        } else {
                            cond = ifBlocks.get(entryName);
                        }

                        sendb = cond;
                    }

                    //State handler (Event centristic, not State)
                    if (conn.relatedState != null) {
                        JConditional cond;
                        if (!stateIfBlocks.containsKey(sendb)) {
                            cond = sendb._if(JExpr.invoke("getState").
                                    invoke("equals").arg(JExpr.lit(conn.relatedState.getName())));

                            stateIfBlocks.put(sendb, cond);
                        } else {
                            cond = stateIfBlocks.get(sendb);
                        }

                        sendb = cond._then();
                    }

                    JExpression selfOrRt = JExpr.invoke("getSelfRouted").arg(sendv);
                    JExpression sender = JExpr.invoke("getParent");

                    if (out instanceof Exit) {
                        JVar resultId = sendb.decl(jm.ref(String.class), "nnm" + out.getCmID() + '_' + cuid++,
                                JExpr.lit("__" + layer.methodName).
                                plus(sendv.invoke("getTargetId").plus(JExpr.lit("__" + conn.getTargetPinID() + "__callback"))));

                        sendb.add(sender.invoke("tell").
                                arg(sendv.invoke(CREATECALLBACK).arg(resultId)).arg(selfOrRt));
                    } else if (out instanceof StateSetter) {
                        StateSetter stset = (StateSetter) out;
                        sendb.add(JExpr.invoke("setState").arg(stset.getRefState().getName()));
                    } else if (out instanceof ConcreticisedMethod) {

                        ConcreticisedMethod mth = (ConcreticisedMethod) out;
                        String moduleName = mth.getModuleID();

                        //User code block generator
                        if (mth.type == ConcreticisedMethod.CMType.UCB) {
                            moduleName = unit.modulePackage + "." + unit.moduleName;

                            if (virtualMethods.containsKey(mth.getMethodName())) {

                                JBlock vsendb = virtualMethods.get(mth.getMethodName()).body();

                                /* User method handler generation */
                                String vmname = mth.getMethodName().replace(' ', '_');
                                //JClass resType = jm.ref(mth.getReferencedMethod().getResults().get(0).getType());

                                JMethod vmethImpl = null;
                                if (uimpl != null) {
                                    vmethImpl = uimpl.method(JMod.PUBLIC, jm.VOID, vmname);
                                    vmethImpl.javadoc().add("User code block implementation for - " + mth.getMethodName() + '.');
                                    //vmethImpl.javadoc().addReturn().add("Returns result of the calculations.");

                                    vmethImpl.annotate(Override.class);

                                    //vmethImpl.body()._return(JExpr._null());
                                }
                                JMethod vmethHandler = uiabs.method(JMod.PUBLIC | JMod.ABSTRACT, jm.VOID, vmname);
                                vmethHandler.javadoc().add("User code block handler - " + mth.getMethodName() + ".");
                                //vmethHandler.javadoc().addReturn().add("Returns result of the calculations.");

                                JInvocation vargs = ucbVar.invoke(vmethHandler);
                                int argId = 0;
                                for (Argument a : mth.getRefMeth().getArguments()) {
                                    if (vmethImpl != null) {
                                        vmethImpl.param(jm.ref(a.getType()), a.getIdentificator());
                                        vmethImpl.javadoc().addParam(a.getIdentificator()).add("Unboxed message data");
                                    }

                                    vmethHandler.param(jm.ref(a.getType()), a.getIdentificator());
                                    vmethHandler.javadoc().addParam(a.getIdentificator()).add("Unboxed message data");
                                    JExpression data = JExpr.cast(jm.ref(a.getType()), vmethargs.get(mth.getMethodName() + argId).invoke("getData"));

                                    vargs.arg(data);

                                    argId++;
                                }

                                JVar vsendv = vmethargs.get(mth.getMethodName() + "0");
                                argId = 0;
                                for (Result r : mth.getRefMeth().getResults()) {
                                    JExpression resultId = JExpr.lit("__" + mth.getMethodName() + out.getCmID() + "__"
                                            + r.getIdentificator() + "__callback");
                                    JClass flowType = jm.ref(Flow.class).narrow(jm.ref(r.getType()));

                                    if (vmethImpl != null) {
                                        vmethImpl.param(flowType, r.getIdentificator());
                                        vmethImpl.javadoc().addParam(r.getIdentificator()).add("Block results flow");
                                    }

                                    vmethHandler.param(flowType, r.getIdentificator());
                                    vmethHandler.javadoc().addParam(r.getIdentificator()).add("Block results flow");

                                    vargs.arg(JExpr._new(flowType).
                                            arg(JExpr.invoke("getSelfRouted").arg(vsendv)).
                                            arg(sender).
                                            arg(vsendv.invoke(CREATECALLBACK).arg(resultId)));

                                    argId++;
                                }

                                vsendb.add(vargs);

                                /* End */
                                virtualMethods.remove(mth.getMethodName());
                            }
                        }

                        String[] missRaw = mth.getModuleID().split("\\.");
                        String name = missRaw[missRaw.length - 1];
                        //String uname = Character.toUpperCase(name.charAt(0)) + name.substring(1);

                        boolean isRouted = mth.router != null;
                        boolean isSelf = mth.selType == ConcreticisedMethod.SelectorType.Self;

                        String methodName = (mth.type == ConcreticisedMethod.CMType.ConcreticisedMethod ? "" : UCB_DECLPREFIX) + mth.getMethodName();

                        JVar dest = null;
                        if (fields.containsKey(name + conn.targetCMID)) {
                            dest = fields.get(name + conn.targetCMID);
                        } else if (!isSelf) {
                            JVar actorDecl = postInitHandler.body().decl(jm.ref(Props.class),
                                    name + conn.targetCMID + "_decl",
                                    getActorRef(jm, mth, moduleName, methodName));

                            dest = jc.field(JMod.PRIVATE, jm.ref(ActorRef.class), name + conn.targetCMID);
                            JExpression ini;

                            if (isRouted) {
                                // getContext().actorOf(new RoundRobinPool(5).props(Props.create(Worker.class)), "router2");
                                ini = JExpr.invoke("getContext").invoke("actorOf").
                                        arg(JExpr._new(jm.ref(mth.router.getLogicPool())).
                                                arg(JExpr.lit(mth.router.getMinRoutes())).invoke("props").
                                                arg(actorDecl)).
                                        arg(name + conn.targetCMID);

                            } else {
                                ini = JExpr.invoke("getContext").invoke("actorOf").
                                        arg(actorDecl).
                                        arg(name + conn.targetCMID);
                            }

                            postInitHandler.body().assign(dest, ini);

                            fields.put(name + conn.targetCMID, dest);
                        }

                        if (conn.extractedFieldName != null && !conn.extractedFieldName.isEmpty()) {

                            JExpression dataP;
                            Entry t = layer.getEntryByName(conn.sourceCMID);
                            if (t != null) {
                                dataP = JExpr.cast(jm.ref(t.getRefArg().getType()),
                                        sendv.invoke("getData"));
                            } else {
                                dataP = JExpr.cast(jm.ref(layer
                                        .getUnitByID(conn.sourceCMID)
                                        .getRefMeth()
                                        .getArgumentByName(conn.sourcePinName)
                                        .getType()),
                                        sendv.invoke("getData"));
                            }

                            sendb.add((isSelf ? selfOrRt : dest).invoke("tell").arg(sendv.invoke(CREATEDIRECT)
                                    .arg(dataP.invoke(conn.extractedFieldName))
                                    .arg(name)
                                    .arg(conn.getTargetPinID())
                                    .arg(JExpr.lit(Integer.parseInt(conn.targetCMID)))
                                    .arg(JExpr.lit(isRouted))).arg(selfOrRt));
                        } else {
                            sendb.add((isSelf ? selfOrRt : dest).invoke("tell").arg(sendv.invoke(CREATEDIRECT)
                                    .arg(name)
                                    .arg(conn.getTargetPinID())
                                    .arg(JExpr.lit(Integer.parseInt(conn.targetCMID)))
                                    .arg(JExpr.lit(isRouted))).arg(selfOrRt));
                        }

                    } else {
                        //TODO: error log
                        throw new AVEInternaException("Invalid connection!");
                    }
                }
            }

//            if (isFixedExists) {
//                JMethod defcons = jc.constructor(JMod.PUBLIC);
//                defcons.body().add(defthis);
//            }
            JConditional isFabric = cons.body()._if(selectedMethod.invoke("equals").arg(JExpr.lit(NEW_DECL)));
            isFabric._then().add(JExpr.invoke("initActorFabric").arg(visualProps).arg(_ca1).arg(_ca2));
            isFabric._else().invoke("initSupervisor").arg(visualProps.invoke("getSupStrategy")).arg(visualProps.invoke("getSupNumOfRetries")).arg(visualProps.invoke("getSupDuration"));
            isFabric._else().add(JExpr.invoke("init").arg(visualProps.invoke("getParentPath")).arg(_ca1).arg(_ca2).arg(JExpr.lit("DEFAULT")));

            //Adding breaks to the callback handler
            for (JBlock ifBlck : ifBlocks.values()) {
                ifBlck._break();
            }

            registryInit._break();

            JMethod selfCreator = jc.method(JMod.PROTECTED, jm.ref(ActorRef.class), "createSelfInstance");
            JVar selfMethName = selfCreator.param(jm.ref(String.class), "methodName");

            selfCreator.annotate(jm.ref(Override.class));

            selfCreator.javadoc().add("Spawn new sub-instance of that actor");
            selfCreator.javadoc().addParam(selfMethName).add("Method to spawn");

            selfCreator.body()._return(JExpr.invoke("getContext").invoke("actorOf").
                    arg(getSelfActorRef(jm, jc, selfMethName, unit)).
                    arg("instance"));

            /* Building class at given location */
            File output = new File(rootFolder);
            jm.build(output, System.out);

        } catch (JClassAlreadyExistsException ex) {
            //logger.log(Level.SEVERE, "Other Exception which in not catched:" + ex);
            System.err.println("DEBUG Exception: " + ex.getMessage());
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            //logger.log(Level.SEVERE, "Other Exception which in not catched:" + ex);
            System.err.println("DEBUG Exception: " + ex.getMessage());
            ex.printStackTrace();
        } catch (AVEInternaException ex) {
            //logger.log(Level.SEVERE, "Other Exception which in not catched:" + ex);
            System.err.println("DEBUG Exception: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            //logger.log(Level.SEVERE, "Other Exception which in not catched:" + ex);
            System.err.println("DEBUG Exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void introduceUserCodeBlock(JDefinedClass jc, JCodeModel jm,
            ConcreticisedMethod block,
            JSwitch methodSwitch,
            JBlock registryInit,
            JVar _ca2, JVar _ca1,
            Map<String, JMethod> virtualMethods) {

        JMethod vmethhandler = jc.method(JMod.PUBLIC,
                jm.VOID,
                block.getRefMeth().getArguments().get(0).getIdentificator());

        vmethhandler
                ._throws(AVEInternaException.class
                );
        vmethhandler.javadoc()
                .add("Virtual method handler for " + block.getMethodName() + " method.");
        vmethhandler.javadoc()
                .addThrows(AVEInternaException.class
                ).
                add("Throws when message has an invalid instance.");

        JBlock userBlockSwitch = methodSwitch._case(JExpr.lit(UCB_DECLPREFIX + block.getMethodName())).body();
        JArray mthNames = JExpr.newArray(jm.ref(String.class));

        int argId = 0;
        for (Argument arg
                : block.getRefMeth()
                .getArguments()) {
            String defaultV = arg.getDefaultValue();
            JInvocation node = _ca2.invoke("add").arg(
                    JExpr._new(jm.ref(Node.class))
                    .arg(arg.getIdentificator())
                    .arg(arg.getType())
                    .arg(defaultV == null ? "" : defaultV)
                    .arg(JExpr.lit(arg.isFixed())));

            userBlockSwitch.add(node);
            //registryInit.add(node);

            mthNames.add(JExpr.lit(arg.getIdentificator()));
            vmethargs.put(block.getMethodName() + argId, vmethhandler.param(Message.class, arg.getIdentificator()));

            argId++;
        }

        JInvocation meth = _ca1.invoke("add").arg(jm.ref(Arrays.class)
                .staticInvoke("asList").arg(mthNames));

        userBlockSwitch.add(meth);
        //registryInit.add(meth);

        userBlockSwitch._break();

        virtualMethods.put(block.getMethodName(), vmethhandler);
    }

    private void generateActorCreator(JCodeModel jm, JDefinedClass rt)
            throws JClassAlreadyExistsException {
        //ConcurrentHashMap<String, String> newdef = mth.getProperties();
        //String[] missRaw = mth.getModuleID().split("\\.");
        String name = "Creator";// missRaw[missRaw.length - 1] + "Creator";

        JClass actorT = rt;// jm.ref(module);

        creator = rt._class(JMod.PUBLIC | JMod.STATIC, name).
                _implements(jm.ref(Creator.class
                        ).narrow(actorT));

        JMethod cons = creator.constructor(JMod.PUBLIC);

        JVar parentPath = cons.param(jm.ref(VisualProps.class), "vprops");
        JVar selectedMethod = cons.param(jm.ref(String.class), "methodName");
        JVar selectedArgs = cons.param(jm.ref(String.class).array(), "methodArgs");

        JVar selectedParentPath = creator.field(JMod.PRIVATE | JMod.FINAL, jm.ref(VisualProps.class), "vprops");
        JVar selectedMethodField = creator.field(JMod.PRIVATE | JMod.FINAL, jm.ref(String.class), "methodName");
        JVar selectedArgsField = creator.field(JMod.PRIVATE | JMod.FINAL, jm.ref(String.class).array(), "methodArgs");

        cons.body().assign(JExpr._this().ref(selectedParentPath), parentPath);
        cons.body().assign(JExpr._this().ref(selectedMethodField), selectedMethod);
        cons.body().assign(JExpr._this().ref(selectedArgsField), selectedArgs);

        JMethod cr = creator.method(JMod.PUBLIC, actorT, "create");

        cr.annotate(Override.class);

        JInvocation newA = JExpr._new(actorT);

        newA.arg(selectedParentPath);
        newA.arg(selectedMethodField);
        newA.arg(selectedArgsField);

        cr.body()._return(newA);
    }

    private JExpression getSelfActorRef(JCodeModel jm, JDefinedClass jc, JVar methName, UnitObject unit)
            throws JClassAlreadyExistsException {

        ConcurrentHashMap<String, String> newdef = new ConcurrentHashMap<String, String>();

        //XXX: Check correctness
        for (VFLayer layer : unit.layers) {
            for (Entry conn : layer.entries) {
                if (conn.getRefArg().isFixed()) {
                    newdef.put(conn.getRefArg().getName(), conn.getRefArg().getDefaultValue());
                }
            }
        }

        JClass actorT = jm.ref(unit.moduleName);

        JInvocation newI = JExpr._new(jm.ref(unit.moduleName + ".Creator"));

        newI.arg(JExpr.ref("fabricProps"));

        newI.arg(methName);

        JArray args = JExpr.newArray(jm.ref(String.class));
        for (String defValue
                : newdef.values()) {
            args.add(JExpr.lit(defValue));
        }

        newI.arg(args);

        return jm.ref(Props.class
        ).staticInvoke("create").
                arg(actorT.staticRef("class")).arg(newI);

    }

    private JExpression getActorRef(JCodeModel jm,
            ConcreticisedMethod mth, String module, String method)
            throws JClassAlreadyExistsException {

        ConcurrentHashMap<String, String> newdef = mth.getProperties();

        JClass actorT = jm.ref(module);

        JInvocation newI = JExpr._new(jm.ref(module + ".Creator"));

        //JClass enm = jm.ref(module + ".MethodName");
        String methBuild = method;

        if (mth.actorscope == ConcreticisedMethod.InstancingType.Prototype) {
            methBuild = NEW_DECL;
        }

        newI.arg(JExpr._new(jm.ref(VisualProps.class)).arg(JExpr.invoke("getPath")).
                arg(JExpr.lit(mth.supvisstrat.toString())).
                arg(JExpr.lit(mth.maxnumofretr)).arg(JExpr.lit(mth.withTimeRange)));

        newI.arg(JExpr.lit(methBuild));

        JArray args = JExpr.newArray(jm.ref(String.class));
        for (String defValue
                : newdef.values()) {
            args.add(JExpr.lit(defValue));
        }

        newI.arg(args);

        return jm.ref(Props.class
        ).staticInvoke("create").
                arg(actorT.staticRef("class")).arg(newI);

    }

    @Override
    public void javaGen(Workspace workspace, String rootFolder) {

        cuid = 0;

        //Compile workspace and transform it into more suitable format
        UnitObject unit = compile(workspace);

        //Build and flush to file obtained unit
        build(unit, rootFolder + "/src");
    }
}
