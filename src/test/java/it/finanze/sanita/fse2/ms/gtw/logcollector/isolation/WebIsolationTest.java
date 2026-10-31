package it.finanze.sanita.fse2.ms.gtw.logcollector.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import it.finanze.sanita.fse2.ms.gtw.logcollector.client.exceptions.ServerResponseException;
import it.finanze.sanita.fse2.ms.gtw.logcollector.controller.LivenessCheckCTL;
import it.finanze.sanita.fse2.ms.gtw.logcollector.controller.handler.ControllerExceptionHandler;
import it.finanze.sanita.fse2.ms.gtw.logcollector.controller.impl.ScheduleCTL;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.ResultDto;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.response.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.exceptions.*;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.*;
import it.finanze.sanita.fse2.ms.gtw.logcollector.scheduler.*;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.ISchedulerSrv;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig({ScheduleCTL.class, LivenessCheckCTL.class, ControllerExceptionHandler.class,
        ProcessControlLogScheduler.class, ProcessKpiLogScheduler.class})
class WebIsolationTest {
    @Autowired private ScheduleCTL controller;
    @Autowired private LivenessCheckCTL liveness;
    @Autowired private ControllerExceptionHandler advice;
    @Autowired private ProcessControlLogScheduler controlScheduler;
    @Autowired private ProcessKpiLogScheduler kpiScheduler;
    @MockitoBean private ISchedulerSrv scheduler;
    @MockitoBean private Tracer tracer;
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller, liveness).setControllerAdvice(advice).build();
        when(tracer.spanBuilder(anyString())).thenAnswer(invocation ->
                OpenTelemetry.noop().getTracer("isolation").spanBuilder(invocation.getArgument(0)));
    }

    @Test
    void livenessIsUpWithoutInfrastructure() throws Exception {
        mvc.perform(get("/status")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
        verifyNoInteractions(scheduler);
    }

    @Test
    void controlEndpointSerializesSchedulerResult() throws Exception {
        ResultDto result = new ResultDto();
        result.setPageSize(10);
        result.setTotalRecord(12);
        result.setProcessedLog(12);
        when(scheduler.scheduleLog(LogCollectorControlETY.class)).thenReturn(result);
        mvc.perform(get("/runControlLog")).andExpect(status().isOk())
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalRecord").value(12))
                .andExpect(jsonPath("$.processedLog").value(12));
        verify(scheduler).scheduleLog(LogCollectorControlETY.class);
    }

    @Test
    void kpiEndpointUsesKpiEntity() throws Exception {
        when(scheduler.scheduleLog(LogCollectorKpiETY.class)).thenReturn(new ResultDto());
        mvc.perform(get("/runKpiLog")).andExpect(status().isOk());
        verify(scheduler).scheduleLog(LogCollectorKpiETY.class);
    }

    @Test
    void genericFailureReturnsProblemWithoutLeakingCause() throws Exception {
        when(scheduler.scheduleLog(LogCollectorControlETY.class)).thenThrow(new IllegalStateException("secret database password"));
        mvc.perform(get("/runControlLog")).andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("/msg/generic-error"))
                .andExpect(jsonPath("$.detail").value("Errore generico"));
    }

    @Test
    void validationFailurePreservesStructuredProblem() throws Exception {
        ErrorResponseDTO error = new ErrorResponseDTO(new LogTraceInfoDTO("span", "trace"),
                "/msg/invalid", "Invalid", "bad value", 400, "/request");
        when(scheduler.scheduleLog(LogCollectorControlETY.class)).thenThrow(new ValidationException(error));
        mvc.perform(get("/runControlLog")).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("bad value"))
                .andExpect(jsonPath("$.traceID").value("trace"));
    }

    @Test
    void connectionFailureReturnsSpecificProblem() throws Exception {
        when(scheduler.scheduleLog(LogCollectorControlETY.class)).thenThrow(new ConnectionRefusedException("http://offline", "offline"));
        mvc.perform(get("/runControlLog")).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.type").value("/msg/connection-refused"))
                .andExpect(jsonPath("$.detail").value("offline"));
    }

    @Test
    void upstreamFailureReturnsBadRequestProblem() throws Exception {
        when(scheduler.scheduleLog(LogCollectorControlETY.class)).thenThrow(new ServerResponseException("upstream failed"));
        mvc.perform(get("/runControlLog")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("/msg/server-error"))
                .andExpect(jsonPath("$.title").value("Server error"));
    }

    @Test
    void missingSpanBuilderStillReturnsGenericProblem() throws Exception {
        when(tracer.spanBuilder(anyString())).thenReturn(null);
        when(scheduler.scheduleLog(LogCollectorControlETY.class)).thenThrow(new IllegalStateException());
        mvc.perform(get("/runControlLog")).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.type").value("/msg/generic-error"));
    }

    @Test
    void scheduledEntrypointsUseTheirOwnEntityTypes() {
        controlScheduler.schedulingTask();
        kpiScheduler.schedulingTask();
        verify(scheduler).scheduleLog(LogCollectorControlETY.class);
        verify(scheduler).scheduleLog(LogCollectorKpiETY.class);
    }

    @Test
    void schedulerEntrypointPropagatesFailure() {
        IllegalStateException cause = new IllegalStateException("failed");
        when(scheduler.scheduleLog(LogCollectorKpiETY.class)).thenThrow(cause);
        assertSame(cause, assertThrows(IllegalStateException.class, kpiScheduler::schedulingTask));
    }
}
