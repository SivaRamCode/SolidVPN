package com.siva.vpn.core;

public interface OpenVPNManagement {
    enum pauseReason {
        noNetwork,
        userPause,
        screenOff
    }

	int mBytecountInterval =2;

	void reconnect();

	void pause();

	void resume();

	boolean stopVPN();

	void prefChanged();

}
