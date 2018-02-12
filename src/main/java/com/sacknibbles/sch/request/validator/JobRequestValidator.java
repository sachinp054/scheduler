/**
 * 
 */
package com.sacknibbles.sch.request.validator;

import static com.sacknibbles.sch.avro.model.JobRequestRecord.getClassSchema;

import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.springframework.stereotype.Service;

import com.sacknibbles.sch.avro.model.JobRequestRecord;
import com.sacknibbles.sch.scheduler.exception.BadRequestException;

/**
 * @author Sachin
 * @author Gaurav Rai Mazra {@link https://lineofcode.in}
 *
 */
@Service
public class JobRequestValidator {
	private final DatumReader<JobRequestRecord> JOB_REQUEST_DATUM_READER = new SpecificDatumReader<>(JobRequestRecord.class);

	public JobRequestRecord validateAndGetJobRequestRecord(String jobRequestPayload) throws BadRequestException {
		try {
			Decoder decoder = DecoderFactory.get().jsonDecoder(getClassSchema(), jobRequestPayload);
			return JOB_REQUEST_DATUM_READER.read(null, decoder);
		} catch (Exception e) {
			throw new BadRequestException(e.getLocalizedMessage());
		}
	}
}
