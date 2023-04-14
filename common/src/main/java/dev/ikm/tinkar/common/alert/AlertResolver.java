package dev.ikm.tinkar.common.alert;

import java.util.concurrent.FutureTask;

/**
 * @author kec
 */

public interface AlertResolver {
    String getTitle();

    String getDescription();

    FutureTask<Void> resolve();

    ResolutionPersistence getPersistence();
}
