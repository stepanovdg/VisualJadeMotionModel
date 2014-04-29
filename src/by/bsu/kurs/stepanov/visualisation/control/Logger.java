package by.bsu.kurs.stepanov.visualisation.control;


import by.bsu.kurs.stepanov.utils.ExceptionUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 20.04.14
 * Time: 20:58
 * To change this template use File | Settings | File Templates.
 */
public class Logger {

    private Writer bw;
    private String path;

    public Logger() {
        init();
    }

    private void init() {
        try {
            if (this.bw != null) {
                this.bw.close();
            }
            File dest = File.createTempFile("VisualJade", "log");
            this.path = dest.getAbsolutePath();
            this.bw = new BufferedWriter(new FileWriter(dest));
        } catch (IOException e) {
            ExceptionUtils.handleException(e, "Exception during initializing log writer");
        }
    }

    public void write(String s) {
        try {
            StringBuilder sb = new StringBuilder();
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            sb.append("[").append(ts).append("] ").append(s).append('\n').append('\r');
            bw.append(sb);
        } catch (IOException e) {
            ExceptionUtils.handleException(e, "Exception during writing to log");
        }
    }

    public void export(File destFile) {
        String previousPath = this.path;
        init();
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(previousPath);
            FileChannel src = fis.getChannel();
            fos = new FileOutputStream(destFile);
            FileChannel dest = fos.getChannel();
            src.transferTo(0, src.size(), dest);
        } catch (IOException e) {
            ExceptionUtils.handleException(e, "Exception during log export");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignored) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
