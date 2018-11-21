package fr.afcepf.al32.groupe2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Element {
	private TextValue distance;

	public void setDistance(TextValue distance) {
		this.distance = distance;
	}

	public TextValue getDistance() {
		return distance;
	}

}
