package it.finanze.sanita.fse2.ms.gtw.logcollector.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import it.finanze.sanita.fse2.ms.gtw.logcollector.enums.ProcessedStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.gtw.logcollector.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.logcollector.config.LogCfg;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.EsitoDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.IssuerDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.LocalityDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.ResultDto;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.SubjApplicationDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorBase;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorKpiETY;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.mongo.IlogCollectorRepo;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.ILogCollectorSrv;
import it.finanze.sanita.fse2.ms.gtw.logcollector.utility.DateUtility;
import it.finanze.sanita.fse2.ms.gtw.logcollector.utility.JsonUtility;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LogCollectorSrv implements ILogCollectorSrv {

    @Autowired
    private IlogCollectorRepo ilogCollectorRepo;

    @Autowired
    private LogCfg logCfg;

    @Override
    public void saveLogEvent(final String json) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.App.Custom.DATE_PATTERN);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            Document doc = buildDocumentToSave(json);

            LogCollectorBase b = null;
            if (Constants.Mongo.Fields.LOG_TYPE_CONTROL.equals(doc.getString("log_type"))) {
                b = JsonUtility.clone(doc, LogCollectorControlETY.class);
            } else {
                b = JsonUtility.clone(doc, LogCollectorKpiETY.class);
            }

            b.setProcessed(ProcessedStatusEnum.UNPROCESSED.getCode());
            ilogCollectorRepo.save(b);
            log.info("Salvataggio su mongo effettuato");
        } catch (Exception ex) {
            log.error("Error while save event : ", ex);
            throw new BusinessException("Error while save event : ", ex);
        }
    }

    @Override
    public Document buildDocumentToSave(String json) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.App.Custom.DATE_PATTERN);
        Document doc = Document.parse(json);
        String issuer = doc.getString(Constants.Mongo.Fields.OP_ISSUER);
        String locality = doc.getString(Constants.Mongo.Fields.OP_LOCALITY);
        doc.remove(Constants.Mongo.Fields.OP_ISSUER);
        doc.remove(Constants.Mongo.Fields.OP_LOCALITY);

        Date startDate = null;
        if (doc.getString(Constants.Mongo.Fields.OP_TIMESTAMP_START) != null) {
            startDate = simpleDateFormat.parse(doc.getString(Constants.Mongo.Fields.OP_TIMESTAMP_START));
        }

        Date endDate = null;
        if (doc.getString(Constants.Mongo.Fields.OP_TIMESTAMP_END) != null) {
            endDate = simpleDateFormat.parse(doc.getString(Constants.Mongo.Fields.OP_TIMESTAMP_END));
        }
        doc.put(Constants.Mongo.Fields.OP_TIMESTAMP_START, startDate);
        doc.put(Constants.Mongo.Fields.OP_TIMESTAMP_END, endDate);

        if (StringUtils.isNotEmpty(issuer)) {
            IssuerDTO issuerDTO = IssuerDTO.decodeIssuer(issuer);
            doc.put(Constants.Mongo.Fields.OP_ISSUER, Document.parse(JsonUtility.objectToJson(issuerDTO)));
        }

        if (StringUtils.isNotEmpty(locality)) {
            LocalityDTO localityDTO = LocalityDTO.decodeLocality(locality);
            doc.put(Constants.Mongo.Fields.OP_LOCALITY, Document.parse(JsonUtility.objectToJson(localityDTO)));
        }

        String subjApplicationId = doc.getString(Constants.Mongo.Fields.OP_SUBJ_APPLICATION_ID);
        String subjApplicationVendor = doc.getString(Constants.Mongo.Fields.OP_SUBJ_APPLICATION_VENDOR);
        String subjApplicationVersion = doc.getString(Constants.Mongo.Fields.OP_SUBJ_APPLICATION_VERSION);
        doc.remove(Constants.Mongo.Fields.OP_SUBJ_APPLICATION_ID);
        doc.remove(Constants.Mongo.Fields.OP_SUBJ_APPLICATION_VENDOR);
        doc.remove(Constants.Mongo.Fields.OP_SUBJ_APPLICATION_VERSION);

        SubjApplicationDTO subjDTO = new SubjApplicationDTO();
        boolean saveField = false;
        if (StringUtils.isNotEmpty(subjApplicationId)) {
            subjDTO.setSubject_application_id(subjApplicationId);
            saveField = true;
        }

        if (StringUtils.isNotEmpty(subjApplicationVendor)) {
            subjDTO.setSubject_application_vendor(subjApplicationVendor);
            saveField = true;
        }

        if (StringUtils.isNotEmpty(subjApplicationVersion)) {
            subjDTO.setSubject_application_version(subjApplicationVersion);
            saveField = true;
        }
        if (saveField) {
            doc.put(Constants.Mongo.Fields.OP_SUBJ_APPLICATION, Document.parse(JsonUtility.objectToJson(subjDTO)));
        }

        return doc;
    }

}
