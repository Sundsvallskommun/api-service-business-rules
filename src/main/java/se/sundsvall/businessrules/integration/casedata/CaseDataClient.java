package se.sundsvall.businessrules.integration.casedata;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.businessrules.integration.casedata.configuration.CaseDataConfiguration.CLIENT_ID;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import generated.se.sundsvall.casedata.GetParkingPermitDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.businessrules.integration.casedata.configuration.CaseDataConfiguration;

@CircuitBreaker(name = "casedata")
@FeignClient(name = CLIENT_ID, url = "${integration.casedata.url}", configuration = CaseDataConfiguration.class)
public interface CaseDataClient {

	/**
	 * Get all issued parking permits for a person.
	 *
	 * @param  personId                             of person to collect issued parking permits for
	 * @return                                      list of of <code>GetParkingPermitDTO</code> representing all issued
	 *                                              parking permits for person matching sent in personId.
	 * @throws org.zalando.problem.ThrowableProblem on error
	 */
	@GetMapping(path = "/parking-permits", produces = APPLICATION_JSON_VALUE)
	List<GetParkingPermitDTO> getParkingPermits(@RequestParam(name = "personId") String personId);
}
