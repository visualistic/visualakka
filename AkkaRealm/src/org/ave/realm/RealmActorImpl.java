package org.ave.realm;

import org.ave.core.Flow;
import org.ave.realm.model.Citizen;
import org.ave.realm.model.CreateRealm;

/**
 * User code logic implementation
 *
 */
public class RealmActorImpl
        extends org.ave.realm.RealmActor.UserLogic {

    /**
     * User code block implementation for - GenerateCitizens.
     *
     * @param Output Block results flow
     * @param GenerateCitizens Unboxed message data
     */
    @Override
    public void GenerateCitizens(CreateRealm GenerateCitizens, Flow<Citizen> Output) {
        System.out.println("Generating citizens: started");
        for (int i = 0; i < GenerateCitizens.getCitizensCount(); i++) {
            Output.send(new Citizen(i, i % 2 == 0, i, i));
        }
        System.out.println("Generating citizens: finished");
    }

}
