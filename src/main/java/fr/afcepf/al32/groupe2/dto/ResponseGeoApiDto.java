package fr.afcepf.al32.groupe2.dto;

public class ResponseGeoApiDto {
	private String source;
	private String status;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ResponseGeoApiDto(String source, String status) {
		super();
		this.source = source;
		this.status = status;
	}

	public ResponseGeoApiDto() {

	}

}
