package app.openconnect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import app.openconnect.containers.Mount;
import app.openconnect.containers.Permissions;
import app.openconnect.containers.Symlink;
import app.openconnect.exceptions.RootDeniedException;
import app.openconnect.execution.Command;
import app.openconnect.execution.Shell;
import app.openconnect.internal.Remounter;
import app.openconnect.internal.RootToolsInternalMethods;
import app.openconnect.internal.Runner;

public final class RootTools {

    private static RootToolsInternalMethods rim = null;

    public static void setRim(RootToolsInternalMethods rim) {
        RootTools.rim = rim;
    }

    private static final RootToolsInternalMethods getInternals() {
        if (rim == null) {
            RootToolsInternalMethods.getInstance();
            return rim;
        } else {
            return rim;
        }
    }

    public static boolean debugMode = false;
    public static List<String> lastFoundBinaryPaths = new ArrayList<String>();
    public static String utilPath;

    public static boolean handlerEnabled = true;


    public static int default_Command_Timeout = 20000;
   public static boolean checkUtil(String util) {

        return getInternals().checkUtil(util);
    }

     public static void closeAllShells() throws IOException {
        Shell.closeAll();
    }

    public static void closeCustomShell() throws IOException {
        Shell.closeCustomShell();
    }

   public static void closeShell(boolean root) throws IOException {
        if (root)
            Shell.closeRootShell();
        else
            Shell.closeShell();
    }

    public static boolean copyFile(String source, String destination, boolean remountAsRw,
                                   boolean preserveFileAttributes) {
        return getInternals().copyFile(source, destination, remountAsRw, preserveFileAttributes);
    }

    public static boolean deleteFileOrDirectory(String target, boolean remountAsRw) {
        return getInternals().deleteFileOrDirectory(target, remountAsRw);
    }

   public static boolean exists(final String file) {
        return getInternals().exists(file);
    }

   public static void fixUtil(String util, String utilPath) {
        getInternals().fixUtil(util, utilPath);
    }

    public static boolean fixUtils(String[] utils) throws Exception {
        return getInternals().fixUtils(utils);
    }

    public static boolean findBinary(String binaryName) {
        return getInternals().findBinary(binaryName);
    }

     public static String getBusyBoxVersion(String path) {
        return getInternals().getBusyBoxVersion(path);
    }

    public static String getBusyBoxVersion() {
        return RootTools.getBusyBoxVersion("");
    }

     public static List<String> getBusyBoxApplets() throws Exception {
        return RootTools.getBusyBoxApplets("");
    }

    public static List<String> getBusyBoxApplets(String path) throws Exception {
        return getInternals().getBusyBoxApplets(path);
    }

     public static Shell getCustomShell(String shellPath, int timeout) throws IOException, TimeoutException, RootDeniedException {
        return Shell.startCustomShell(shellPath, timeout);
    }

    public static Shell getCustomShell(String shellPath) throws IOException, TimeoutException, RootDeniedException {
        return RootTools.getCustomShell(shellPath, 10000);
    }

    public static Permissions getFilePermissionsSymlinks(String file) {
        return getInternals().getFilePermissionsSymlinks(file);
    }

    public static String getInode(String file) {
        return getInternals().getInode(file);
    }

   public static ArrayList<Mount> getMounts() throws Exception {
        return getInternals().getMounts();
    }

    public static String getMountedAs(String path) throws Exception {
        return getInternals().getMountedAs(path);
    }

    public static Set<String> getPath() throws Exception {
        return getInternals().getPath();
    }

    public static Shell getShell(boolean root, int timeout, int retry) throws IOException, TimeoutException, RootDeniedException {
        if (root)
            return Shell.startRootShell(timeout);
        else
            return Shell.startShell(timeout);
    }

     public static Shell getShell(boolean root, int timeout) throws IOException, TimeoutException, RootDeniedException {
        return getShell(root, timeout, 3);
    }

     public static Shell getShell(boolean root) throws IOException, TimeoutException, RootDeniedException {
        return RootTools.getShell(root, 25000);
    }

    public static long getSpace(String path) {
        return getInternals().getSpace(path);
    }

     public static String getSymlink(String file) {
        return getInternals().getSymlink(file);
    }

       public static ArrayList<Symlink> getSymlinks(String path) throws Exception {
        return getInternals().getSymlinks(path);
    }

    public static String getWorkingToolbox() {
        return getInternals().getWorkingToolbox();
    }

       public static boolean hasEnoughSpaceOnSdCard(long updateSize) {
        return getInternals().hasEnoughSpaceOnSdCard(updateSize);
    }

   public static boolean hasUtil(final String util, final String box) {
        //TODO Convert this to use the new shell.
        return getInternals().hasUtil(util, box);
    }

 public static boolean installBinary(Context context, int sourceId, String destName, String mode) {
        return getInternals().installBinary(context, sourceId, destName, mode);
    }

      public static boolean installBinary(Context context, int sourceId, String binaryName) {
        return installBinary(context, sourceId, binaryName, "700");
    }

       public static boolean hasBinary(Context context, String binaryName) {
        return getInternals().isBinaryAvailable(context, binaryName);
    }

   public static boolean isAppletAvailable(String applet, String path) {
        return getInternals().isAppletAvailable(applet, path);
    }

   public static boolean isAppletAvailable(String applet) {
        return RootTools.isAppletAvailable(applet, "");
    }

     public static boolean isAccessGiven() {
        return getInternals().isAccessGiven();
    }

     public static boolean isBusyboxAvailable() {
        return findBinary("busybox");
    }

    public static boolean isNativeToolsReady(int nativeToolsId, Context context) {
        return getInternals().isNativeToolsReady(nativeToolsId, context);
    }

      public static boolean isRootAvailable() {
        return findBinary("su");
    }

   public static boolean killProcess(final String processName) {
        //TODO convert to new shell
        return getInternals().killProcess(processName);
    }

    public static void offerBusyBox(Activity activity) {
        getInternals().offerBusyBox(activity);
    }

    public static Intent offerBusyBox(Activity activity, int requestCode) {
        return getInternals().offerBusyBox(activity, requestCode);
    }

     public static void offerSuperUser(Activity activity) {
        getInternals().offerSuperUser(activity);
    }

    public static Intent offerSuperUser(Activity activity, int requestCode) {
        return getInternals().offerSuperUser(activity, requestCode);
    }

     public static boolean remount(String file, String mountType) {
        // Recieved a request, get an instance of Remounter
        Remounter remounter = new Remounter();
        // send the request.
        return (remounter.remount(file, mountType));
    }

    public static void restartAndroid() {
        RootTools.log("Restart Android");
        killProcess("zygote");
    }

    public static void runBinary(Context context, String binaryName, String parameter) {
        Runner runner = new Runner(context, binaryName, parameter);
        runner.start();
    }
  public static void runShellCommand(Shell shell, Command command) throws IOException {
        shell.add(command);
    }

       public static void log(String msg) {
        log(null, msg, 3, null);
    }

        public static void log(String TAG, String msg) {
        log(TAG, msg, 3, null);
    }

      public static void log(String msg, int type, Exception e) {
        log(null, msg, type, e);
    }

   public static void log(String TAG, String msg, int type, Exception e) {
        if (msg != null && !msg.equals("")) {
            if (debugMode) {
                if (TAG == null) {
                    TAG = Constants.TAG;
                }

                switch (type) {
                    case 1:
                        Log.v(TAG, msg);
                        break;
                    case 2:
                        Log.e(TAG, msg, e);
                        break;
                    case 3:
                        Log.d(TAG, msg);
                        break;
                }
            }
        }
    }
}
