
package org.ave.realm;

import org.ave.core.Flow;


/**
 * User code logic implementation
 * 
 */
public class WorldActorImpl
    extends org.ave.realm.WorldActor.UserLogic
{


    /**
     * User code block implementation for - GenerateWorldEvent.
     * 
     * @param GenerateWorldEvent
     *     Unboxed message data
     * @param IsEventGenerated
     *     Block results flow
     */
    @Override
    public void GenerateWorldEvent(Boolean GenerateWorldEvent, Flow<Boolean> IsEventGenerated) {
        System.out.println("World Event: " + GenerateWorldEvent);
        IsEventGenerated.send(!GenerateWorldEvent);
    }

}
