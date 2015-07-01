/* 
 */
package org.vap.fileutils;

import java.io.File;
import java.io.IOException;
import org.vap.core.model.macro.Workspace;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.vap.core.model.macro.ConcreticisedMethod;
import org.vap.core.model.macro.Connection;
import org.vap.core.model.macro.Entry;
import org.vap.core.model.macro.Exit;
import org.vap.core.model.macro.Router;
import org.vap.core.model.macro.State;
import org.vap.core.model.macro.StateSetter;
import org.vap.core.model.macro.UserCodeBlock;
import org.vap.core.model.macro.VFLayer;
import org.vap.core.model.micro.Argument;
import org.vap.core.model.micro.Method;
import org.vap.core.model.micro.Point;
import org.vap.core.model.micro.Result;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Oleg Bantysh
 */
public class WorkspaceParser {
    
    public static Workspace Parse(File file) throws ParserConfigurationException, SAXException, IOException {
        Workspace ws = new Workspace();
        if(file==null){
            return null;
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        dbf.setIgnoringElementContentWhitespace(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        NodeList refMeth = doc.getElementsByTagName("refModuleName");
        ws.setRefModuleName(refMeth.item(0).getTextContent().trim());
        NodeList refPack = doc.getElementsByTagName("refModulePackage");
        ws.setRefModulePackage(refPack.item(0).getTextContent().trim());
        
        NodeList layers = doc.getElementsByTagName("layers");
        for (int i = 0; i < layers.getLength(); i++) {
            Element le = (Element) layers.item(i);
            VFLayer vl = new VFLayer();
            vl.methodName = le.getElementsByTagName("methodName").item(0).getTextContent().trim();
            try{
            NodeList units = le.getElementsByTagName("units");
            for (int j = 0; j < units.getLength(); j++) {
                Element ue = (Element) units.item(j);                
                
                ConcreticisedMethod cm;
                String type = ue.getElementsByTagName("type").item(0).getTextContent().trim();
                switch (type) {
                    case "UCB":
                        cm = new UserCodeBlock();
                        break;
                    default:
                        cm = new ConcreticisedMethod();
                }

                //cmid
                cm.setCmID(ue.getElementsByTagName("cmID").item(0).getTextContent().trim());
                //location
                Element pointEl = (Element) ue.getElementsByTagName("loc").item(0);
                cm.setLoc(new Point(Integer.parseInt(pointEl.getElementsByTagName("x").item(0).getTextContent().trim()),
                        Integer.parseInt(pointEl.getElementsByTagName("y").item(0).getTextContent().trim())));
                
                //actor scope type
                try{
                    cm.actorscope = ConcreticisedMethod.InstancingType.valueOf(ue.getElementsByTagName("actorscope").item(0).getTextContent().trim());
                }catch(Exception e){}
                
                //referenced method
                Element methEl = (Element) ue.getElementsByTagName("refMeth").item(0);
                Method m = new Method();
                m.setName(methEl.getElementsByTagName("name").item(0).getTextContent().trim());
                try{
                NodeList methArgs = methEl.getElementsByTagName("arguments");
                for (int k = 0; k < methArgs.getLength(); k++) {
                    Element argEl = (Element) methArgs.item(k);
                    Argument arg = new Argument();
                    arg.setName(argEl.getElementsByTagName("name").item(0).getTextContent().trim());
                    arg.setType(argEl.getElementsByTagName("type").item(0).getTextContent().trim());
                    arg.setIsMainArg(Boolean.parseBoolean(argEl.getElementsByTagName("isMainArg").item(0).getTextContent().trim()));
                    if (Boolean.parseBoolean(argEl.getElementsByTagName("fixed").item(0).getTextContent().trim())) {
                        arg.setFixed(true);
                        arg.setDefaultValue(argEl.getElementsByTagName("defaultValue").item(0).getTextContent().trim());
                    } else {
                        arg.setFixed(false);
                    }
                    m.addParameter(arg);
                }}catch(Exception e){}
                try{
                NodeList methRes = methEl.getElementsByTagName("results");
                for (int k = 0; k < methRes.getLength(); k++) {
                    Element argEl = (Element) methRes.item(k);
                    Result arg = new Result();
                    arg.setName(argEl.getElementsByTagName("name").item(0).getTextContent().trim());
                    arg.setType(argEl.getElementsByTagName("type").item(0).getTextContent().trim());
                    m.addParameter(arg);
                }}catch(Exception e){}
                cm.setRefMeth(m);
                
                //router
                try{
                Element routerEl = (Element) ue.getElementsByTagName("router").item(0);
                Router r = new Router();
                r.setLogic(Router.RouterLogic.valueOf(routerEl.getElementsByTagName("logic").item(0).getTextContent().trim()));
                r.setIsStretched(Boolean.parseBoolean(routerEl.getElementsByTagName("isStretched").item(0).getTextContent().trim()));
                r.setMinRoutes(Integer.parseInt(routerEl.getElementsByTagName("minRoutes").item(0).getTextContent().trim()));
                if(r.isIsStretched()){
                    r.setMaxRoutes(Integer.parseInt(routerEl.getElementsByTagName("maxRoutes").item(0).getTextContent().trim()));
                    r.setMaxMBCapacity(Integer.parseInt(routerEl.getElementsByTagName("maxMBCapacity").item(0).getTextContent().trim()));
                }
                cm.router = r;
                }catch(Exception e){}
                
                //basic properties
                try{
                cm.supvisstrat = ConcreticisedMethod.SupervisingStrategy
                        .valueOf(ue.getElementsByTagName("supvisstrat").item(0).getTextContent().trim());
                }catch(Exception e){}
                try{
                cm.withTimeRange = ue.getElementsByTagName("withTimeRange").item(0).getTextContent().trim();
                }catch(Exception e){}
                try{
                cm.maxnumofretr = Integer.parseInt(ue.getElementsByTagName("maxnumofretr").item(0).getTextContent().trim());
                }catch(Exception e){}
                cm.setMethodName(ue.getElementsByTagName("methodName").item(0).getTextContent().trim());
                cm.setModuleID(ue.getElementsByTagName("moduleID").item(0).getTextContent().trim());
                
                //advanced properties
                try{
                Element propEl = (Element) ue.getElementsByTagName("properties").item(0);
                NodeList props = propEl.getElementsByTagName("entry");
                for(int k =0; k< props.getLength(); k++){
                    Element p = (Element) props.item(k);
                    cm.properties.put(p.getElementsByTagName("key").item(0).getTextContent().trim(),
                            p.getElementsByTagName("value").item(0).getTextContent().trim().trim());
                }}catch(Exception e){}
                
                vl.units.add(cm);
            }}catch(Exception e){}
            try{
            NodeList entries = le.getElementsByTagName("entries");
            for (int j = 0; j < entries.getLength(); j++) {
                Element ee = (Element) entries.item(j);
                Entry e = new Entry();
                //cmid
                e.setCmID(ee.getElementsByTagName("cmID").item(0).getTextContent().trim());
                //location
                Element pointEl = (Element) ee.getElementsByTagName("loc").item(0);
                e.setLoc(new Point(Integer.parseInt(pointEl.getElementsByTagName("x").item(0).getTextContent().trim()),
                        Integer.parseInt(pointEl.getElementsByTagName("y").item(0).getTextContent().trim())));
                //refarg
                Element argEl = (Element) ee.getElementsByTagName("refArg").item(0);
                Argument arg = new Argument();
                arg.setName(argEl.getElementsByTagName("name").item(0).getTextContent().trim());
                arg.setType(argEl.getElementsByTagName("type").item(0).getTextContent().trim());
                arg.setIsMainArg(Boolean.parseBoolean(argEl.getElementsByTagName("isMainArg").item(0).getTextContent().trim()));
                if (Boolean.parseBoolean(argEl.getElementsByTagName("fixed").item(0).getTextContent().trim())) {
                    arg.setFixed(true);
                    arg.setDefaultValue(argEl.getElementsByTagName("defaultValue").item(0).getTextContent().trim());
                } else {
                    arg.setFixed(false);
                }
                e.setRefArg(arg);
                
                vl.entries.add(e);
            }}catch(Exception e){}
            try{
            NodeList exits = le.getElementsByTagName("exits");
            for (int j = 0; j < exits.getLength(); j++) {
                Element ee = (Element) exits.item(j);
                Exit e = new Exit();
                //cmid
                e.setCmID(ee.getElementsByTagName("cmID").item(0).getTextContent().trim());
                //location
                Element pointEl = (Element) ee.getElementsByTagName("loc").item(0);
                e.setLoc(new Point(Integer.parseInt(pointEl.getElementsByTagName("x").item(0).getTextContent().trim()),
                        Integer.parseInt(pointEl.getElementsByTagName("y").item(0).getTextContent().trim())));
                //refres
                Element argEl = (Element) ee.getElementsByTagName("refRes").item(0);
                Result arg = new Result();
                arg.setName(argEl.getElementsByTagName("name").item(0).getTextContent().trim());
                arg.setType(argEl.getElementsByTagName("type").item(0).getTextContent().trim());                
                e.setRefRes(arg);
                
                vl.exits.add(e);
            }}catch(Exception e){}
            try{
            NodeList stSetters = le.getElementsByTagName("stSetters");
            for (int j = 0; j < stSetters.getLength(); j++) {
                Element sse = (Element) stSetters.item(j);
                StateSetter setter = new StateSetter();
                //cmid
                setter.setCmID(sse.getElementsByTagName("cmID").item(0).getTextContent().trim());
                //location
                Element pointEl = (Element) sse.getElementsByTagName("loc").item(0);
                setter.setLoc(new Point(Integer.parseInt(pointEl.getElementsByTagName("x").item(0).getTextContent().trim()),
                        Integer.parseInt(pointEl.getElementsByTagName("y").item(0).getTextContent().trim())));
                //refstate
                Element argEl = (Element) sse.getElementsByTagName("refState").item(0);
                State s = new State();
                s.setName(argEl.getElementsByTagName("name").item(0).getTextContent().trim());
                setter.setRefState(s);
                
                vl.stSetters.add(setter);
            }}catch(Exception e){}
            try{
            NodeList connections = ((Element) le.getElementsByTagName("connections").item(0))
                    .getElementsByTagName("entry");
            for (int j = 0; j < connections.getLength(); j++) {
                Element ce = (Element) connections.item(j);
                Element cev = (Element) ce.getElementsByTagName("value").item(0);
                Connection c = new Connection();
                c.sourceCMID = cev.getElementsByTagName("sourceCMID").item(0).getTextContent().trim();
                c.sourcePinName = cev.getElementsByTagName("sourcePinName").item(0).getTextContent().trim();
                c.targetCMID = cev.getElementsByTagName("targetCMID").item(0).getTextContent().trim();
                c.targetPinName = cev.getElementsByTagName("targetPinName").item(0).getTextContent().trim();
                try{
                c.extractedFieldName = cev.getElementsByTagName("extractedFieldName").item(0).getTextContent().trim();
                }catch(Exception e){}
                try{
                Element argEl = (Element) cev.getElementsByTagName("relatedState").item(0);
                State s = new State();
                s.setName(argEl.getElementsByTagName("name").item(0).getTextContent().trim());
                c.relatedState = s;
                }catch(Exception e){}
                
                vl.connections.put(ce.getElementsByTagName("key").item(0).getTextContent().trim(), c);
                
            }}catch(Exception e){}
            ws.getLayers().add(vl);
        }
        
        NodeList states = doc.getElementsByTagName("states");
        for (int i = 0; i < states.getLength(); i++) {
            Element el = (Element)layers.item(i);
            try{
                State s = new State(el.getElementsByTagName("name").item(0).getTextContent().trim());
                boolean notContains = true;
                for(State st: ws.getStates()){
                    if(st.equals(s)){
                        notContains = false;
                    }
                }
                if(notContains){
                    ws.addState(s);
                }          
            }catch(Exception e){}
        }
        
        return ws;
    }
    
}
