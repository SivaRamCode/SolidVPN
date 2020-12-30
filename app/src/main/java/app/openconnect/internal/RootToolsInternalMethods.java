package app.openconnect.internal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import app.openconnect.Constants;
import app.openconnect.RootTools;
import app.openconnect.containers.Mount;
import app.openconnect.containers.Permissions;
import app.openconnect.containers.Symlink;
import app.openconnect.execution.Command;
import app.openconnect.execution.CommandCapture;
import app.openconnect.execution.Shell;

public final class RootToolsInternalMethods {

 protected RootToolsInternalMethods() {}

    public static void getInstance() {
        RootTools.setRim(new RootToolsInternalMethods());
    }

    public boolean returnPath() throws TimeoutException {

        CommandCapture command = null;
        LineNumberReader lnr = null;
        FileReader fr = null;

        try {
            if (!RootTools.exists("/data/local/tmp")) {

                command = new CommandCapture(0, false, "mkdir /data/local/tmp");
                Shell.startRootShell().add(command);
                commandWait(command);

            }

            InternalVariables.path = new HashSet<String>();

            String mountedas = RootTools.getMountedAs("/");
            RootTools.remount("/", "rw");

            command = new CommandCapture(0, false, "chmod 0777 /init.rc");
            Shell.startRootShell().add(command);

            command = new CommandCapture(0, false,
                    "dd if=/init.rc of=/data/local/tmp/init.rc");
            Shell.startRootShell().add(command);

            command = new CommandCapture(0, false,
                    "chmod 0777 /data/local/tmp/init.rc");
            Shell.startRootShell().add(command);
            commandWait(command);

            RootTools.remount("/", mountedas);

            fr = new FileReader("/data/local/tmp/init.rc");
            lnr = new LineNumberReader(fr);

            String line;
            while ((line = lnr.readLine()) != null) {
                RootTools.log(line);
                if (line.contains("export PATH")) {
                    int tmp = line.indexOf("/");
                    InternalVariables.path = new HashSet<String>(
                            Arrays.asList(line.substring(tmp).split(":")));
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            if (RootTools.debugMode) {
                RootTools.log("Error: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        } finally {
            try {
                fr.close();
            } catch (Exception e) {}

            try {
                lnr.close();
            } catch (Exception e) {}
        }
    }

    public ArrayList<Symlink> getSymLinks() throws IOException {

        LineNumberReader lnr = null;
        FileReader fr = null;

        try {

            fr = new FileReader("/data/local/symlinks.txt");
            lnr = new LineNumberReader(fr);

            String line;
            ArrayList<Symlink> symlink = new ArrayList<Symlink>();

            while ((line = lnr.readLine()) != null) {

                RootTools.log(line);

                String[] fields = line.split(" ");
                symlink.add(new Symlink(new File(fields[fields.length - 3]), // file
                        new File(fields[fields.length - 1]) // SymlinkPath
                ));
            }
            return symlink;
        } finally {
            try {
                fr.close();
            } catch (Exception e) {}

            try {
                lnr.close();
            } catch (Exception e) {}
        }
    }

    public Permissions getPermissions(String line) {

        String[] lineArray = line.split(" ");
        String rawPermissions = lineArray[0];

        if (rawPermissions.length() == 10
                && (rawPermissions.charAt(0) == '-'
                || rawPermissions.charAt(0) == 'd' || rawPermissions
                .charAt(0) == 'l')
                && (rawPermissions.charAt(1) == '-' || rawPermissions.charAt(1) == 'r')
                && (rawPermissions.charAt(2) == '-' || rawPermissions.charAt(2) == 'w')) {
            RootTools.log(rawPermissions);

            Permissions permissions = new Permissions();

            permissions.setType(rawPermissions.substring(0, 1));

            RootTools.log(permissions.getType());

            permissions.setUserPermissions(rawPermissions.substring(1, 4));

            RootTools.log(permissions.getUserPermissions());

            permissions.setGroupPermissions(rawPermissions.substring(4, 7));

            RootTools.log(permissions.getGroupPermissions());

            permissions.setOtherPermissions(rawPermissions.substring(7, 10));

            RootTools.log(permissions.getOtherPermissions());

            StringBuilder finalPermissions = new StringBuilder();
            finalPermissions.append(parseSpecialPermissions(rawPermissions));
            finalPermissions.append(parsePermissions(permissions.getUserPermissions()));
            finalPermissions.append(parsePermissions(permissions.getGroupPermissions()));
            finalPermissions.append(parsePermissions(permissions.getOtherPermissions()));

            permissions.setPermissions(Integer.parseInt(finalPermissions.toString()));

            return permissions;
        }

        return null;
    }

    public int parsePermissions(String permission) {
        int tmp;
        if (permission.charAt(0) == 'r')
            tmp = 4;
        else
            tmp = 0;

        RootTools.log("permission " + tmp);
        RootTools.log("character " + permission.charAt(0));

        if (permission.charAt(1) == 'w')
            tmp += 2;
        else
            tmp += 0;

        RootTools.log("permission " + tmp);
        RootTools.log("character " + permission.charAt(1));

        if (permission.charAt(2) == 'x')
            tmp += 1;
        else
            tmp += 0;

        RootTools.log("permission " + tmp);
        RootTools.log("character " + permission.charAt(2));

        return tmp;
    }

    public int parseSpecialPermissions(String permission) {
        int tmp = 0;
        if (permission.charAt(2) == 's')
            tmp += 4;

        if (permission.charAt(5) == 's')
            tmp += 2;

        if (permission.charAt(8) == 't')
            tmp += 1;

        RootTools.log("special permissions " + tmp);

        return tmp;
    }

       public boolean copyFile(String source, String destination, boolean remountAsRw,
                            boolean preserveFileAttributes) {

        CommandCapture command = null;
        boolean result = true;

        try {
            if (remountAsRw) {
                RootTools.remount(destination, "RW");
            }
            if (checkUtil("cp")) {
                RootTools.log("cp command is available!");

                if (preserveFileAttributes) {
                    command = new CommandCapture(0, false, "cp -fp " + source + " " + destination);
                    Shell.startRootShell().add(command);
                    commandWait(command);

                    //ensure that the file was copied, an exitcode of zero means success
                    result = command.getExitCode() == 0;

                } else {
                    command = new CommandCapture(0, false, "cp -f " + source + " " + destination);
                    Shell.startRootShell().add(command);
                    commandWait(command);

                    result = command.getExitCode() == 0;

                }
            } else {
                if (checkUtil("busybox") && hasUtil("cp", "busybox")) {
                    RootTools.log("busybox cp command is available!");

                    if (preserveFileAttributes) {
                        command = new CommandCapture(0, false, "busybox cp -fp " + source + " " + destination);
                        Shell.startRootShell().add(command);
                        commandWait(command);

                    } else {
                        command = new CommandCapture(0, false, "busybox cp -f " + source + " " + destination);
                        Shell.startRootShell().add(command);
                        commandWait(command);

                    }
                } else { // if cp is not available use cat
                    if (checkUtil("cat")) {
                        RootTools.log("cp is not available, use cat!");

                        int filePermission = -1;
                        if (preserveFileAttributes) {
                            // get permissions of source before overwriting
                            Permissions permissions = getFilePermissionsSymlinks(source);
                            filePermission = permissions.getPermissions();
                        }

                        // copy with cat
                        command = new CommandCapture(0, false, "cat " + source + " > " + destination);
                        Shell.startRootShell().add(command);
                        commandWait(command);

                        if (preserveFileAttributes) {
                            // set premissions of source to destination
                            command = new CommandCapture(0, false, "chmod " + filePermission + " " + destination);
                            Shell.startRootShell().add(command);
                            commandWait(command);
                        }
                    } else {
                        result = false;
                    }
                }
            }

            // mount destination back to ro
            if (remountAsRw) {
                RootTools.remount(destination, "RO");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        if (command != null) {
            result = command.getExitCode() == 0;
        }

        return result;
    }

    public boolean checkUtil(String util) {
        if (RootTools.findBinary(util)) {

            List<String> binaryPaths = new ArrayList<String>();
            binaryPaths.addAll(RootTools.lastFoundBinaryPaths);

            for (String path : binaryPaths) {
                Permissions permissions = RootTools
                        .getFilePermissionsSymlinks(path + "/" + util);

                if (permissions != null) {
                    String permission;

                    if (Integer.toString(permissions.getPermissions()).length() > 3)
                        permission = Integer.toString(permissions.getPermissions()).substring(1);
                    else
                        permission = Integer.toString(permissions.getPermissions());

                    if (permission.equals("755") || permission.equals("777")
                            || permission.equals("775")) {
                        RootTools.utilPath = path + "/" + util;
                        return true;
                    }
                }
            }
        }

        return false;

    }

    public boolean deleteFileOrDirectory(String target, boolean remountAsRw) {
        boolean result = true;

        try {
            // mount destination as rw before writing to it
            if (remountAsRw) {
                RootTools.remount(target, "RW");
            }

            if (hasUtil("rm", "toolbox")) {
                RootTools.log("rm command is available!");

                CommandCapture command = new CommandCapture(0, false, "rm -r " + target);
                Shell.startRootShell().add(command);
                commandWait(command);

                if (command.getExitCode() != 0) {
                    RootTools.log("target not exist or unable to delete file");
                    result = false;
                }
            } else {
                if (checkUtil("busybox") && hasUtil("rm", "busybox")) {
                    RootTools.log("busybox cp command is available!");

                    CommandCapture command = new CommandCapture(0, false, "busybox rm -rf " + target);
                    Shell.startRootShell().add(command);
                    commandWait(command);

                    if (command.getExitCode() != 0) {
                        RootTools.log("target not exist or unable to delete file");
                        result = false;
                    }
                }
            }

            // mount destination back to ro
            if (remountAsRw) {
                RootTools.remount(target, "RO");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    public boolean exists(final String file) {
        final List<String> result = new ArrayList<String>();

        CommandCapture command = new CommandCapture(0, false, "ls " + file) {
            @Override
            public void output(int arg0, String arg1) {
                RootTools.log(arg1);
                result.add(arg1);
            }
        };

        try {
            //Try not to open a new shell if one is open.
            if (!Shell.isAnyShellOpen()) {
                Shell.startShell().add(command);
                commandWait(command);

            }
            else {
                Shell.getOpenShell().add(command);
                commandWait(command);
            }
        } catch (Exception e) {
            return false;
        }

        for (String line : result) {
            if (line.trim().equals(file)) {
                return true;
            }
        }

        try {
            RootTools.closeShell(false);
        } catch (Exception e) {
        }

        result.clear();
        try {
            Shell.startRootShell().add(command);
            commandWait(command);

        } catch (Exception e) {
            return false;
        }

        //Avoid concurrent modification...
        List<String> final_result = new ArrayList<String>();
        final_result.addAll(result);

        for (String line : final_result) {
            if (line.trim().equals(file)) {
                return true;
            }
        }

        return false;

    }

     public void fixUtil(String util, String utilPath) {
        try {
            RootTools.remount("/system", "rw");

            if (RootTools.findBinary(util)) {
                List<String> paths = new ArrayList<String>();
                paths.addAll(RootTools.lastFoundBinaryPaths);
                for (String path : paths) {
                    CommandCapture command = new CommandCapture(0, false, utilPath + " rm " + path + "/" + util);
                    Shell.startRootShell().add(command);
                    commandWait(command);

                }

                CommandCapture command = new CommandCapture(0, false, utilPath + " ln -s " + utilPath + " /system/bin/" + util, utilPath + " chmod 0755 /system/bin/" + util);
                Shell.startRootShell().add(command);
                commandWait(command);

            }

            RootTools.remount("/system", "ro");
        } catch (Exception e) {
        }
    }

     public boolean fixUtils(String[] utils) throws Exception {

        for (String util : utils) {
            if (!checkUtil(util)) {
                if (checkUtil("busybox")) {
                    if (hasUtil(util, "busybox")) {
                        fixUtil(util, RootTools.utilPath);
                    }
                } else {
                    if (checkUtil("toolbox")) {
                        if (hasUtil(util, "toolbox")) {
                            fixUtil(util, RootTools.utilPath);
                        }
                    } else {
                        return false;
                    }
                }
            }
        }

        return true;
    }

   public boolean findBinary(String binaryName) {
        boolean found = false;
        RootTools.lastFoundBinaryPaths.clear();

        List<String> list = new ArrayList<String>();

        RootTools.log("Checking for " + binaryName);

        try {
            Set<String> paths = RootTools.getPath();
            if (paths.size() > 0) {
                for (String path : paths) {
                    if (RootTools.exists(path + "/" + binaryName)) {
                        RootTools.log(binaryName + " was found here: " + path);
                        list.add(path);
                        found = true;
                    } else {
                        RootTools.log(binaryName + " was NOT found here: " + path);
                    }
                }
            }
        } catch (TimeoutException ex) {
            RootTools.log("TimeoutException!!!");
        } catch (Exception e) {
            RootTools.log(binaryName + " was not found, more information MAY be available with Debugging on.");
        }

        if (!found) {
            RootTools.log("Trying second method");
            RootTools.log("Checking for " + binaryName);
            String[] places = {"/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/",
                    "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"};
            for (String where : places) {
                if (RootTools.exists(where + binaryName)) {
                    RootTools.log(binaryName + " was found here: " + where);
                    list.add(where);
                    found = true;
                } else {
                    RootTools.log(binaryName + " was NOT found here: " + where);
                }
            }
        }

        if (RootTools.debugMode) {
            for (String path : list) {
                RootTools.log("Paths: " + path);
            }
        }

        Collections.reverse(list);

        RootTools.lastFoundBinaryPaths.addAll(list);

        return found;
    }

      public List<String> getBusyBoxApplets(String path) throws Exception {

        if (path != null && !path.endsWith("/") && !path.equals("")) {
            path += "/";
        } else if (path == null) {
            //Don't know what the user wants to do...what am I pshycic?
            throw new Exception("Path is null, please specifiy a path");
        }

        final List<String> results = new ArrayList<String>();

        CommandCapture command = new CommandCapture(Constants.BBA, false, path + "busybox --list") {

            @Override
            public void output(int id, String line) {
                if (id == Constants.BBA) {
                    if (!line.trim().equals("") && !line.trim().contains("not found")) {
                        results.add(line);
                    }
                }
            }
        };
        Shell.startRootShell().add(command);
        commandWait(command);

        return results;
    }

      public String getBusyBoxVersion(String path) {

        if (!path.equals("") && !path.endsWith("/")) {
            path += "/";
        }

        RootTools.log("Getting BusyBox Version");
        InternalVariables.busyboxVersion = "";
        try {
            CommandCapture command = new CommandCapture(Constants.BBV, false, path + "busybox") {
                @Override
                public void output(int id, String line) {
                    if (id == Constants.BBV) {
                        if (line.startsWith("BusyBox") && InternalVariables.busyboxVersion.equals("")) {
                            String[] temp = line.split(" ");
                            InternalVariables.busyboxVersion = temp[1];
                        }
                    }
                }
            };
            Shell.startRootShell().add(command);
            commandWait(command);

        } catch (Exception e) {
            RootTools.log("BusyBox was not found, more information MAY be available with Debugging on.");
            return "";
        }

        return InternalVariables.busyboxVersion;
    }

     public long getConvertedSpace(String spaceStr) {
        try {
            double multiplier = 1.0;
            char c;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < spaceStr.length(); i++) {
                c = spaceStr.charAt(i);
                if (!Character.isDigit(c) && c != '.') {
                    if (c == 'm' || c == 'M') {
                        multiplier = 1024.0;
                    } else if (c == 'g' || c == 'G') {
                        multiplier = 1024.0 * 1024.0;
                    }
                    break;
                }
                sb.append(spaceStr.charAt(i));
            }
            return (long) Math.ceil(Double.valueOf(sb.toString()) * multiplier);
        } catch (Exception e) {
            return -1;
        }
    }
  public String getInode(String file) {
        try {
            CommandCapture command = new CommandCapture(Constants.GI, false, "/data/local/ls -i " + file) {

                @Override
                public void output(int id, String line) {
                    if (id == Constants.GI) {
                        if (!line.trim().equals("") && Character.isDigit((char) line.trim().substring(0, 1).toCharArray()[0])) {
                            InternalVariables.inode = line.trim().split(" ")[0];
                        }
                    }
                }
            };
            Shell.startRootShell().add(command);
            commandWait(command);

            return InternalVariables.inode;
        } catch (Exception ignore) {
            return "";
        }
    }
    public boolean isAccessGiven() {
        try {
            RootTools.log("Checking for Root access");
            InternalVariables.accessGiven = false;

            CommandCapture command = new CommandCapture(Constants.IAG, false, "id") {
                @Override
                public void output(int id, String line) {
                    if (id == Constants.IAG) {
                        Set<String> ID = new HashSet<String>(Arrays.asList(line.split(" ")));
                        for (String userid : ID) {
                            RootTools.log(userid);

                            if (userid.toLowerCase().contains("uid=0")) {
                                InternalVariables.accessGiven = true;
                                RootTools.log("Access Given");
                                break;
                            }
                        }
                        if (!InternalVariables.accessGiven) {
                            RootTools.log("Access Denied?");
                        }
                    }
                }
            };
            Shell.startRootShell().add(command);
            commandWait(command);

            return InternalVariables.accessGiven;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isNativeToolsReady(int nativeToolsId, Context context) {
        RootTools.log("Preparing Native Tools");
        InternalVariables.nativeToolsReady = false;

        Installer installer;
        try {
            installer = new Installer(context);
        } catch (IOException ex) {
            if (RootTools.debugMode) {
                ex.printStackTrace();
            }
            return false;
        }

        if (installer.isBinaryInstalled("nativetools")) {
            InternalVariables.nativeToolsReady = true;
        } else {
            InternalVariables.nativeToolsReady = installer.installBinary(nativeToolsId,
                    "nativetools", "700");
        }
        return InternalVariables.nativeToolsReady;
    }

     public Permissions getFilePermissionsSymlinks(String file) {
        RootTools.log("Checking permissions for " + file);
        if (RootTools.exists(file)) {
            RootTools.log(file + " was found.");
            try {

                CommandCapture command = new CommandCapture(
                        Constants.FPS, false, "ls -l " + file,
                        "busybox ls -l " + file,
                        "/system/bin/failsafe/toolbox ls -l " + file,
                        "toolbox ls -l " + file) {
                    @Override
                    public void output(int id, String line) {
                        if (id == Constants.FPS) {
                            String symlink_final = "";

                            String[] lineArray = line.split(" ");
                            if (lineArray[0].length() != 10) {
                                return;
                            }

                            RootTools.log("Line " + line);

                            try {
                                String[] symlink = line.split(" ");
                                if (symlink[symlink.length - 2].equals("->")) {
                                    RootTools.log("Symlink found.");
                                    symlink_final = symlink[symlink.length - 1];
                                }
                            } catch (Exception e) {}

                            try {
                                InternalVariables.permissions = getPermissions(line);
                                if (InternalVariables.permissions != null) {
                                    InternalVariables.permissions.setSymlink(symlink_final);
                                }
                            } catch (Exception e) {
                                RootTools.log(e.getMessage());
                            }
                        }
                    }
                };
                Shell.startRootShell().add(command);
                commandWait(command);

                return InternalVariables.permissions;

            } catch (Exception e) {
                RootTools.log(e.getMessage());
                return null;
            }
        }

        return null;
    }
public ArrayList<Mount> getMounts() throws Exception {

        Shell shell = RootTools.getShell(true);

        CommandCapture cmd = new CommandCapture(0,
                "cat /proc/mounts > /data/local/RootToolsMounts",
                "chmod 0777 /data/local/RootToolsMounts");
        shell.add(cmd);
        this.commandWait(cmd);

        LineNumberReader lnr = null;
        FileReader fr = null;

        try {
            fr = new FileReader("/data/local/RootToolsMounts");
            lnr = new LineNumberReader(fr);
            String line;
            ArrayList<Mount> mounts = new ArrayList<Mount>();
            while ((line = lnr.readLine()) != null) {

                RootTools.log(line);

                String[] fields = line.split(" ");
                mounts.add(new Mount(new File(fields[0]), // device
                        new File(fields[1]), // mountPoint
                        fields[2], // fstype
                        fields[3] // flags
                ));
            }
            InternalVariables.mounts = mounts;

            if (InternalVariables.mounts != null) {
                return InternalVariables.mounts;
            } else {
                throw new Exception();
            }
        } finally {
            try {
                fr.close();
                fr = null;
            } catch (Exception e) {}

            try {
                lnr.close();
                lnr = null;
            } catch (Exception e) {}
        }
    }

   public String getMountedAs(String path) throws Exception {
        InternalVariables.mounts = getMounts();
        String mp;
        if (InternalVariables.mounts != null) {
            for (Mount mount : InternalVariables.mounts) {

                mp = mount.getMountPoint().getAbsolutePath();

                if (mp.equals("/")) {
                    if (path.equals("/")) {
                        return (String) mount.getFlags().toArray()[0];
                    }
                    else {
                        continue;
                    }
                }

                if (path.equals(mp) || path.startsWith(mp + "/")) {
                    RootTools.log((String) mount.getFlags().toArray()[0]);
                    return (String) mount.getFlags().toArray()[0];
                }
            }

            throw new Exception();
        } else {
            throw new Exception();
        }
    }

    public Set<String> getPath() throws Exception {
        if (InternalVariables.path != null) {
            return InternalVariables.path;
        } else {
            if (returnPath()) {
                return InternalVariables.path;
            } else {
                throw new Exception();
            }
        }
    }

    public long getSpace(String path) {
        InternalVariables.getSpaceFor = path;
        boolean found = false;
        RootTools.log("Looking for Space");
        try {
            final CommandCapture command = new CommandCapture(Constants.GS, false, "df " + path) {

                @Override
                public void output(int id, String line) {
                    if (id == Constants.GS) {
                        if (line.contains(InternalVariables.getSpaceFor.trim())) {
                            InternalVariables.space = line.split(" ");
                        }
                    }
                }
            };
            Shell.startRootShell().add(command);
            commandWait(command);

        } catch (Exception e) {}

        if (InternalVariables.space != null) {
            RootTools.log("First Method");

            for (String spaceSearch : InternalVariables.space) {

                RootTools.log(spaceSearch);

                if (found) {
                    return getConvertedSpace(spaceSearch);
                } else if (spaceSearch.equals("used,")) {
                    found = true;
                }
            }

            // Try this way
            int count = 0, targetCount = 3;

            RootTools.log("Second Method");

            if (InternalVariables.space[0].length() <= 5) {
                targetCount = 2;
            }

            for (String spaceSearch : InternalVariables.space) {

                RootTools.log(spaceSearch);
                if (spaceSearch.length() > 0) {
                    RootTools.log(spaceSearch + ("Valid"));
                    if (count == targetCount) {
                        return getConvertedSpace(spaceSearch);
                    }
                    count++;
                }
            }
        }
        RootTools.log("Returning -1, space could not be determined.");
        return -1;
    }

  public String getSymlink(String file) {
        RootTools.log("Looking for Symlink for " + file);

        try {
            final List<String> results = new ArrayList<String>();

            CommandCapture command = new CommandCapture(Constants.GSYM, false, "ls -l " + file) {

                @Override
                public void output(int id, String line) {
                    if (id == Constants.GSYM) {
                        if (!line.trim().equals("")) {
                            results.add(line);
                        }
                    }
                }
            };
            Shell.startRootShell().add(command);
            commandWait(command);

            String[] symlink = results.get(0).split(" ");
            if (symlink.length > 2 && symlink[symlink.length - 2].equals("->")) {
                RootTools.log("Symlink found.");

                String final_symlink = "";
                if (!symlink[symlink.length - 1].equals("") && !symlink[symlink.length - 1].contains("/")) {
                    //We assume that we need to get the path for this symlink as it is probably not absolute.
                    findBinary(symlink[symlink.length - 1]);
                    if (RootTools.lastFoundBinaryPaths.size() > 0) {
                        //We return the first found location.
                        final_symlink = RootTools.lastFoundBinaryPaths.get(0) + "/" + symlink[symlink.length - 1];
                    } else {
                        //we couldnt find a path, return the symlink by itself.
                        final_symlink = symlink[symlink.length - 1];
                    }
                } else {
                    final_symlink = symlink[symlink.length - 1];
                }

                return final_symlink;
            }
        } catch (Exception e) {
            if (RootTools.debugMode)
                e.printStackTrace();
        }

        RootTools.log("Symlink not found");
        return "";
    }

  public ArrayList<Symlink> getSymlinks(String path) throws Exception {

        // this command needs find
        if (!checkUtil("find")) {
            throw new Exception();
        }

        CommandCapture command = new CommandCapture(0, false, "dd if=/dev/zero of=/data/local/symlinks.txt bs=1024 count=1", "chmod 0777 /data/local/symlinks.txt");
        Shell.startRootShell().add(command);
        commandWait(command);

        command = new CommandCapture(0, false, "find " + path + " -type l -exec ls -l {} \\; > /data/local/symlinks.txt");
        Shell.startRootShell().add(command);
        commandWait(command);

        InternalVariables.symlinks = getSymLinks();
        if (InternalVariables.symlinks != null) {
            return InternalVariables.symlinks;
        } else {
            throw new Exception();
        }
    }

     public String getWorkingToolbox() {
        if (RootTools.checkUtil("busybox")) {
            return "busybox";
        } else if (RootTools.checkUtil("toolbox")) {
            return "toolbox";
        } else {
            return "";
        }
    }

  public boolean hasEnoughSpaceOnSdCard(long updateSize) {
        RootTools.log("Checking SDcard size and that it is mounted as RW");
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return (updateSize < availableBlocks * blockSize);
    }

    public boolean hasUtil(final String util, final String box) {

        InternalVariables.found = false;

        // only for busybox and toolbox
        if (!(box.endsWith("toolbox") || box.endsWith("busybox"))) {
            return false;
        }

        try {

            CommandCapture command = new CommandCapture(0, false, box.endsWith("toolbox") ? box + " " + util : box + " --list") {

                @Override
                public void output(int id, String line) {
                    if (box.endsWith("toolbox")) {
                        if (!line.contains("no such tool")) {
                            InternalVariables.found = true;
                        }
                    } else if (box.endsWith("busybox")) {
                        // go through all lines of busybox --list
                        if (line.contains(util)) {
                            RootTools.log("Found util!");
                            InternalVariables.found = true;
                        }
                    }
                }
            };
            RootTools.getShell(true).add(command);
            commandWait(command);

            if (InternalVariables.found) {
                RootTools.log("Box contains " + util + " util!");
                return true;
            } else {
                RootTools.log("Box does not contain " + util + " util!");
                return false;
            }
        } catch (Exception e) {
            RootTools.log(e.getMessage());
            return false;
        }
    }

  public boolean installBinary(Context context, int sourceId, String destName, String mode) {
        Installer installer;

        try {
            installer = new Installer(context);
        } catch (IOException ex) {
            if (RootTools.debugMode) {
                ex.printStackTrace();
            }
            return false;
        }

        return (installer.installBinary(sourceId, destName, mode));
    }

     public boolean isBinaryAvailable(Context context, String binaryName) {
        Installer installer;

        try {
            installer = new Installer(context);
        } catch (IOException ex) {
            if (RootTools.debugMode) {
                ex.printStackTrace();
            }
            return false;
        }

        return (installer.isBinaryInstalled(binaryName));
    }

   public boolean isAppletAvailable(String applet, String binaryPath) {
        try {
            for (String aplet : getBusyBoxApplets(binaryPath)) {
                if (aplet.equals(applet)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            RootTools.log(e.toString());
            return false;
        }
    }

    public boolean isProcessRunning(final String processName) {

        RootTools.log("Checks if process is running: " + processName);

        InternalVariables.processRunning = false;

        try {
            CommandCapture command = new CommandCapture(0, false, "ps") {
                @Override
                public void output(int id, String line) {
                    if (line.contains(processName)) {
                        InternalVariables.processRunning = true;
                    }
                }
            };
            RootTools.getShell(true).add(command);
            commandWait(command);

        } catch (Exception e) {
            RootTools.log(e.getMessage());
        }

        return InternalVariables.processRunning;
    }

  public boolean killProcess(final String processName) {
        RootTools.log("Killing process " + processName);

        InternalVariables.pid_list = "";

        InternalVariables.processRunning = true;

        try {

            CommandCapture command = new CommandCapture(0, false, "ps") {
                @Override
                public void output(int id, String line) {
                    if (line.contains(processName)) {
                        Matcher psMatcher = InternalVariables.psPattern.matcher(line);

                        try {
                            if (psMatcher.find()) {
                                String pid = psMatcher.group(1);

                                InternalVariables.pid_list += " " + pid;
                                InternalVariables.pid_list = InternalVariables.pid_list.trim();

                                RootTools.log("Found pid: " + pid);
                            } else {
                                RootTools.log("Matching in ps command failed!");
                            }
                        } catch (Exception e) {
                            RootTools.log("Error with regex!");
                            e.printStackTrace();
                        }
                    }
                }
            };
            RootTools.getShell(true).add(command);
            commandWait(command);

            String pids = InternalVariables.pid_list;

            if (!pids.equals("")) {
                try {
                    // example: kill -9 1234 1222 5343
                    command = new CommandCapture(0, false, "kill -9 " + pids);
                    RootTools.getShell(true).add(command);
                    commandWait(command);

                    return true;
                } catch (Exception e) {
                    RootTools.log(e.getMessage());
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            RootTools.log(e.getMessage());
        }

        return false;
    }

        public void offerBusyBox(Activity activity) {
        RootTools.log("Launching Market for BusyBox");
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=stericson.busybox"));
        activity.startActivity(i);
    }

    public Intent offerBusyBox(Activity activity, int requestCode) {
        RootTools.log("Launching Market for BusyBox");
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=stericson.busybox"));
        activity.startActivityForResult(i, requestCode);
        return i;
    }

      public void offerSuperUser(Activity activity) {
        RootTools.log("Launching Market for SuperUser");
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=com.noshufou.android.su"));
        activity.startActivity(i);
    }

    public Intent offerSuperUser(Activity activity, int requestCode) {
        RootTools.log("Launching Market for SuperUser");
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=com.noshufou.android.su"));
        activity.startActivityForResult(i, requestCode);
        return i;
    }

    private void commandWait(Command cmd) throws Exception {

        while (!cmd.isFinished()) {

            RootTools.log(Constants.TAG, Shell.getOpenShell().getCommandQueuePositionString(cmd));

            synchronized (cmd) {
                try {
                    if (!cmd.isFinished()) {
                        cmd.wait(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (!cmd.isExecuting() && !cmd.isFinished()) {
                if (!Shell.isExecuting && !Shell.isReading) {
                    Log.e(Constants.TAG, "Waiting for a command to be executed in a shell that is not executing and not reading! \n\n Command: " + cmd.getCommand());
                    Exception e = new Exception();
                    e.setStackTrace(Thread.currentThread().getStackTrace());
                    e.printStackTrace();
                } else if (Shell.isExecuting && !Shell.isReading) {
                    Log.e(Constants.TAG, "Waiting for a command to be executed in a shell that is executing but not reading! \n\n Command: " + cmd.getCommand());
                    Exception e = new Exception();
                    e.setStackTrace(Thread.currentThread().getStackTrace());
                    e.printStackTrace();
                } else {
                    Log.e(Constants.TAG, "Waiting for a command to be executed in a shell that is not reading! \n\n Command: " + cmd.getCommand());
                    Exception e = new Exception();
                    e.setStackTrace(Thread.currentThread().getStackTrace());
                    e.printStackTrace();
                }
            }

        }
    }
}
