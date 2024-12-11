package it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity;

import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.IssuerDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.LocalityDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.SubjApplicationDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.enums.ProcessedStatusEnum;
import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class LogCollectorBaseTest {

    @Test
    public void testConstructorAndGetters() {
        String id = "123";
        String logType = "control-structured-log";
        String message = "Validation CDA completed for workflow instance Id 2.16.840.1.113883.2.9.2.120.4.4.46a41df0ab0514f11c0811056832c3225e06c8e11824f27c7e5517ca5cfc57fe.e0177e0824^^^^urn:ihe:iti:xdw:2013:workflowInstanceId";
        String operation = "VAL-CDA2";
        String result = "OK";
        String documentType = "Referto di Laboratorio";
        String role = "AAS";
        String fiscalcode = "string2";
        String microserviceName = "gtw-dispatcher";
        Date timestampStart = new Date();
        Date timestampEnd = new Date();
        String workFlowIstanceId = "test";
        String processed = ProcessedStatusEnum.UNPROCESSED.getCode();

        IssuerDTO opIssuer = new IssuerDTO();
        opIssuer.setRawValue("integrity:S1#190201234567XX");
        opIssuer.setRegion("190");

        LocalityDTO opLocality = new LocalityDTO();
        opLocality.setRawValue("201123456");
        opLocality.setAslCode("201");
        opLocality.setStructure("123456");

        SubjApplicationDTO subjApplication = new SubjApplicationDTO();
        subjApplication.setSubject_application_version("R2");
        subjApplication.setSubject_application_vendor("GATEWAY");
        subjApplication.setSubject_application_version("GTW");

        LogCollectorBase logCollectorBase = new LogCollectorBase(id,logType,message,operation,result,timestampStart,timestampEnd,documentType,role,fiscalcode,microserviceName,subjApplication,processed,workFlowIstanceId);
        assertEquals(id, logCollectorBase.getId());
        assertEquals(logType, logCollectorBase.getLogType());
        assertEquals(message, logCollectorBase.getMessage());
        assertEquals(operation, logCollectorBase.getOperation());
        assertEquals(result, logCollectorBase.getResult());
        assertEquals(timestampStart, logCollectorBase.getTimestampStart());
        assertEquals(timestampEnd, logCollectorBase.getTimestampEnd());
        assertEquals(documentType, logCollectorBase.getDocumentType());
        assertEquals(role, logCollectorBase.getRole());
        assertEquals(fiscalcode, logCollectorBase.getFiscalCode());
        assertEquals(subjApplication, logCollectorBase.getOpSubjApplication());
        assertEquals(ProcessedStatusEnum.UNPROCESSED.getCode(), logCollectorBase.getProcessed());
        assertEquals(workFlowIstanceId, logCollectorBase.getWorkflowInstanceId());
    }

    @Test
    public void testSetters() {
        String id = "123";
        String logType = "control-structured-log";
        String message = "Validation CDA completed for workflow instance Id 2.16.840.1.113883.2.9.2.120.4.4.46a41df0ab0514f11c0811056832c3225e06c8e11824f27c7e5517ca5cfc57fe.e0177e0824^^^^urn:ihe:iti:xdw:2013:workflowInstanceId";
        String operation = "VAL-CDA2";
        String result = "OK";
        String documentType = "Referto di Laboratorio";
        String role = "AAS";
        String fiscalcode = "string2";
        String microserviceName = "gtw-dispatcher";
        Date timestampStart = new Date();
        Date timestampEnd = new Date();

        IssuerDTO opIssuer = new IssuerDTO();
        opIssuer.setRawValue("integrity:S1#190201234567XX");
        opIssuer.setRegion("190");

        LocalityDTO opLocality = new LocalityDTO();
        opLocality.setRawValue("201123456");
        opLocality.setAslCode("201");
        opLocality.setStructure("123456");

        SubjApplicationDTO subjApplication = new SubjApplicationDTO();
        subjApplication.setSubject_application_version("R2");
        subjApplication.setSubject_application_vendor("GATEWAY");
        subjApplication.setSubject_application_version("GTW");
        LogCollectorBase logCollectorBase = new LogCollectorBase();
        logCollectorBase.setId(id);
        logCollectorBase.setLogType(logType);
        logCollectorBase.setMessage(message);
        logCollectorBase.setOperation(operation);
        logCollectorBase.setResult(result);
        logCollectorBase.setTimestampStart(timestampStart);
        logCollectorBase.setTimestampEnd(timestampEnd);
        logCollectorBase.setDocumentType(documentType);
        logCollectorBase.setRole(role);
        logCollectorBase.setFiscalCode(fiscalcode);
        logCollectorBase.setMicroserviceName(microserviceName);
        logCollectorBase.setOpSubjApplication(subjApplication);

        assertEquals(id, logCollectorBase.getId());
        assertEquals(logType, logCollectorBase.getLogType());
        assertEquals(message, logCollectorBase.getMessage());
        assertEquals(operation, logCollectorBase.getOperation());
        assertEquals(result, logCollectorBase.getResult());
        assertEquals(timestampStart, logCollectorBase.getTimestampStart());
        assertEquals(timestampEnd, logCollectorBase.getTimestampEnd());
        assertEquals(documentType, logCollectorBase.getDocumentType());
        assertEquals(role, logCollectorBase.getRole());
        assertEquals(fiscalcode, logCollectorBase.getFiscalCode());
        assertEquals(microserviceName, logCollectorBase.getMicroserviceName());
        assertEquals(subjApplication, logCollectorBase.getOpSubjApplication());

    }

}