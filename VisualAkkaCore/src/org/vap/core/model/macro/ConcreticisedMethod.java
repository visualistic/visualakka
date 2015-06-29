/* 
 */
package org.vap.core.model.macro;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.openide.util.Lookup;
import org.vap.core.model.micro.Argument;
import org.vap.core.model.micro.Method;
import org.vap.core.model.micro.Point;
import org.vap.core.model.micro.Property;
import org.vap.core.unitmanager.UnitManager;

/**
 *
 * @author Oleg Bantysh
 */
public class ConcreticisedMethod extends WorkspaceObject implements Transferable, Cloneable {

    /**
     * @return the properties
     */
    public ConcurrentHashMap<String, String> getProperties() {
        return properties;
    }

//    /**
//     * @param properties the properties to set
//     */
//    public void setProperties(ConcurrentHashMap<String, Property> properties) {
//        this.properties = properties;
//    }
    /**
     *
     */
    public enum CMType {

        /**
         *
         */
        ConcreticisedMethod,
        /**
         *
         */
        UCB,
        /**
         *
         */
        StaticMethod
    };

    public enum InstancingType {

        Singleton, Prototype, Routed, Self
    };

    public enum SelectorType {

        None, Self, All
    };

    /**
     * @return the refMeth
     */
    public Method getRefMeth() {
        if (refMeth == null) {
            UnitManager um = (UnitManager) Lookup.getDefault().lookup(UnitManager.class);
            refMeth = um.findModule(moduleID).getMethodByName(methodName);
        }
        return refMeth;
    }

    /**
     * @param refMeth the refMeth to set
     */
    public void setRefMeth(Method refMeth) {
        this.refMeth = refMeth;
    }

    /**
     *
     */
    public static enum SupervisingStrategy {

        /**
         *
         */
        Stop,
        /**
         *
         */
        Restart,
        /**
         *
         */
        Escalate,
        /**
         *
         */
        Resume,
        /**
         *
         */
        Custom
    };
    //private static int IDCOUNT = 0;//private static int IDCOUNT = 0;

    /**
     *
     */
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(ConcreticisedMethod.class, "CM");
    private String moduleID;
    private String methodName;
    private Method refMeth;

    /**
     *
     */
    public SupervisingStrategy supvisstrat = SupervisingStrategy.Escalate;

    /**
     *
     */
    public Router router = null;

    /**
     *
     */
    public String withTimeRange = "-1";

    /**
     *
     */
    public int maxnumofretr = Integer.MAX_VALUE;

    /**
     *
     */
    public CMType type = CMType.ConcreticisedMethod;
    //public HashMap<String, String> propertiesToSave = new HashMap();

    public InstancingType iType = InstancingType.Singleton;

    public SelectorType selType = SelectorType.None;

    /**
     *
     */
    public ConcurrentHashMap<String, String> properties = new ConcurrentHashMap();

    /**
     *
     */
    public ConcreticisedMethod() {
    }

    /**
     *
     * @param cmID
     * @param moduleID
     * @param methodName
     * @param loc
     */
    public ConcreticisedMethod(String cmID, String moduleID, String methodName, Point loc) {
        this.cmID = cmID;
        this.moduleID = moduleID;
        this.methodName = methodName;
        this.loc = loc;
        checkID(cmID);
    }

    /**
     *
     * @param cmID
     * @param moduleID
     * @param method
     * @param loc
     */
    public ConcreticisedMethod(String cmID, String moduleID, Method method, Point loc) {
        this.cmID = cmID;
        this.moduleID = moduleID;
        this.methodName = method.getName();
        this.loc = loc;
        this.refMeth = method;
        checkID(cmID);
    }

    /**
     *
     */
    public void UpdateName() {
        this.methodName = refMeth.getName();
    }

    /**
     *
     * @param m
     * @param mid
     * @return
     */
    public static ConcreticisedMethod formCM(Method m, String mid) {
        return new ConcreticisedMethod("" + getCurrIDInc(), mid, m, null);
    }

    public InstancingType getInstancingType() {
        return iType;
    }

    public SelectorType getSelectorType() {
        return selType;
    }

    /**
     * @return the moduleID
     */
    public String getModuleID() {
        return moduleID;
    }

    public String getModuleName() {
        String[] strs = moduleID.split("\\.");
        if(strs.length==0)
            return moduleID;
        return strs[strs.length - 1];
    }

    /**
     * @param moduleID the moduleID to set
     */
    public void setMethodID(String moduleID) {
        this.setModuleID(moduleID);
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @param methodName the methodName to set
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * @return the loc
     */
    public Point getLoc() {
        return loc;
    }

    /**
     * @param loc the loc to set
     */
    public void setLoc(Point loc) {
        this.loc = loc;
    }

    /**
     * @param moduleID the moduleID to set
     */
    public void setModuleID(String moduleID) {
        this.moduleID = moduleID;
    }

//    public Method getReferencedMethod() {
//        if (refMeth == null) {
//            UnitManager um = (UnitManager) Lookup.getDefault().lookup(UnitManager.class);
//            refMeth = um.findModule(moduleID).getMethodByName(methodName);
//        }
//        return refMeth;
//    }
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DATA_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor == DATA_FLAVOR;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor == DATA_FLAVOR) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    /**
     *
     * @param reset
     */
    public void loadDefaultProperties(boolean reset) {
//        Method m = getReferencedMethod();
        Method m = getRefMeth();
        if (reset || getProperties().isEmpty()) {
            for (Argument a : m.getArguments()) {
                if (a.isFixed()) {
                    getProperties().put(a.getName(), a.getDefaultValue());
                }
            }
        }
//        else{
//            for(String s: propertiesToSave.keySet()){
//                getProperties().put(s, propertiesToSave.get(s));
//            }
//        }
    }

//    public void UpdatePropertiesToSave(){
//        propertiesToSave = new HashMap();
//        for(String s: getProperties().keySet()){
//            propertiesToSave.put(s, getProperties().get(s));
//        }
//    }
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

}
