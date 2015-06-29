/*
 */

package org.vap.workspace.palettesupport;

import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Oleg Bantysh
 */
public class SelectorsTabNodesFactory extends ChildFactory<Integer>{
        /**
     *
     * @param list
     * @return
     */
    @Override
    protected boolean createKeys(List<Integer> list) {
        list.add(0);
        //list.add(1);
        return true;
    }

    /**
     *
     * @param key
     * @return
     */
    @Override
    protected Node createNodeForKey(Integer key) {
        switch(key){
            case 0: return new SelectorSelfNode();
            case 1: return new SelectorAllNode();
            default: return null;
        }
    }

}
