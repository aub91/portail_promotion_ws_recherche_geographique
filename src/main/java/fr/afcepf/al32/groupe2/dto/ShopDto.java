package fr.afcepf.al32.groupe2.dto;

import fr.afcepf.al32.groupe2.entity.Shop;

public class ShopDto {

	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ShopDto(Shop shop) {
		super();
		this.id = shop.getId();
	}

}
