
package org.ave.realm;

import org.ave.core.Flow;
import org.ave.realm.model.Citizen;
import org.ave.realm.model.CreateRealm;


/**
 * User code logic implementation
 * 
 */
public class RealmActorImpl
    extends org.ave.realm.RealmActor.UserLogic
{


    /**
     * User code block implementation for - GenerateCitizens.
     * 
     * @param Output
     *     Block results flow
     * @param GenerateCitizens
     *     Unboxed message data
     */
    @Override
    public void GenerateCitizens(CreateRealm GenerateCitizens, Flow<Citizen> Output) {
    }

}
