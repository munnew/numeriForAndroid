package com.serori.numeri.util.async;

/**
 */
public interface BackgroundRunnable<Param, Result> {
    Result run(Param params);
}
