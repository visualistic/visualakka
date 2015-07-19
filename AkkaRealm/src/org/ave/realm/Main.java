/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ave.realm;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.ave.core.Message;
import org.ave.core.VisualActor;
import org.ave.core.VisualProps;
import org.ave.realm.model.CreateRealm;

/**
 *
 * @author Pocomaxa
 */
public class Main {
       static ActorSystem sys = ActorSystem.create("Server");
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        VisualProps props = new VisualProps("/user/", VisualActor.ESCALATE, Integer.MAX_VALUE, "-1");
        ActorRef realm = sys.actorOf(Props.create(RealmActor.class, new RealmActor.Creator(props, "Start", new String[] { })), "Realm");
        
        
        ActorRef sum = sys.actorOf(Props.create(Sum.class, new Sum.Creator(props, "A", new String[] { })), "Sum");
        Message a = new Message(new Float(5f), "Sum", "A", 0, false);
        Message b = new Message(new Float(7f), "Sum", "B", 0, false);
        
        sum.tell(a, ActorRef.noSender());
        sum.tell(b, ActorRef.noSender());
//        Message m1 = new Message(new CreateRealm(10), "RealmActor", "Start", 0, false);
//
//        realm.tell(m1, ActorRef.noSender());       
    }
}
