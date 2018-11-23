package fr.afcepf.al32.groupe2.restController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.afcepf.al32.groupe2.service.IRechercheCommerceService;
import fr.afcepf.al32.groupe2.ws.dto.ResponseGeoApiDto;
import fr.afcepf.al32.groupe2.ws.dto.ResponseWsDto;

@RestController // composant spring de type controller de WS REST
@RequestMapping(value = "/rest/rechercheGeo", headers = "Accept=application/json")
public class RechercheCommerceCtrl {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	private static final String STATUS_FALSE = "ZERO_RESULTS";

	@Autowired
	private IRechercheCommerceService rechercheCommerceService;

	@GetMapping("/localisation")
	public ResponseGeoApiDto getValidationAndresse(@RequestParam("source") String source) {
		return rechercheCommerceService.verifierVraiAdresse(source);
	}

	@GetMapping("/commerce")
	public ResponseWsDto getCommerce(@RequestParam("source") String source,
			@RequestParam("perimetre") Integer perimetre) {

		ResponseWsDto responseWsDto = null;

		try {
			responseWsDto = rechercheCommerceService.rechercherShopsByPerimetreEtDepart(source, perimetre);
			responseWsDto.setSource(source);
			responseWsDto.setStatus("OK");
		} catch (Exception e) {
			log.info(e.getMessage());
		}

		return responseWsDto;

	}
}
