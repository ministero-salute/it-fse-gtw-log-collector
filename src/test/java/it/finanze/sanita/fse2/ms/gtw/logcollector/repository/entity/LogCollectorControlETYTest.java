package it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

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