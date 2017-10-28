/**
 * 
 */
package com.sacknibbles.sch.request.validator;

import java.io.IOException;

import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.springframework.stereotype.Service;

import com.sacknibbles.sch.avro.model.JobRequestRecord;
import com.sacknibbles.sch.avro.model.SchedulerResponse.Builder;

/**
 * @author Sachin
 *
 */
@Service
public class JobRequestValidator {

	private final DatumReader<JobRequestRecord> jobReqDatumReader = new SpecificDatumReader<>(JobRequestRecord.class);
	
	public JobRequestRecord jobRequestValidator(String jobRequestPayload, Builder respBuilder){
		JobRequestRecord jobRequest = null;
		try {
			Decoder decoder = DecoderFactory.get().jsonDecoder(JobRequestRecord.SCHEMA$, jobRequestPayload);
			jobRequest = jobReqDatumReader.read(null, decoder);
		} catch (IOException e) {
			respBuilder.setException(e.toString());
		}
		return jobRequest;
	}
}
