package com.su.market.query.http;

/**
 * Created by su on 18-3-22.
 */

public interface ProgressListener {

    void onProgress(long bytes, long contentLength, ProgressResult.NetStatus status);
}
