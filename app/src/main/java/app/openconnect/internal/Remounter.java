package app.openconnect.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import app.openconnect.Constants;
import app.openconnect.RootTools;
import app.openconnect.containers.Mount;
import app.openconnect.execution.Command;
import app.openconnect.execution.CommandCapture;
import app.openconnect.execution.Shell;

public class Remounter {

 public boolean remount(String file, String mountType) {

        if (file.endsWith("/") && !file.equals("/")) {
            file = file.substring(0, file.lastIndexOf("/"));
        }
         boolean foundMount = false;
        while (!foundMount) {
            try {
                for (Mount mount : RootTools.getMounts()) {
                    RootTools.log(mount.getMountPoint().toString());

                    if (file.equals(mount.getMountPoint().toString())) {
                        foundMount = true;
                        break;
                    }
                }
            } catch (Exception e) {
                if (RootTools.debugMode) {
                    e.printStackTrace();
                }
                return false;
            }
            if (!foundMount) {
                try {
                    file = (new File(file).getParent());
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        Mount mountPoint = findMountPointRecursive(file);

        if (mountPoint != null) {
            RootTools.log(Constants.TAG, "Remounting " + mountPoint.getMountPoint().getAbsolutePath() + " as " + mountType.toLowerCase());
            final boolean isMountMode = mountPoint.getFlags().contains(mountType.toLowerCase());

            if (!isMountMode) {
                //grab an instance of the internal class
                try {
                    CommandCapture command = new CommandCapture(0,
                            true,
                            "busybox mount -o remount," + mountType.toLowerCase() + " " + mountPoint.getDevice().getAbsolutePath() + " " + mountPoint.getMountPoint().getAbsolutePath(),
                            "toolbox mount -o remount," + mountType.toLowerCase() + " " + mountPoint.getDevice().getAbsolutePath() + " " + mountPoint.getMountPoint().getAbsolutePath(),
                            "mount -o remount," + mountType.toLowerCase() + " " + mountPoint.getDevice().getAbsolutePath() + " " + mountPoint.getMountPoint().getAbsolutePath(),
                            "/system/bin/toolbox mount -o remount," + mountType.toLowerCase() + " " + mountPoint.getDevice().getAbsolutePath() + " " + mountPoint.getMountPoint().getAbsolutePath()
                    );
                    Shell.startRootShell().add(command);
                    commandWait(command);

                } catch (Exception e) {}

                mountPoint = findMountPointRecursive(file);
            }

            RootTools.log(Constants.TAG, mountPoint.getFlags() + " AND " + mountType.toLowerCase());
            if (mountPoint.getFlags().contains(mountType.toLowerCase())) {
                RootTools.log(mountPoint.getFlags().toString());
                return true;
            } else {
                RootTools.log(mountPoint.getFlags().toString());
                return false;
            }
        }
        else {
            RootTools.log("mount is null, file was: " + file + " mountType was: " + mountType);
        }

        return false;
    }

    private Mount findMountPointRecursive(String file) {
        try {
            ArrayList<Mount> mounts = RootTools.getMounts();
            for (File path = new File(file); path != null; ) {
                for (Mount mount : mounts) {
                    if (mount.getMountPoint().equals(path)) {
                        return mount;
                    }
                }
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            if (RootTools.debugMode) {
                e.printStackTrace();
            }
        }
        return null;
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
