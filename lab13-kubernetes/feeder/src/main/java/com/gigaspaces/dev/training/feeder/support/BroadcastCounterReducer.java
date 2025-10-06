package com.gigaspaces.dev.training.feeder.support;

import org.openspaces.remoting.RemoteResultReducer;
import org.openspaces.remoting.SpaceRemotingInvocation;
import org.openspaces.remoting.SpaceRemotingResult;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A sync remoting reducer (when used in broadcast mode) iterating through the results of {@link
 * com.gigaspaces.dev.training.common.IDataProcessor#countDataProcessed()} performed on all the
 * cluster nodes and reducing the result by aggregating it.
 *
 * @author kimchy
 */
public class BroadcastCounterReducer implements RemoteResultReducer<Long, Long> {

    private static final Logger logger = Logger.getLogger(BroadcastCounterReducer.class.getName());

    public Long reduce(SpaceRemotingResult<Long>[] results, SpaceRemotingInvocation remotingInvocation) throws Exception {
        if (!remotingInvocation.getMethodName().equals("countDataProcessed")) {
            return results[0].getResult();
        }
        long totalCount = 0;
        for (SpaceRemotingResult<Long> result : results) {
            if (result.getException() != null) {
                // just log the fact that there was an exception
                logger.log(Level.INFO, "REMOTING COUNT EXCEPTION " + result.getException().getMessage());
                continue;
            }
            totalCount += result.getResult();
        }
        return totalCount;
    }
}
