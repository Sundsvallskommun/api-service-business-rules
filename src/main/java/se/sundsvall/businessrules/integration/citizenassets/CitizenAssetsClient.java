package se.sundsvall.businessrules.integration.citizenassets;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.businessrules.integration.citizenassets.configuration.CitizenAssetsConfiguration.CLIENT_ID;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

import generated.se.sundsvall.citizenassets.Asset;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.businessrules.integration.citizenassets.configuration.CitizenAssetsConfiguration;

@CircuitBreaker(name = "citizenassets")
@FeignClient(name = CLIENT_ID, url = "${integration.citizenassets.url}", configuration = CitizenAssetsConfiguration.class)
public interface CitizenAssetsClient {

	String PARTY_ID_PARAMETER = "personId";
	String ASSET_ID_PARAMETER = "assetId";
	String TYPE_PARAMETER = "type";
	String STATUS_PARAMETER = "status";

	/**
	 * Search assets by parameter.
	 *
	 * @param  parameters                           a Map of parameters to narrow the result.
	 * @return                                      list of of <code>GetParkingPermitDTO</code> representing the assets.
	 * @throws org.zalando.problem.ThrowableProblem on error
	 */
	@GetMapping(path = "/assets", produces = APPLICATION_JSON_VALUE)
	List<Asset> getAssets(@SpringQueryMap Map<String, String> parameters);
}
