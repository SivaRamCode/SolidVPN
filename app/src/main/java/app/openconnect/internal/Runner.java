package app.openconnect.internal;

import java.io.IOException;

import app.openconnect.RootTools;
import app.openconnect.execution.Command;
import app.openconnect.execution.CommandCapture;
import app.openconnect.execution.Shell;

import android.content.Context;
import android.util.Log;

public class Runner extends Thread {

    private static final String LOG_TAG = "RootTools::Runner";

    Context context;
    String binaryName;
    String parameter;

    public Runner(Context context, String binaryName, String parameter) {
        this.context = context;
        this.binaryName = binaryName;
        this.parameter = parameter;
    }

    public void run() {
        String privateFilesPath = null;
        try {
            privateFilesPath = context.getFilesDir().getCanonicalPath();
        } catch (IOException e) {
            if (RootTools.debugMode) {
                Log.e(LOG_TAG, "Problem occured while trying to locate private files directory!");
            }
            e.printStackTrace();
        }
        if (privateFilesPath != null) {
            try {
                CommandCapture command = new CommandCapture(0, false, privateFilesPath + "/" + binaryName + " " + parameter);
                Shell.startRootShell().add(command);
                commandWait(command);

            } catch (Exception e) {}
        }
    }

    private void commandWait(Command cmd) {
        synchronized (cmd) {
            try {
                if (!cmd.isFinished()) {
                    cmd.wait(2000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
