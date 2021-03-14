package org.hl7.tinkar.provider.ephemeral;

import org.hl7.tinkar.entity.util.EntityCounter;
import org.hl7.tinkar.entity.util.EntityProcessor;
import org.hl7.tinkar.entity.util.EntityRealizer;
import org.hl7.tinkar.entity.LoadEntitiesFromDTO;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.fail;

public class ProviderEphemeralTest {

    @Test
    public void loadChronologies() {
        File file = new File("/Users/kec/Solor/tinkar-export.zip");
        LoadEntitiesFromDTO loadTink = new LoadEntitiesFromDTO(file);
        try {
            int count = loadTink.call();
            System.out.println("Loaded. " + loadTink.report());
        } catch (Exception e) {
            fail(e);
        }
    }
    @Test
    public void count() {
        EntityProcessor processor = new EntityCounter();
        ProviderEphemeral.singleton.forEach(processor);
        System.out.println("Sequential count: \n" + processor.report() + "\n");
        processor = new EntityCounter();
        ProviderEphemeral.singleton.forEachParallel(processor);
        System.out.println("Parallel count: \n" + processor.report()+ "\n");
        processor = new EntityRealizer();
        ProviderEphemeral.singleton.forEach(processor);
        System.out.println("Sequential realization: \n" + processor.report() + "\n");
        processor = new EntityRealizer();
        ProviderEphemeral.singleton.forEachParallel(processor);
        System.out.println("Parallel realization: \n" + processor.report()+ "\n");
        processor = new EntityRealizer();
        ProviderEphemeral.singleton.forEach(processor);
        System.out.println("Sequential realization: \n" + processor.report() + "\n");
        processor = new EntityRealizer();
        ProviderEphemeral.singleton.forEachParallel(processor);
        System.out.println("Parallel realization: \n" + processor.report()+ "\n");
    }
}