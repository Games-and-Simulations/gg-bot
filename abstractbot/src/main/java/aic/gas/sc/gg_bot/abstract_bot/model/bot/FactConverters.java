package aic.gas.sc.gg_bot.abstract_bot.model.bot;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_STATIC_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper.MAX_DISTANCE;

import aic.gas.sc.gg_bot.abstract_bot.model.UnitTypeStatus;
import aic.gas.sc.gg_bot.abstract_bot.model.game.util.Utils;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitOfPlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitTypeWrapper;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnitWithCommands;
import aic.gas.sc.gg_bot.mas.model.metadata.FactConverterID;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactWithOptionalValue;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactWithOptionalValueSet;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactWithOptionalValueSetsForAgentType;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactWithSetOfOptionalValues;
import aic.gas.sc.gg_bot.mas.model.metadata.containers.FactWithSetOfOptionalValuesForAgentType;
import bwapi.Order;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * Enumeration of all IDs for facts' types as static classes
 */
@Slf4j
public class FactConverters {

  public static final FactWithSetOfOptionalValuesForAgentType<APlayer> GAME_PHASE = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(0, FactKeys.IS_PLAYER),
      optionalStream -> (double) optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .mapToDouble(APlayer::getFrameCount)
          .map(frameCount -> {
            // assuming 20 FPS
            // intervals at 5, 10, 15, 20 mins
            if (frameCount < 6000) {
              return 0;
            } else if (frameCount < 12000) {
              return 1;
            } else if (frameCount < 18000) {
              return 2;
            } else if (frameCount < 24000) {
              return 3;
            } else {
              return 4;
            }
          })
          .findFirst()
          .orElse(0.0), AgentTypes.PLAYER);

  //converters for base
  public static final FactWithSetOfOptionalValuesForAgentType<Double> AVERAGE_COUNT_OF_WORKERS_PER_BASE = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(2, FactKeys.AVERAGE_COUNT_OF_WORKERS_PER_BASE), optionalStream ->
      optionalStream.filter(Optional::isPresent)
          .mapToDouble(Optional::get)
          .sum(), AgentTypes.PLAYER);
  public static final FactWithSetOfOptionalValuesForAgentType<Double> AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(3, FactKeys.AVERAGE_COUNT_OF_WORKERS_MINING_GAS_PER_BASE),
      optionalStream -> optionalStream.filter(Optional::isPresent)
          .mapToDouble(Optional::get)
          .sum(), AgentTypes.PLAYER);
  public static final FactWithOptionalValueSetsForAgentType<AUnitOfPlayer> COUNT_OF_EXTRACTORS = new FactWithOptionalValueSetsForAgentType<>(
      new FactConverterID<>(5, FactKeys.HAS_EXTRACTOR), AgentTypes.BASE_LOCATION,
      optionalStream -> (double) optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .mapToLong(Stream::count)
          .sum()
  );
  public static final FactWithOptionalValueSet<AUnit> COUNT_OF_MINERALS_ON_BASE = new FactWithOptionalValueSet<>(
      new FactConverterID<>(6, FactKeys.MINERAL),
      aUnitStream -> aUnitStream.map(aUnitStream1 -> (double) aUnitStream1.count()).orElse(0.0));
  public static final FactWithOptionalValueSet<AUnitOfPlayer> COUNT_OF_EXTRACTORS_ON_BASE = new FactWithOptionalValueSet<>(
      new FactConverterID<>(7, FactKeys.HAS_EXTRACTOR),
      aUnitStream -> aUnitStream.map(aUnitStream1 -> (double) aUnitStream1
          .filter(aUnitOfPlayer -> !aUnitOfPlayer.isMorphing()
              && !aUnitOfPlayer.isBeingConstructed())
          .count()).orElse(0.0));
  public static final FactWithSetOfOptionalValuesForAgentType<Integer> COUNT_OF_BASES_WITHOUT_EXTRACTORS = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(8, FactKeys.COUNT_OF_BASES_WITHOUT_EXTRACTORS),
      optionalStream -> Math.min(optionalStream.filter(Optional::isPresent)
          .mapToInt(Optional::get)
          .sum(), 3), AgentTypes.PLAYER);

  //converters for player's - aggregated data
  public static final FactWithSetOfOptionalValuesForAgentType<Boolean> COUNT_OF_BASES = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(31, FactKeys.IS_OUR_BASE), optionalStream -> (double) optionalStream
      .filter(Optional::isPresent)
      .filter(Optional::get)
      .count(), AgentTypes.BASE_LOCATION);
  public static final FactWithSetOfOptionalValuesForAgentType<Boolean> HAS_AT_LEAST_TWO_BASES = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(32, FactKeys.IS_OUR_BASE), optionalStream -> optionalStream
      .filter(Optional::isPresent)
      .filter(Optional::get)
      .count() >= 2 ? 1.0 : 0.0, AgentTypes.BASE_LOCATION);
  public static final FactWithSetOfOptionalValuesForAgentType<Boolean> COUNT_OF_ENEMY_BASES = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(33, FactKeys.IS_ENEMY_BASE), optionalStream -> (double) optionalStream
      .filter(Optional::isPresent)
      .filter(Optional::get)
      .count(), AgentTypes.BASE_LOCATION);
  public static final FactWithOptionalValueSetsForAgentType<UnitTypeStatus> SUM_OF_ENEMY_AIR_DMG = new FactWithOptionalValueSetsForAgentType<>(
      new FactConverterID<>(34, ENEMY_AIR_FORCE_STATUS), AgentTypes.PLAYER,
      optionalStream -> optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .mapToDouble(value -> value
              .mapToDouble(
                  v -> v.getCount() * v.getUnitTypeWrapper().getAirWeapon()
                      .getDamagePerSecondNormalized())
              .sum())
          .sum());
  public static final FactWithOptionalValueSetsForAgentType<UnitTypeStatus> SUM_OF_ENEMY_STATIC_AIR_DMG = new FactWithOptionalValueSetsForAgentType<>(
      new FactConverterID<>(35, FactKeys.ENEMY_STATIC_AIR_FORCE_STATUS), AgentTypes.PLAYER,
      optionalStream -> optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .mapToDouble(value -> value
              .mapToDouble(
                  v -> v.getCount() * v.getUnitTypeWrapper().getAirWeapon()
                      .getDamagePerSecondNormalized())
              .sum())
          .sum());
  public static final FactWithOptionalValueSetsForAgentType<UnitTypeStatus> CAN_ENEMY_PRODUCE_MILITARY_UNITS = new FactWithOptionalValueSetsForAgentType<>(
      new FactConverterID<>(36, FactKeys.ENEMY_BUILDING_STATUS), AgentTypes.PLAYER,
      optionalStream -> {
        boolean canProduceMilitaryUnits = optionalStream
            .filter(Optional::isPresent)
            .map(Optional::get)
            .flatMap(unitTypeStatusStream -> unitTypeStatusStream)
            .map(UnitTypeStatus::getUnitTypeWrapper)
            .anyMatch(AUnitTypeWrapper::isEnablesMilitaryUnits);
        return canProduceMilitaryUnits ? 1.0 : 0.0;
      }
  );
  public static final FactWithSetOfOptionalValuesForAgentType<Double> CURRENT_POPULATION = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(38, FactKeys.POPULATION),
      optionalStream -> optionalStream.filter(Optional::isPresent)
          .mapToDouble(Optional::get)
          .sum(), AgentTypes.PLAYER);
  public static final FactWithSetOfOptionalValuesForAgentType<Double> MAX_POPULATION = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(39, FactKeys.POPULATION_LIMIT),
      optionalStream -> optionalStream.filter(Optional::isPresent)
          .mapToDouble(Optional::get)
          .sum(), AgentTypes.PLAYER);
  public static final FactWithSetOfOptionalValuesForAgentType<Double> COUNT_OF_MINERALS = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(40, FactKeys.AVAILABLE_MINERALS),
      optionalStream -> (double) optionalStream.filter(Optional::isPresent)
          .mapToDouble(Optional::get)
          .sum(), AgentTypes.PLAYER);
  public static final FactWithSetOfOptionalValuesForAgentType<Double> FREE_SUPPLY = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(41, FactKeys.FREE_SUPPLY),
      optionalStream -> {
        double count = optionalStream.filter(Optional::isPresent)
            .mapToDouble(Optional::get)
            .sum();
        return count > 7 ? 7 : count;
      }, AgentTypes.PLAYER);
  public static final FactWithSetOfOptionalValuesForAgentType<Double> FORCE_SUPPLY_RATIO = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(42, FactKeys.FORCE_SUPPLY_RATIO),
      optionalStream -> {
        double sum = optionalStream.filter(Optional::isPresent)
            .mapToDouble(Optional::get)
            .sum();
        //cap to interval 0.5 - 2.0
        return Math.max(Math.min(sum, 2.0), 0.5);
      }, AgentTypes.PLAYER);
  public static final FactWithSetOfOptionalValuesForAgentType<Double> DIFFERENCE_IN_BASES = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(43, FactKeys.DIFFERENCE_IN_BASES),
      optionalStream -> {
        double sum = optionalStream.filter(Optional::isPresent)
            .mapToDouble(Optional::get)
            .sum();
        //cap to interval 0.5 - 2.0
        return Math.max(Math.min(sum, 3.0), -3.0);
      }, AgentTypes.PLAYER);
  public static final FactWithOptionalValueSetsForAgentType<UnitTypeStatus> ENEMY_RANGED_VS_MELEE_DAMAGE = new FactWithOptionalValueSetsForAgentType<>(
      new FactConverterID<>(44, ENEMY_GROUND_FORCE_STATUS), AgentTypes.PLAYER,
      optionalStream -> {
        double sum = optionalStream.filter(Optional::isPresent)
            .mapToDouble(Utils::computeRangedVsMeleeDamageRatio)
            .sum();
        //cap to interval 0.5 - 2.0
        return Math.max(Math.min(sum, 2.0), 0.5);
      }
  );
  public static final FactWithOptionalValueSetsForAgentType<UnitTypeStatus> OUR_RANGED_VS_MELEE_DAMAGE = new FactWithOptionalValueSetsForAgentType<>(
      new FactConverterID<>(45, FactKeys.OWN_GROUND_FORCE_STATUS), AgentTypes.PLAYER,
      optionalStream -> {
        double sum = optionalStream.filter(Optional::isPresent)
            .mapToDouble(Utils::computeRangedVsMeleeDamageRatio)
            .sum();
        //cap to interval 0.5 - 2.0
        return Math.max(Math.min(sum, 2.0), 0.5);
      }
  );
  public static final FactWithOptionalValueSetsForAgentType<UnitTypeStatus> SUM_OF_ENEMY_AIR_HP = new FactWithOptionalValueSetsForAgentType<>(
      new FactConverterID<>(46, ENEMY_AIR_FORCE_STATUS), AgentTypes.PLAYER,
      optionalStream -> optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .mapToDouble(value -> value
              .mapToDouble(v -> v.getCount() * (v.getUnitTypeWrapper().getMaxHitPoints() + v
                  .getUnitTypeWrapper().getMaxShields())).sum())
          .sum());
  public static final FactWithOptionalValueSetsForAgentType<UnitTypeStatus> HAS_AT_LEAST_10_ARMY_SUPPLY = new FactWithOptionalValueSetsForAgentType<>(
      new FactConverterID<>(47, FactKeys.OWN_FORCE_STATUS), AgentTypes.PLAYER,
      optionalStream -> {
        return optionalStream
            .filter(Optional::isPresent)
            .map(Optional::get)
            .mapToDouble(value -> value
                .mapToDouble(v -> v.getCount() * v.getUnitTypeWrapper().supplyRequired()).sum())
            .sum() >= 10 ? 1.0 : 0.0;
      });
  public static final FactWithOptionalValueSetsForAgentType<UnitTypeStatus> SUM_OF_ENEMY_STATIC_GROUND_DMG = new FactWithOptionalValueSetsForAgentType<>(
      new FactConverterID<>(48, ENEMY_STATIC_GROUND_FORCE_STATUS), AgentTypes.PLAYER,
      optionalStream -> (double) optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .mapToDouble(value -> value.mapToDouble(
              v -> v.getCount() * v.getUnitTypeWrapper().getGroundWeapon()
                  .getDamagePerSecondNormalized()).sum())
          .sum());
  public static final FactWithOptionalValueSetsForAgentType<UnitTypeStatus> SUM_OF_ENEMY_AIR_UNITS = new FactWithOptionalValueSetsForAgentType<>(
      new FactConverterID<>(49, ENEMY_AIR_FORCE_STATUS), AgentTypes.PLAYER,
      optionalStream -> (double) optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .mapToDouble(value -> value.mapToDouble(UnitTypeStatus::getCount).sum())
          .sum());
  public static final FactWithSetOfOptionalValuesForAgentType<Double> ENEMY_BASES_UNPROTECTED_AGAINST_AIR = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(50, FactKeys.DPS_OF_ANTI_AIR_UNITS_ON_ENEMY_BASE),
      optionalStream -> optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(aDouble -> aDouble < 60)
          .count(), AgentTypes.BASE_LOCATION);
  public static final FactWithSetOfOptionalValuesForAgentType<Double> ENEMY_BASES_UNPROTECTED_AGAINST_GROUND = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(51, FactKeys.DPS_OF_ANTI_GROUND_UNITS_ON_ENEMY_BASE),
      optionalStream -> optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(aDouble -> aDouble < 60)
          .count(), AgentTypes.BASE_LOCATION);
  public static final FactWithOptionalValueSetsForAgentType<UnitTypeStatus> SUM_OF_ENEMY_GROUND_DMG = new FactWithOptionalValueSetsForAgentType<>(
      new FactConverterID<>(52, ENEMY_GROUND_FORCE_STATUS), AgentTypes.PLAYER,
      optionalStream -> (double) optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .mapToDouble(value -> value.mapToDouble(v -> v.getCount()
              * v.getUnitTypeWrapper().getGroundWeapon().getDamagePerSecondNormalized()).sum())
          .sum());

  //base army stats
  public static final FactWithOptionalValue<Boolean> IS_BASE = new FactWithOptionalValue<>(
      new FactConverterID<>(323, FactKeys.IS_OUR_BASE),
      aBoolean -> aBoolean.orElse(false) ? 1.0 : 0.0);
  public static final FactWithOptionalValue<Boolean> IS_ENEMY_BASE = new FactWithOptionalValue<>(
      new FactConverterID<>(324, FactKeys.IS_ENEMY_BASE),
      aBoolean -> aBoolean.orElse(false) ? 1.0 : 0.0);
  public static final FactWithOptionalValue<Boolean> IS_START_LOCATION = new FactWithOptionalValue<>(
      new FactConverterID<>(327, FactKeys.IS_START_LOCATION),
      aBoolean -> aBoolean.orElse(false) ? 1.0 : 0.0);

  //defense
  public static final FactWithOptionalValueSet<AUnitOfPlayer> COUNT_OF_CREEP_COLONIES_AT_BASE = new FactWithOptionalValueSet<>(
      new FactConverterID<>(328, FactKeys.STATIC_DEFENSE),
      vStream -> (double) vStream.orElse(Stream.empty())
          .map(AUnit::getType)
          .filter(typeWrapper -> typeWrapper.equals(AUnitTypeWrapper.CREEP_COLONY_TYPE))
          .count());
  public static final FactWithOptionalValueSet<AUnitOfPlayer> COUNT_OF_SPORE_COLONIES_AT_BASE = new FactWithOptionalValueSet<>(
      new FactConverterID<>(329, FactKeys.STATIC_DEFENSE),
      vStream -> (double) vStream.orElse(Stream.empty())
          .map(AUnit::getType)
          .filter(typeWrapper -> typeWrapper.equals(AUnitTypeWrapper.SPORE_COLONY_TYPE))
          .count());
  public static final FactWithOptionalValueSet<AUnitOfPlayer> COUNT_OF_SUNKEN_COLONIES_AT_BASE = new FactWithOptionalValueSet<>(
      new FactConverterID<>(330, FactKeys.STATIC_DEFENSE),
      vStream -> (double) vStream.orElse(Stream.empty())
          .map(AUnit::getType)
          .filter(typeWrapper -> typeWrapper.equals(AUnitTypeWrapper.SUNKEN_COLONY_TYPE))
          .count());
  public static final FactWithOptionalValueSet<AUnitOfPlayer> BASE_IS_COMPLETED = new FactWithOptionalValueSet<>(
      new FactConverterID<>(334, FactKeys.HAS_BASE), vStream -> vStream.orElse(Stream.empty())
      .anyMatch(aUnitOfPlayer -> !aUnitOfPlayer.isBeingConstructed()
          && !aUnitOfPlayer.isMorphing()) ? 1.0 : 0.0);
  public static final FactWithOptionalValue<Double> DMG_AIR_CAN_INFLICT_TO_GROUND_VS_SUFFER = new FactWithOptionalValue<>(
      new FactConverterID<>(335, FactKeys.DAMAGE_AIR_CAN_INFLICT_TO_GROUND_VS_SUFFER),
      aDouble -> aDouble.orElse(0.0));
  public static final FactWithOptionalValue<Double> DMG_GROUND_CAN_INFLICT_TO_GROUND_VS_SUFFER = new FactWithOptionalValue<>(
      new FactConverterID<>(336, FactKeys.DAMAGE_GROUND_CAN_INFLICT_TO_GROUND_VS_SUFFER),
      aDouble -> aDouble.orElse(0.0));
  public static final FactWithOptionalValue<Double> DMG_AIR_CAN_INFLICT_TO_AIR_VS_SUFFER = new FactWithOptionalValue<>(
      new FactConverterID<>(337, FactKeys.DAMAGE_AIR_CAN_INFLICT_TO_AIR_VS_SUFFER),
      aDouble -> aDouble.orElse(0.0));
  public static final FactWithOptionalValue<Double> DMG_GROUND_CAN_INFLICT_TO_AIR_VS_SUFFER = new FactWithOptionalValue<>(
      new FactConverterID<>(338, FactKeys.DAMAGE_GROUND_CAN_INFLICT_TO_AIR_VS_SUFFER),
      aDouble -> aDouble.orElse(0.0));
  public static final FactWithOptionalValue<Double> RATIO_GLOBAL_AIR_VS_ANTI_AIR_ON_BASE = new FactWithOptionalValue<>(
      new FactConverterID<>(339, FactKeys.RATIO_GLOBAL_AIR_VS_ANTI_AIR_ON_BASE),
      aDouble -> aDouble.orElse(0.0));
  public static final FactWithOptionalValue<Double> AIR_DISTANCE_TO_OUR_CLOSEST_BASE = new FactWithOptionalValue<>(
      new FactConverterID<>(340, FactKeys.AIR_DISTANCE_TO_OUR_CLOSEST_BASE),
      aDouble -> aDouble.orElse(MAX_DISTANCE));
  public static final FactWithOptionalValue<Double> AIR_DISTANCE_TO_ENEMY_CLOSEST_BASE = new FactWithOptionalValue<>(
      new FactConverterID<>(341, FactKeys.AIR_DISTANCE_TO_ENEMY_CLOSEST_BASE),
      aDouble -> aDouble.orElse(MAX_DISTANCE));
  public static final FactWithOptionalValue<Double> GROUND_DISTANCE_TO_OUR_CLOSEST_BASE = new FactWithOptionalValue<>(
      new FactConverterID<>(342, FactKeys.GROUND_DISTANCE_TO_OUR_CLOSEST_BASE),
      aDouble -> aDouble.orElse(MAX_DISTANCE));
  public static final FactWithOptionalValue<Double> GROUND_DISTANCE_TO_ENEMY_CLOSEST_BASE = new FactWithOptionalValue<>(
      new FactConverterID<>(343, FactKeys.GROUND_DISTANCE_TO_ENEMY_CLOSEST_BASE),
      aDouble -> aDouble.orElse(MAX_DISTANCE));
  public static final FactWithOptionalValue<Double> RATIO_GLOBAL_GROUND_VS_ANTI_GROUND_ON_BASE = new FactWithOptionalValue<>(
      new FactConverterID<>(344, FactKeys.RATIO_GLOBAL_GROUND_VS_ANTI_GROUND_ON_BASE),
      aDouble -> aDouble.orElse(0.0));

  //building
  public static final FactWithSetOfOptionalValuesForAgentType<AUnitOfPlayer> COUNT_OF_POOLS = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(402, FactKeys.REPRESENTS_UNIT),
      optionalStream -> (double) optionalStream
          .filter(Optional::isPresent)
          .count(), AgentTypes.SPAWNING_POOL);
  public static final FactWithSetOfOptionalValuesForAgentType<AUnitOfPlayer> IS_POOL_BUILT = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(407, FactKeys.REPRESENTS_UNIT),
      optionalStream -> optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .count() > 0 ? 1.0 : 0.0, AgentTypes.SPAWNING_POOL);
  public static final FactWithSetOfOptionalValuesForAgentType<AUnitOfPlayer> COUNT_OF_LAIRS = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(410, FactKeys.REPRESENTS_UNIT),
      optionalStream -> (double) optionalStream.filter(Optional::isPresent)
          .count(), AgentTypes.LAIR);
  public static final FactWithSetOfOptionalValuesForAgentType<AUnitOfPlayer> COUNT_OF_SPIRES = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(412, FactKeys.REPRESENTS_UNIT),
      optionalStream -> (double) optionalStream.filter(Optional::isPresent)
          .count(), AgentTypes.SPIRE);
  public static final FactWithSetOfOptionalValuesForAgentType<AUnitOfPlayer> COUNT_OF_HYDRALISK_DENS = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(414, FactKeys.REPRESENTS_UNIT),
      optionalStream -> (double) optionalStream.filter(Optional::isPresent)
          .count(), AgentTypes.HYDRALISK_DEN);
  public static final FactWithSetOfOptionalValuesForAgentType<AUnitOfPlayer> COUNT_OF_EVOLUTION_CHAMBERS = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(416, FactKeys.REPRESENTS_UNIT),
      optionalStream -> (double) optionalStream.filter(Optional::isPresent)
          .count(), AgentTypes.EVOLUTION_CHAMBER);

  //"is unit morphing - command was issued"
  public static final FactWithOptionalValue<AUnitOfPlayer> IS_MORPHING = new FactWithOptionalValue<>(
      new FactConverterID<>(502, FactKeys.REPRESENTS_UNIT),
      aUnit -> aUnit.get().getOrder().isPresent() && Stream
          .of(Order.ZergBuildingMorph, Order.IncompleteBuilding, Order.ZergUnitMorph)
          .anyMatch(order -> order == aUnit.get().getOrder().get()) ? 1.0 : 0.0);

  //worker
  public static final FactWithOptionalValue<AUnit> IS_MINING_MINERAL = new FactWithOptionalValue<>(
      new FactConverterID<>(701, FactKeys.MINING_MINERAL), aUnit -> {
    if (aUnit.isPresent()) {
      return 1;
    }
    return 0;
  });
  public static final FactWithOptionalValue<AUnitWithCommands> IS_CARRYING_MINERAL = new FactWithOptionalValue<>(
      new FactConverterID<>(702, FactKeys.IS_UNIT), aUnit -> {
    if (aUnit.isPresent()) {
      if (aUnit.get().isCarryingMinerals()) {
        return 1;
      }
    }
    return 0;
  });
  public static final FactWithOptionalValue<AUnitWithCommands> IS_MINING_GAS = new FactWithOptionalValue<>(
      new FactConverterID<>(707, FactKeys.IS_UNIT),
      aUnit -> aUnit.get().isGatheringGas() ? 1.0 : 0.0
  );
  public static final FactWithOptionalValue<AUnitWithCommands> IS_CARRYING_GAS = new FactWithOptionalValue<>(
      new FactConverterID<>(708, FactKeys.IS_UNIT), aUnit -> aUnit.get().isCarryingGas() ? 1.0 : 0.0
  );
  public static final FactWithSetOfOptionalValuesForAgentType<AUnitOfPlayer> COUNT_OF_IDLE_DRONES = new FactWithSetOfOptionalValuesForAgentType<>(
      new FactConverterID<>(710, FactKeys.REPRESENTS_UNIT), optionalStream -> optionalStream
      .filter(Optional::isPresent)
      .map(Optional::get)
      .filter(AUnit::isIdle)
      .count(), AgentTypes.DRONE);

  //morphing to - count has cap 3
  public static final FactWithSetOfOptionalValues<AUnitTypeWrapper> COUNT_OF_INCOMPLETE_EXTRACTORS = new FactWithSetOfOptionalValues<>(
      new FactConverterID<>(801, FactKeys.IS_MORPHING_TO),
      optionalStream -> Math.min(optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(AUnitTypeWrapper::isGasBuilding)
          .count(), 3.0));
  public static final FactWithSetOfOptionalValues<AUnitTypeWrapper> COUNT_OF_INCOMPLETE_OVERLORDS = new FactWithSetOfOptionalValues<>(
      new FactConverterID<>(802, FactKeys.IS_MORPHING_TO),
      optionalStream -> Math.min(optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(unitTypeWrapper -> unitTypeWrapper.equals(AUnitTypeWrapper.OVERLORD_TYPE))
          .count(), 3.0));
  public static final FactWithSetOfOptionalValues<AUnitTypeWrapper> COUNT_OF_INCOMPLETE_DRONES = new FactWithSetOfOptionalValues<>(
      new FactConverterID<>(803, FactKeys.IS_MORPHING_TO),
      optionalStream -> Math.min(optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(AUnitTypeWrapper::isWorker)
          .count(), 3.0));
  public static final FactWithSetOfOptionalValues<AUnitTypeWrapper> COUNT_OF_INCOMPLETE_HATCHERIES = new FactWithSetOfOptionalValues<>(
      new FactConverterID<>(804, FactKeys.IS_MORPHING_TO),
      optionalStream -> Math.min(optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(unitTypeWrapper -> unitTypeWrapper.equals(AUnitTypeWrapper.HATCHERY_TYPE))
          .count(), 3.0));
  public static final FactWithSetOfOptionalValues<AUnitTypeWrapper> COUNT_OF_INCOMPLETE_AIRS = new FactWithSetOfOptionalValues<>(
      new FactConverterID<>(805, FactKeys.IS_MORPHING_TO),
      optionalStream -> Math.min(optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(
              unitTypeWrapper -> unitTypeWrapper.isFlyer() && !unitTypeWrapper.isNotActuallyUnit()
                  && !unitTypeWrapper.equals(AUnitTypeWrapper.OVERLORD_TYPE))
          .count(), 3.0));
  public static final FactWithSetOfOptionalValues<AUnitTypeWrapper> COUNT_OF_INCOMPLETE_RANGED = new FactWithSetOfOptionalValues<>(
      new FactConverterID<>(806, FactKeys.IS_MORPHING_TO),
      optionalStream -> Math.min(optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(
              unitTypeWrapper -> !unitTypeWrapper.isWorker() && !unitTypeWrapper.isFlyer()
                  && !unitTypeWrapper.isNotActuallyUnit()
                  && !unitTypeWrapper.equals(AUnitTypeWrapper.ZERGLING_TYPE))
          .count(), 3.0));
  public static final FactWithSetOfOptionalValues<AUnitTypeWrapper> COUNT_OF_INCOMPLETE_MELEE = new FactWithSetOfOptionalValues<>(
      new FactConverterID<>(807, FactKeys.IS_MORPHING_TO),
      optionalStream -> Math.min(optionalStream
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(
              unitTypeWrapper -> unitTypeWrapper.equals(AUnitTypeWrapper.ZERGLING_TYPE))
          .count(), 3.0));


}
