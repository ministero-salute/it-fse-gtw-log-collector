package it.finanze.sanita.fse2.ms.gtw.logcollector.dto;

import lombok.Data;

@Data
public class ResultDto {

	private Integer totalRecord;
	private Integer pageSize;
	private Integer totalPage;
	private Integer processedLog;
	
	
}
