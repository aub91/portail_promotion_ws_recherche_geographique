package fr.afcepf.al32.groupe2.dto;

import java.util.List;

public class ResponseWsDto {

	private List<ShopDto> listDtos;
	private String source;
	private Integer perimetre;
	private String status;

	public List<ShopDto> getListDtos() {
		return listDtos;
	}

	public void setListDtos(List<ShopDto> listDtos) {
		this.listDtos = listDtos;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getPerimetre() {
		return perimetre;
	}

	public void setPerimetre(Integer perimetre) {
		this.perimetre = perimetre;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ResponseWsDto(List<ShopDto> listDtos, String source, Integer perimetre, String status) {
		super();
		this.listDtos = listDtos;
		this.source = source;
		this.perimetre = perimetre;
		this.status = status;
	}

	public ResponseWsDto() {
	}

}
