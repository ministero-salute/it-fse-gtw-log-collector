package it.finanze.sanita.fse2.ms.gtw.logcollector.service;

import java.text.ParseException;

import org.bson.Document;


public interface ILogCollectorSrv {

     void saveLogEvent(final String json);

     Document buildDocumentToSave(String json) throws ParseException;


}
