package fr.afcepf.al32.groupe2.service;

import java.io.IOException;
import java.util.Collection;

import fr.afcepf.al32.groupe2.entity.Shop;

public interface IRechercheCommerceService {

	// public Collection<Shop> findShops();

	Collection<Shop> rechercherShopsByPerimetreEtDepart(String source, String perimetre) throws IOException;
}
