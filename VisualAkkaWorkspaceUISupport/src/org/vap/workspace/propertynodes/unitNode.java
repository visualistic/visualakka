/* 
 */
package org.vap.workspace.propertynodes;

import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.vap.core.model.macro.ConcreticisedMethod;
import org.vap.core.model.macro.Router;
import org.vap.workspace.WorkspaceScene;

/**
 *
 * @author Oleg Bantysh
 */
public class unitNode extends AbstractNode {

    public ConcreticisedMethod m;
    private WorkspaceScene ws;
    //Router options
    private Sheet.Set routerSet;
    //Supervising options
    private Sheet.Set svstset;
    //Default values options
    private Sheet.Set dvset;
    private Sheet sheet;

    public unitNode(ConcreticisedMethod m, WorkspaceScene ws) {
        super(Children.LEAF);
        this.m = m;
        this.setDisplayName(m.getMethodName());
        this.ws = ws;
    }

    @Override
    protected Sheet createSheet() {
        sheet = Sheet.createDefault();
        Sheet.Set genset = Sheet.createPropertiesSet();
        genset.put(new InstanceTypeProperty());
        //genset.put(new SelectorTypeProperty());
        genset.setDisplayName("General");
        genset.setName("General");
        sheet.put(genset);
        dvset = Sheet.createPropertiesSet();
        for (String k : m.getProperties().keySet()) {
            Property p = new UnitProperty(k);
            try {
                p.setValue(m.getProperties().get(k));
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
            dvset.put(p);
        }
        dvset.setDisplayName("Default values");
        dvset.setName("Default values");
        sheet.put(dvset);
//        Property p = new EnumProperty(m.getSs(), "Supervisor strategy", String.class,
//        "SS", "SS");
        svstset = Sheet.createPropertiesSet();
        Property p = new SupVisStratProperty();
        svstset.put(p);
        svstset.put(new WTRProperty());
        svstset.put(new MaxNumRetriesProperty());
        svstset.setDisplayName("Supervising");
        svstset.setName("Supervising");
        sheet.put(svstset);
        routerSet = Sheet.createPropertiesSet();
        routerSet.setDisplayName("Routing");
        routerSet.setName("Routing");
        //routerSet.put(new HasRouterProperty());
        if (m.router == null) {
            m.router = new Router();
        }
        routerSet.put(new RoutingLogicProperty());
            routerSet.put(new StretchingProperty());
            if (m.router.isIsStretched()) {
                routerSet.put(new MinRoutesProperty());
                routerSet.put(new MaxRoutesProperty());
                routerSet.put(new MaxMBCapacityProperty());
            } else {
                routerSet.put(new StaticRoutesProperty());
            }
        sheet.put(routerSet);
        switch (m.getActorscope()) {
            case Self: {
                sheet.remove(routerSet.getName());
                sheet.remove(dvset.getName());
                sheet.remove(svstset.getName());
            }
            break;
            case Prototype: {
                sheet.put(dvset);
                sheet.put(svstset);
                sheet.remove(routerSet.getName());
            }
            break;
            case Singleton: {
                sheet.put(dvset);
                sheet.put(svstset);
                sheet.remove(routerSet.getName());
            }
            break;
            case Routed: {
                sheet.put(dvset);
                sheet.put(svstset);
                sheet.put(routerSet);
            }
        }
        return sheet;
    }

    public class UnitProperty extends PropertySupport.ReadWrite {

        String propName;

        public UnitProperty(String name) {
            super(name, String.class, name, name);
            propName = name;
        }

        @Override
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return m.getProperties().get(propName);
        }

        @Override
        public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            if (t instanceof String) {
                if (m.getProperties().get(propName) == null
                        || !(m.getProperties().get(propName)).equals((String) t)) {
                    m.getProperties().put(propName, (String) t);
                    ws.load();
                }
                m.getProperties().put(propName, (String) t);
            } else {
                if (m.getProperties().get(propName) == null
                        || !(m.getProperties().get(propName)).equals(((org.vap.core.model.micro.Property) t).value)) {
                    m.getProperties().put(propName, (String) t);
                    ws.load();
                }
                m.getProperties().put(propName, (String) t);
            }
        }

    }

    public class InstanceTypeProperty extends PropertySupport.ReadWrite {

        public InstanceTypeProperty() {
            super("Actor scope", ConcreticisedMethod.InstancingType.class, "Actor scope", "Actor scope");

        }

        @Override
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return m.getActorscope();
        }

        @Override
        public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            if (m.getActorscope() != (ConcreticisedMethod.InstancingType) t) {
                m.setActorscope(((ConcreticisedMethod.InstancingType) t).name());
                switch (m.getActorscope()) {
                    case Self: {
                        sheet.remove(routerSet.getName());
                        sheet.remove(dvset.getName());
                        sheet.remove(svstset.getName());
                    }
                    break;
                    case Prototype: {
                        sheet.put(dvset);
                        sheet.put(svstset);
                        sheet.remove(routerSet.getName());
                    }
                    break;
                    case Singleton: {
                        sheet.put(dvset);
                        sheet.put(svstset);
                        sheet.remove(routerSet.getName());
                    }
                    break;
                    case Routed: {
                        sheet.put(dvset);
                        sheet.put(svstset);
                        sheet.put(routerSet);
                    }
                }
                ws.load();
            }
        }
    }

    public class SelectorTypeProperty extends PropertySupport.ReadWrite {

        public SelectorTypeProperty() {
            super("Selctor type", ConcreticisedMethod.SelectorType.class, "Selector type", "Selector type");
        }

        @Override
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return m;
        }

        @Override
        public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            if (m.getSelType() != (ConcreticisedMethod.SelectorType) t) {
                m.setSelType((ConcreticisedMethod.SelectorType) t);
                ws.load();
            }
        }
    }

    public class SupVisStratProperty extends PropertySupport.ReadWrite {

        public SupVisStratProperty() {
            super("Supervisor strategy", ConcreticisedMethod.SupervisingStrategy.class, "Supervisor strategy", "Supervisor strategy");

        }

        @Override
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return m.supvisstrat;
        }

        @Override
        public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            m.supvisstrat = (ConcreticisedMethod.SupervisingStrategy) t;
        }

    }

    public class WTRProperty extends PropertySupport.ReadWrite {

        public WTRProperty() {
            super("Within time range", String.class, "Within time range",
                    "Within time range");
        }

        @Override
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return m.withTimeRange;
        }

        @Override
        public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            m.withTimeRange = (String) t;
        }

    }

    public class RoutingLogicProperty extends PropertySupport.ReadWrite {

        public RoutingLogicProperty() {
            super("Routing logic", Router.RouterLogic.class, "Routing logic", "Routing logic");
        }

        @Override
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return m.router.getLogic();
        }

        @Override
        public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            boolean update = m.router.getLogic() != (Router.RouterLogic) t;
            m.router.setLogic((Router.RouterLogic) t);
            if (update) {
                ws.load();
            }

        }

    }

    public class StretchingProperty extends PropertySupport.ReadWrite {

        public StretchingProperty() {
            super("Stretching", boolean.class, "Stretching", "Stretching");
        }

        @Override
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return m.router.isIsStretched();
        }

        @Override
        public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            boolean update = false;
            if (!m.router.isIsStretched() && (Boolean) t) {
                routerSet.remove("Routees amount");
                routerSet.put(new MinRoutesProperty());
                routerSet.put(new MaxRoutesProperty());
                routerSet.put(new MaxMBCapacityProperty());
                update = true;
            } else if (m.router.isIsStretched() && !((Boolean) t)) {
                routerSet.remove("Min routees");
                routerSet.remove("Max routees");
                routerSet.remove("Max mailbox capacity");
                routerSet.put(new StaticRoutesProperty());
                update = true;
            }
            m.router.setIsStretched((Boolean) t);
            if (update) {
                ws.load();
            }
        }
    }

    public class MinRoutesProperty extends PropertySupport.ReadWrite {

        public MinRoutesProperty() {
            super("Min routees", Integer.class, "Min routees", "Min routees");
        }

        @Override
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return m.router.getMinRoutes();
        }

        @Override
        public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            boolean update = m.router.getMinRoutes() != (Integer) t;
            m.router.setMinRoutes((Integer) t);
            if (update) {
                ws.load();
            }
        }
    }

    public class MaxRoutesProperty extends PropertySupport.ReadWrite {

        public MaxRoutesProperty() {
            super("Max routees", Integer.class, "Max routees", "Max routees");
        }

        @Override
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return m.router.getMaxRoutes();
        }

        @Override
        public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            boolean update = m.router.getMaxRoutes() != (Integer) t;
            m.router.setMaxRoutes((Integer) t);
            if (update) {
                ws.load();
            }
        }
    }

    public class MaxNumRetriesProperty extends PropertySupport.ReadWrite {

        public MaxNumRetriesProperty() {
            super("Max number of retries", Integer.class, "Max number of retries",
                    "Max number of retries");
        }

        @Override
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return m.maxnumofretr;
        }

        @Override
        public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            m.maxnumofretr = (Integer) t;
        }
    }

    public class MaxMBCapacityProperty extends PropertySupport.ReadWrite {

        public MaxMBCapacityProperty() {
            super("Max mailbox capacity", Integer.class, "Max mailbox capacity",
                    "Max mailbox capacity");
        }

        @Override
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return m.router.getMaxMBCapacity();
        }

        @Override
        public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            m.router.setMaxMBCapacity((Integer) t);
        }
    }

    public class StaticRoutesProperty extends PropertySupport.ReadWrite {

        public StaticRoutesProperty() {
            super("Routees amount", Integer.class, "Routees amount", "Routees amount");
        }

        @Override
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return m.router.getMinRoutes();
        }

        @Override
        public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            m.router.setMinRoutes((Integer) t);
            ws.load();
        }
    }

    public class HasRouterProperty extends PropertySupport.ReadWrite {

        public HasRouterProperty() {
            super("Enabled", boolean.class, "Enabled", "Enabled");
        }

        @Override
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return m.router != null;
        }

        @Override
        public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            boolean update = false;
            if (((Boolean) t) && m.router == null) {
                m.router = new Router();
                routerSet.put(new RoutingLogicProperty());
                routerSet.put(new StretchingProperty());
                if (m.router.isIsStretched()) {
                    routerSet.put(new MinRoutesProperty());
                    routerSet.put(new MaxRoutesProperty());
                    routerSet.put(new MaxMBCapacityProperty());
                } else {
                    routerSet.put(new StaticRoutesProperty());
                }
                update = true;
            } else if (!((Boolean) t) && m.router != null) {
                routerSet.remove("Routing logic");
                routerSet.remove("Stretching");
                if (m.router.isIsStretched()) {
                    routerSet.remove("Min routees");
                    routerSet.remove("Max routees");
                    routerSet.remove("Max mailbox capacity");
                } else {
                    routerSet.remove("Routees amount");
                }
                m.router = null;
                update = true;
            }
            if (update) {
                ws.load();
            }
        }

    }
}
