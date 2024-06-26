package se.sundsvall.businessrules.integration.partyassets;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.businessrules.integration.partyassets.configuration.PartyAssetsConfiguration.CLIENT_ID;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import generated.se.sundsvall.partyassets.Asset;
import se.sundsvall.businessrules.integration.partyassets.configuration.PartyAssetsConfiguration;

@FeignClient(name = CLIENT_ID, url = "${integration.partyassets.url}", configuration = PartyAssetsConfiguration.class)
public interface PartyAssetsClient {

	String PARTY_ID_PARAMETER = "partyId";
	String TYPE_PARAMETER = "type";
	String STATUS_PARAMETER = "status";
	String STATUS_REASON_PARAMETER = "statusReason";

	/**
	 * Search assets by parameter.
	 *
	 * @param  municipalityId                       the municipalityId to use.
	 * @param  parameters                           a Map of parameters to narrow the result.
	 * @return                                      list of of <code>GetParkingPermitDTO</code> representing the assets.
	 * @throws org.zalando.problem.ThrowableProblem on error
	 */
	@GetMapping(path = "/{municipalityId}/assets", produces = APPLICATION_JSON_VALUE)
	List<Asset> getAssets(@PathVariable("municipalityId") String municipalityId, @SpringQueryMap Map<String, String> parameters);
}
