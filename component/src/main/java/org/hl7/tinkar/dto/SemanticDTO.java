package org.hl7.tinkar.dto;

import org.eclipse.collections.api.list.ImmutableList;
import org.hl7.tinkar.component.DefinitionForSemantic;
import org.hl7.tinkar.component.IdentifiedThing;
import org.hl7.tinkar.component.Semantic;

import java.util.UUID;

public record SemanticDTO(ImmutableList<UUID> componentUuids, ImmutableList<UUID> definitionForSemanticUuids,
                          ImmutableList<UUID> referencedComponentUuids) implements Semantic {

    public SemanticDTO(ImmutableList<UUID> componentUuids, DefinitionForSemantic definitionForSemantic, IdentifiedThing referencedComponent) {
        this(componentUuids, definitionForSemantic.componentUuids(), referencedComponent.componentUuids());
    }

    @Override
    public IdentifiedThing referencedComponent() {
        return new IdentifiedThingDTO(referencedComponentUuids);
    }

    @Override
    public DefinitionForSemantic definitionForSemantic() {
        return new DefinitionForSemanticDTO(definitionForSemanticUuids);
    }

}