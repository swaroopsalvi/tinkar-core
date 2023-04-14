package dev.ikm.tinkar.coordinate.logic.calculator;

import dev.ikm.tinkar.coordinate.logic.LogicCoordinateRecord;
import dev.ikm.tinkar.coordinate.logic.PremiseType;
import dev.ikm.tinkar.coordinate.stamp.calculator.Latest;
import dev.ikm.tinkar.coordinate.stamp.calculator.StampCalculator;
import dev.ikm.tinkar.entity.SemanticEntityVersion;
import dev.ikm.tinkar.entity.graph.DiTreeEntity;
import dev.ikm.tinkar.entity.graph.EntityVertex;
import dev.ikm.tinkar.terms.EntityFacade;

public interface LogicCalculator {

    LogicCoordinateRecord logicCoordinateRecord();

    default boolean hasSufficientSet(EntityFacade entityFacade) {
        return hasSufficientSet(entityFacade.nid());
    }

    boolean hasSufficientSet(int nid);

    default Latest<DiTreeEntity> getStatedLogicalExpressionForEntity(EntityFacade entity, StampCalculator stampCalculator) {
        return getAxiomTreeForEntity(entity.nid(), stampCalculator, PremiseType.STATED);
    }

    Latest<DiTreeEntity> getAxiomTreeForEntity(int entityNid, StampCalculator stampCalculator, PremiseType premiseType);

    Latest<SemanticEntityVersion> getAxiomSemanticForEntity(int entityNid, StampCalculator stampCalculator, PremiseType premiseType);

    default Latest<DiTreeEntity> getAxiomTreeForEntity(EntityFacade entity, StampCalculator stampCalculator, PremiseType premiseType) {
        return getAxiomTreeForEntity(entity.nid(), stampCalculator, premiseType);
    }

    default Latest<DiTreeEntity> getStatedLogicalExpressionForEntity(int entityNid, StampCalculator stampCalculator) {
        return getAxiomTreeForEntity(entityNid, stampCalculator, PremiseType.STATED);
    }

    default Latest<DiTreeEntity> getInferredLogicalExpressionForEntity(EntityFacade entity, StampCalculator stampCalculator) {
        return getAxiomTreeForEntity(entity.nid(), stampCalculator, PremiseType.INFERRED);
    }

    default Latest<DiTreeEntity> getInferredLogicalExpressionForEntity(int entityNid, StampCalculator stampCalculator) {
        return getAxiomTreeForEntity(entityNid, stampCalculator, PremiseType.INFERRED);
    }

}
