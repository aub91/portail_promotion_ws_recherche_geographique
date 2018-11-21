package fr.afcepf.al32.groupe2.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import fr.afcepf.al32.groupe2.dao.IRechercheCommerceDao;
import fr.afcepf.al32.groupe2.entity.Shop;

@Component
@Transactional
public class RechercheCommerceDaoImpl implements IRechercheCommerceDao {
	@PersistenceContext
	private EntityManager entityManager;

	private final String ALL_COMMERCES = "SELECT s FROM Shop s";

	@SuppressWarnings("unchecked")
	@Override
	public List<Shop> findAllShops() {
		return entityManager.createQuery(ALL_COMMERCES, Shop.class).getResultList();
	}

}
