package fr.afcepf.al32.groupe2.service;

import java.io.IOException;
import java.util.Collection;

import fr.afcepf.al32.groupe2.dto.ShopDto;
import fr.afcepf.al32.groupe2.entity.Shop;

public interface IRechercheCommerceService {

	// public Collection<Shop> findShops();

	Collection<ShopDto> rechercherShopsByPerimetreEtDepart(String source, Integer perimetre) throws IOException;
}
