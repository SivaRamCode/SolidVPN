
package app.openconnect.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import app.openconnect.RootTools;
import app.openconnect.execution.Command;
import app.openconnect.execution.CommandCapture;
import app.openconnect.execution.Shell;

import android.content.Context;
import android.util.Log;

class Installer {

    static final String LOG_TAG = "RootTools::Installer";

    static final String BOGUS_FILE_NAME = "bogus";

    Context context;
    String filesPath;

    public Installer(Context context)
            throws IOException {

        this.context = context;
        this.filesPath = context.getFilesDir().getCanonicalPath();
    }

    protected boolean installBinary(int sourceId, String destName, String mode) {
        File mf = new File(filesPath + File.separator + destName);
        if (!mf.exists()) {
             try {
                FileInputStream fis = context.openFileInput(BOGUS_FILE_NAME);
                fis.close();
            } catch (FileNotFoundException e) {
                FileOutputStream fos = null;
                try {
                    fos = context.openFileOutput("bogus", Context.MODE_PRIVATE);
                    fos.write("justcreatedfilesdirectory".getBytes());
                } catch (Exception ex) {
                    if (RootTools.debugMode) {
                        Log.e(LOG_TAG, ex.toString());
                    }
                    return false;
                } finally {
                    if (null != fos) {
                        try {
                            fos.close();
                            context.deleteFile(BOGUS_FILE_NAME);
                        } catch (IOException e1) {}
                    }
                }
            } catch (IOException ex) {
                if (RootTools.debugMode) {
                    Log.e(LOG_TAG, ex.toString());
                }
                return false;
            }

           InputStream iss = context.getResources().openRawResource(sourceId);
            FileOutputStream oss = null;
            try {
                oss = new FileOutputStream(mf);
                byte[] buffer = new byte[4096];
                int len;
                try {
                    while (-1 != (len = iss.read(buffer))) {
                        oss.write(buffer, 0, len);
                    }
                } catch (IOException ex) {
                    if (RootTools.debugMode) {
                        Log.e(LOG_TAG, ex.toString());
                    }
                    return false;
                }
            } catch (FileNotFoundException ex) {
                if (RootTools.debugMode) {
                    Log.e(LOG_TAG, ex.toString());
                }
                return false;
            } finally {
                if (oss != null) {
                    try {
                        oss.close();
                    } catch (IOException e) {
                    }
                }
            }
            try {
                iss.close();
            } catch (IOException ex) {
                if (RootTools.debugMode) {
                    Log.e(LOG_TAG, ex.toString());
                }
                return false;
            }

            try {
                CommandCapture command = new CommandCapture(0, false, "chmod " + mode + " " + filesPath + File.separator + destName);
                Shell.startShell().add(command);
                commandWait(command);

            } catch (Exception e) {}
        }
        return true;
    }

    protected boolean isBinaryInstalled(String destName) {
        boolean installed = false;
        File mf = new File(filesPath + File.separator + destName);
        if (mf.exists()) {
            installed = true;
            // TODO: pass mode as argument and check it matches
        }
        return installed;
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
