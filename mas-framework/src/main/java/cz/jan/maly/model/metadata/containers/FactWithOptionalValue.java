package cz.jan.maly.model.metadata.containers;

import cz.jan.maly.model.FeatureRawValueObtainingStrategy;
import cz.jan.maly.model.metadata.FactKey;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * Container for fact type and raw value obtaining strategy
 * Created by Jan on 14-Apr-17.
 */
@Getter
@AllArgsConstructor
public class FactWithOptionalValue<V> {
    private final FactKey<V> factKey;
    private final FeatureRawValueObtainingStrategy<Optional<V>> strategyToObtainValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FactWithOptionalValue<?> that = (FactWithOptionalValue<?>) o;

        return factKey.equals(that.factKey);
    }

    @Override
    public int hashCode() {
        return factKey.hashCode();
    }
}