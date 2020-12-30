package app.openconnect.execution;

import app.openconnect.RootTools;

public class CommandCapture extends Command {
    private StringBuilder sb = new StringBuilder();

    public CommandCapture(int id, String... command) {
        super(id, command);
    }

    public CommandCapture(int id, boolean handlerEnabled, String... command) {
        super(id, handlerEnabled, command);
    }

    public CommandCapture(int id, int timeout, String... command) {
        super(id, timeout, command);
    }


    @Override
    public void commandOutput(int id, String line) {
        sb.append(line).append('\n');
        RootTools.log("Command", "ID: " + id + ", " + line);
    }

    @Override
    public void commandTerminated(int id, String reason) {
        //pass
    }

    @Override
    public void commandCompleted(int id, int exitcode) {
        //pass
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}