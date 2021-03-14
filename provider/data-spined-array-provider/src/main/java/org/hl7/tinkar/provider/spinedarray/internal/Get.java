package org.hl7.tinkar.provider.openhft.internal;

import org.eclipse.collections.api.list.ImmutableList;
import org.hl7.tinkar.component.Stamp;
import org.hl7.tinkar.entity.EntityFactory;
import org.hl7.tinkar.entity.ConceptEntity;
import org.hl7.tinkar.entity.StampEntity;

import java.util.UUID;

public class Get {
    public static OpenHFTProvider singleton;

    public static ConceptEntity concept(int nid) {
         return EntityFactory.make(singleton.getBytes(nid));
    }

    public static StampEntity stamp(int nid) {
        return EntityFactory.makeStamp(singleton.getBytes(nid));
    }

    public static int nidForUuids(ImmutableList<UUID> uuidList) {
        return singleton.nidForUuids(uuidList);
    }

    public static int stampNid(Stamp stamp) {
        throw new UnsupportedOperationException();
    }
}
