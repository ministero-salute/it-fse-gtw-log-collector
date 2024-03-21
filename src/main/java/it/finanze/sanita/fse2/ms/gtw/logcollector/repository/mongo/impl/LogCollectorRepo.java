package it.finanze.sanita.fse2.ms.gtw.logcollector.repository.mongo.impl;

import java.util.ArrayList;
import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.logcollector.enums.ProcessedStatusEnum;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import it.finanze.sanita.fse2.ms.gtw.logcollector.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.logcollector.config.LogCfg;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorBase;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.mongo.IlogCollectorRepo;

@Repository
public class LogCollectorRepo implements IlogCollectorRepo {


	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private LogCfg chunkCfg;

	@Override
	public void save(LogCollectorBase ety) {
		mongoTemplate.save(ety);
	}

	@Override
	public long countUnprocessedLogs(Class clazz) {
		Query query = new Query(Criteria.where(Constants.Mongo.Fields.PROCESSED).is(ProcessedStatusEnum.UNPROCESSED.getCode())
						.andOperator(Criteria.where(Constants.Mongo.Fields.WORKFLOW_INSTANCE_ID).ne(Constants.Mongo.Query.UNKNOWN_WORKFLOW_ID)));
		return mongoTemplate.count(query, clazz);
	}

	@Override
	public List<? extends LogCollectorBase> findUnprocessedLog(Class<? extends LogCollectorBase> clazz){
		Query query = new Query(Criteria.where(Constants.Mongo.Fields.PROCESSED).is(ProcessedStatusEnum.UNPROCESSED.getCode())
				.andOperator(Criteria.where(Constants.Mongo.Fields.WORKFLOW_INSTANCE_ID).ne(Constants.Mongo.Query.UNKNOWN_WORKFLOW_ID)));
		query.with(Sort.by(Constants.Mongo.Fields.OP_TIMESTAMP_START).ascending());
		return mongoTemplate.find(query, clazz);
	}

	@Override
	public int update(List<String> objectIds,Class clazz) {
		List<ObjectId> objectIdList = new ArrayList<>();
		for (String id : objectIds) {
			objectIdList.add(new ObjectId(id));
		}

		Query query = new Query(Criteria.where(Constants.Mongo.Fields.ID).in(objectIdList));
		Update update = Update.update(Constants.Mongo.Fields.PROCESSED, ProcessedStatusEnum.PROCESSING.getCode());
		UpdateResult updatedRecords = mongoTemplate.updateMulti(query, update, clazz);
		return (int)updatedRecords.getModifiedCount();
	}
}
