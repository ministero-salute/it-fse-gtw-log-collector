package it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.finanze.sanita.fse2.ms.gtw.logcollector.config.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = "#{@logCollectorKpiBean}")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
@Data
public class LogCollectorKpiETY extends LogCollectorBase {

    @Field(name = Constants.Mongo.Fields.IS_SSN)
    @JsonProperty(Constants.Mongo.Fields.IS_SSN)
    private boolean isSsn;

    @Field(name = Constants.Mongo.Fields.REGISTRY)
    @JsonProperty(Constants.Mongo.Fields.REGISTRY)
    private String registry;

    @Field(name = Constants.Mongo.Fields.REGION)
    @JsonProperty(Constants.Mongo.Fields.REGION)
    private String region;

    @Field(name = Constants.Mongo.Fields.COMPANY)
    @JsonProperty(Constants.Mongo.Fields.COMPANY)
    private String company;

    @Field(name = Constants.Mongo.Fields.STRUCTURE)
    @JsonProperty(Constants.Mongo.Fields.STRUCTURE)
    private String structure;

}
