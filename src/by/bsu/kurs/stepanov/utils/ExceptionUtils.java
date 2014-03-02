package by.bsu.kurs.stepanov.utils;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 22.02.14
 * Time: 17:20
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionUtils {

    public static void handleException(Throwable e) {
        handleException(e, null);
    }

    public static void handleException(Throwable e, String msg) {
        //TODO implement norm
        System.err.println(msg);
        System.err.println(e.getMessage());
        e.printStackTrace();
    }
}
