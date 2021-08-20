// ITimerServiceAIDL.aidl
package com.vleoniov.timerservice;
import com.vleoniov.timerservice.ITimerServiceResult;
// Declare any non-default types here with import statements

interface ITimerServiceAIDL {
        void startTimer(long time, long period, ITimerServiceResult result);
}