package cz.jan.maly.model.agents;

import cz.jan.maly.model.knowledge.Beliefs;
import cz.jan.maly.model.metadata.AgentTypeKey;
import cz.jan.maly.service.implementation.AgentsRegister;
import lombok.Getter;

/**
 * Created by Jan on 09-Feb-17.
 */
public abstract class Agent {

    @Getter
    private final int id;

    @Getter
    private Beliefs beliefs;

    @Getter
    private final AgentTypeKey agentType;

    public Agent(AgentsRegister agentsRegister, AgentTypeKey agentType) {
        this.id = agentsRegister.getFreeId();
        this.agentType = agentType;
    }

    //TODO initialize beliefs, when starting agent, check environment

    //TODO factory to make from committed desire intention

    //TODO factory for specification when to commit to desire / remove commitment to intention


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Agent)) return false;

        Agent agent = (Agent) o;

        return id == agent.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
