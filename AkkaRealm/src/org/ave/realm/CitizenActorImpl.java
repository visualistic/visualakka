package org.ave.realm;

import org.ave.core.Flow;
import org.ave.realm.model.Citizen;

/**
 * User code logic implementation Hello
 */
public class CitizenActorImpl
        extends org.ave.realm.CitizenActor.UserLogic {

    /**
     * User code block implementation for - Communicate.
     *
     * @param Output Block results flow
     * @param Communicate Unboxed message data
     */
    @Override
    public void Communicate(org.ave.realm.model.Citizen Communicate, Flow<org.ave.realm.model.Citizen> Output) {
    }

    /**
     * User code block implementation for - Wandering.
     *
     * @param Output Block results flow
     * @param Wandering Unboxed message data
     */
    @Override
    public void Wandering(org.ave.realm.model.Citizen Wandering, Flow<org.ave.realm.model.Citizen> Output) {
        System.out.println("Wandering: started for " + Wandering.getId());
        Output.send(Wandering.newState(Wandering.getHungriness() - 0.1f, Wandering.getHappiness() + 0.1f));
    }

    /**
     * User code block implementation for - MakeFight.
     *
     * @param MakeFight Unboxed message data
     * @param Output Block results flow
     */
    @Override
    public void MakeFight(org.ave.realm.model.Citizen MakeFight, Flow<org.ave.realm.model.Citizen> Output) {
    }

    /**
     * User code block implementation for - GoEat.
     *
     * @param Output Block results flow
     * @param GoEat Unboxed message data
     */
    @Override
    public void GoEat(org.ave.realm.model.Citizen GoEat, Flow<org.ave.realm.model.Citizen> Output) {
        System.out.println("GoEat: started for " + GoEat.getId());
        Output.send(GoEat.newState(GoEat.getHungriness() + 0.1f, GoEat.getHappiness()));
    }

    /**
     * User code block implementation for - GoSleep.
     *
     * @param GoSleep Unboxed message data
     * @param Output Block results flow
     */
    @Override
    public void GoSleep(org.ave.realm.model.Citizen GoSleep, Flow<org.ave.realm.model.Citizen> Output) {
    }

    /**
     * User code block implementation for - GoHunt.
     *
     * @param Output Block results flow
     * @param GoHunt Unboxed message data
     */
    @Override
    public void GoHunt(org.ave.realm.model.Citizen GoHunt, Flow<org.ave.realm.model.Citizen> Output) {
    }

    /**
     * User code block implementation for - SelectHighestPriority.
     *
     * @param Hunt Block results flow
     * @param Sleep Block results flow
     * @param Eat Block results flow
     * @param Communicate Block results flow
     * @param citizen Unboxed message data
     * @param Fight Block results flow
     * @param Wander Block results flow
     * @param Child Block results flow
     */
    @Override
    public void SelectHighestPriority(org.ave.realm.model.Citizen citizen,
            Flow<org.ave.realm.model.Citizen> Eat,
            Flow<org.ave.realm.model.Citizen> Sleep,
            Flow<org.ave.realm.model.Citizen> Hunt,
            Flow<org.ave.realm.model.Citizen> Wander,
            Flow<org.ave.realm.model.Citizen> Communicate,
            Flow<org.ave.realm.model.Citizen> Fight,
            Flow<org.ave.realm.model.Citizen> Child) {
        System.out.println("SelectHighestPriority: started for " + citizen.getId());

        if (citizen.getHungriness() < 0.5f) {
            Eat.send(citizen);
        } else if (citizen.getHappiness() > 0.5f && !citizen.isIsMale()) {
            Child.send(new Citizen(citizen.getId(), true, 0, 1, 0f, 0f));
            Wander.send(citizen.newState(0f, 0f));
        } else {
            Wander.send(citizen);
        }
    }

}
