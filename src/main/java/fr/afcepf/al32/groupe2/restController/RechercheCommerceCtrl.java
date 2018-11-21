package fr.afcepf.al32.groupe2.restController;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.afcepf.al32.groupe2.dto.ShopDto;
import fr.afcepf.al32.groupe2.entity.Shop;
import fr.afcepf.al32.groupe2.service.IRechercheCommerceService;

@RestController // composant spring de type controller de WS REST
@RequestMapping(value = "/rest/rechercheGeo", headers = "Accept=application/json")
public class RechercheCommerceCtrl {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private IRechercheCommerceService rechercheCommerceService;

	@GetMapping("")
	public Collection<ShopDto> getCommerce(@RequestParam("source") String source,
			@RequestParam("perimetre") Integer perimetre) {

		Collection<ShopDto> listeCommerce = null;
		try {
			listeCommerce = rechercheCommerceService.rechercherShopsByPerimetreEtDepart(source, perimetre);
		} catch (Exception e) {
			log.info(e.getMessage());
		} finally {

		}

		return listeCommerce;

	}
}
