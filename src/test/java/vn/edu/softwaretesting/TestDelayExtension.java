package vn.edu.softwaretesting;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import java.util.concurrent.locks.LockSupport;

public class TestDelayExtension
        implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(
            ExtensionContext context) {

        System.out.println(
                "Hoan thanh: " + context.getDisplayName()
        );

        LockSupport.parkNanos(2_000_000_000L);
    }
}