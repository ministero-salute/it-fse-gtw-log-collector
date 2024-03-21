package it.finanze.sanita.fse2.ms.gtw.logcollector.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.ResultDto;

public interface IScheduleCTL {

	@GetMapping("/runControlLog")
	@Operation(summary = "Avvia scheduler", description = "Esegue lo scheduler.")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResultDto.class)))
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Scheduler eseguito"),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	ResultDto runControlLogScheduler();
	
	@GetMapping("/runKpiLog")
	@Operation(summary = "Avvia scheduler", description = "Esegue lo scheduler.")
	@ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResultDto.class)))
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Scheduler eseguito"),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	ResultDto runKpiLogScheduler();
	
}
