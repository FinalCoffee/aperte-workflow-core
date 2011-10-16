package pl.net.bluesoft.rnd.util.vaadin;

import com.vaadin.Application;
import pl.net.bluesoft.util.lang.Lambda;

/**
 * Created by IntelliJ IDEA.
 * User: tomek
 * Date: 4/23/11
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
public interface VaadinExceptionHandler {

    class Util {
        public static void onException(Object handler, Exception e) {
            if (handler instanceof VaadinExceptionHandler) {
                ((VaadinExceptionHandler)handler).onException(e);
            } else {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException)e;
                } else {
                    throw new RuntimeException(e);
                }
            }
        }

        public static void withErrorHandling(Object handler, Runnable x) {
            try {
                x.run();
            }
            catch (Exception e) {
                onException(handler, e);
            }
        }
    }

    void onException(Exception e);
}
