
package app.openconnect.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import app.openconnect.containers.Mount;
import app.openconnect.containers.Permissions;
import app.openconnect.containers.Symlink;

public class InternalVariables {



    protected static boolean accessGiven = false;
    protected static boolean nativeToolsReady = false;
    protected static boolean found = false;
    protected static boolean processRunning = false;

    protected static String[] space;
    protected static String getSpaceFor;
    protected static String busyboxVersion;
    protected static String pid_list = "";
    protected static Set<String> path;
    protected static ArrayList<Mount> mounts;
    protected static ArrayList<Symlink> symlinks;
    protected static List<String> results;
    protected static String inode = "";
    protected static Permissions permissions;

    protected static final String PS_REGEX = "^\\S+\\s+([0-9]+).*$";
    protected static Pattern psPattern;

    static {
        psPattern = Pattern.compile(PS_REGEX);
    }
}
