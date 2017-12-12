package aic.gas.sc.gg_bot.replay_parser.model.watcher;

import aic.gas.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Trajectory;
import aic.gas.sc.gg_bot.replay_parser.service.WatcherMediatorService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Template for AgentWatcher
 */
@Slf4j
public class AgentWatcher<T extends AgentWatcherType> {

  private static int idCounter = 0;
  @Getter
  protected final T agentWatcherType;
  @Getter
  protected final Beliefs beliefs;
  @Getter
  protected final List<PlanWatcher> plansToWatch;
  @Getter
  private final int ID;

  public AgentWatcher(T agentWatcherType) {
    this.agentWatcherType = agentWatcherType;
    beliefs = new Beliefs(agentWatcherType);
    this.ID = idCounter++;
    this.plansToWatch = agentWatcherType.getPlanWatchers().stream()
        .map(AgentWatcherType.PlanWatcherInitializationStrategy::returnPlanWatcher)
        .collect(Collectors.toList());
  }

  /**
   * Get stream of entries of list of trajectories for desire
   */
  public Stream<Map.Entry<DesireKeyID, List<Trajectory>>> getTrajectories() {
    return plansToWatch.stream()
        .collect(Collectors.groupingBy(PlanWatcher::getDesireKey,
            Collectors.mapping(PlanWatcher::getTrajectory, Collectors.toList())))
        .entrySet().stream();
  }

  /**
   * Notify plan that commitment has been changed
   */
  void commitmentByOtherAgentToDesireOfThisAgentHasBeenChanged(boolean status,
      DesireKeyID desireKeyID) {
    Optional<PlanWatcher> planWatcherToNotify = plansToWatch.stream()
        .filter(planWatcher -> planWatcher.getDesireKey().equals(desireKeyID))
        .findAny();
    if (planWatcherToNotify.isPresent()) {
      if (status) {
        planWatcherToNotify.get().addCommitment();
      } else {
        planWatcherToNotify.get().removeCommitment();
      }
    } else {
      log.error("Notifying " + agentWatcherType.getName()
          + " but this agent does not contain plan for desire " + desireKeyID.getName());
    }
  }

  /**
   * Do reasoning
   */
  public void reason(WatcherMediatorService mediatorService) {
    if (agentWatcherType.getReasoning().isPresent()) {
      agentWatcherType.getReasoning().get().updateBeliefs(beliefs, mediatorService);
    }
  }

  /**
   * Handle trajectories of plans
   */
  public void handleTrajectoriesOfPlans(WatcherMediatorService mediatorService) {
    Set<Integer> committedIDs = plansToWatch.stream()
        .filter(PlanWatcher::isCommitted)
        .map(planWatcher -> planWatcher.getDesireKey().getID())
        .collect(Collectors.toSet());
    //start execution of jobs
    plansToWatch.forEach(planWatcher -> planWatcher
        .addNewStateIfAgentHasTransitedToOne(beliefs, mediatorService, committedIDs));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AgentWatcher that = (AgentWatcher) o;

    return ID == that.ID && agentWatcherType.equals(that.agentWatcherType);
  }

  @Override
  public int hashCode() {
    int result = agentWatcherType.hashCode();
    result = 31 * result + ID;
    return result;
  }
}