package cz.jan.maly.service;

import bwta.BaseLocation;
import bwta.Region;
import cz.jan.maly.model.agent.AgentBaseLocation;
import cz.jan.maly.model.agent.AgentRegion;
import cz.jan.maly.service.implementation.BotFacade;

import java.util.Optional;

/**
 * Interface to be implemented by user to create agent representing location - base/region.
 * Created by Jan on 05-Apr-17.
 */
public interface LocationInitializer {

    /**
     * Method to create agent from base location
     *
     * @param baseLocation
     * @param botFacade
     * @return
     */
    Optional<AgentBaseLocation> createAgent(BaseLocation baseLocation, BotFacade botFacade);

    /**
     * Method to create agent from base location
     *
     * @param region
     * @param botFacade
     * @return
     */
    Optional<AgentRegion> createAgent(Region region, BotFacade botFacade);

}