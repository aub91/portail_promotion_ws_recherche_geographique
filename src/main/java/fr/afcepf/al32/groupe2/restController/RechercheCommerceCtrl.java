package fr.afcepf.al32.groupe2.restController;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.afcepf.al32.groupe2.entity.Shop;
import fr.afcepf.al32.groupe2.service.IRechercheCommerceService;

@RestController // composant spring de type controller de WS REST
@RequestMapping(value = "/rest/rechercheGeo", headers = "Accept=application/json")
public class RechercheCommerceCtrl {

	@Autowired
	private IRechercheCommerceService rechercheCommerceService;

	@GetMapping("")
	public Collection<Shop> getCommerce(@RequestParam("source") String source,
			@RequestParam("perimetre") String perimetre) {

		Collection<Shop> listeInitiale = new ArrayList();
		try {
			rechercheCommerceService.rechercherShopsByPerimetreEtDepart(source, perimetre);
		} catch (Exception e) {
			System.out.println("Exception Occurred");
		} finally {

		}

		return listeInitiale;

	}
}
