package it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity;

import it.finanze.sanita.fse2.ms.gtw.logcollector.config.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

class LogCollectorControlETYTest {

    @Test
    void gettersAndSettersTest() {
        LogCollectorControlETY controlETY = new LogCollectorControlETY();

        String workflowInstanceId = "123";
        controlETY.setWorkflowInstanceId(workflowInstanceId);

        assertEquals(workflowInstanceId, controlETY.getWorkflowInstanceId());
    }


    @Test
    void testEqualsAndHashCode() {
        LogCollectorControlETY control1 = new LogCollectorControlETY();
        control1.setTypeIdExtension("test");

        LogCollectorControlETY control2 = new LogCollectorControlETY();
        control2.setTypeIdExtension("test");

        assertEquals(control1, control2);

        assertEquals(control1.hashCode(), control2.hashCode());
    }

}