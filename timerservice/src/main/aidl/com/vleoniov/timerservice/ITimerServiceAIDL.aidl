// ITimerServiceAIDL.aidl
package com.vleoniov.timerservice;
import com.vleoniov.timerservice.ITimerServiceResult;

interface ITimerServiceAIDL {

    void startTimer(long time, long period, ITimerServiceResult result);
}