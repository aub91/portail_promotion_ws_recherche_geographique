package fr.afcepf.al32.groupe2.dao;

import java.util.List;

import fr.afcepf.al32.groupe2.entity.Shop;

public interface IRechercheCommerceDao {

	List<Shop> findAllShops();

}
