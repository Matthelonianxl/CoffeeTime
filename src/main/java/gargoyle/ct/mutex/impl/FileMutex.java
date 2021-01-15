package gargoyle.ct.mutex.impl;

import gargoyle.ct.log.Log;
import gargoyle.ct.mutex.CTMutex;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

public final class FileMutex implements CTMutex {
    public static final String MSG_MUTEX_ERROR = "file mutex error";
    private static final String SUFFIX = ".lock";
    private static final String TMPDIR = "java.io.tmpdir";
    private static final String USER_DIR = "user.dir";
    private static final String USER_HOME = "user.home";
    private final String mutex;
    @Nullable
    private FileChannel channel;
    @Nullable
    private File file;
    @Nullable
    private FileLock fileLock;
    @Nullable
    private RandomAccessFile randomAccessFile;

    public FileMutex(String mutex) {
        this.mutex = mutex;
    }

    @Override
    public synchronized boolean acquire() {
        if (file != null && file.exists()) {
            return false;
        }
        try {
            file = new File(new File(System.getProperty(TMPDIR,
                    System.getProperty(USER_HOME,
                            System.getProperty(USER_DIR, ".")))),
                    mutex + SUFFIX);
            randomAccessFile = new RandomAccessFile(file, "rw");
            channel = randomAccessFile.getChannel();
            fileLock = channel.tryLock();
            if (fileLock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread(this::release));
                file.deleteOnExit();
                return true;
            }
        } catch (IOException ex) {
            Log.error(ex, MSG_MUTEX_ERROR);
            return false;
        } catch (OverlappingFileLockException ex) {
            return false;
        }
        return false;
    }

    @Override
    public synchronized void release() {
        if (file != null && file.exists() && fileLock != null && randomAccessFile != null) {
            try {
                fileLock.release();
                randomAccessFile.close();
                if (!file.delete()) {
                    throw new IOException(MSG_MUTEX_ERROR);
                }
                fileLock = null;
                randomAccessFile = null;
                file = null;
                channel = null;
            } catch (IOException ex) {
                Log.error(ex, MSG_MUTEX_ERROR);
            }
        }
    }
}
