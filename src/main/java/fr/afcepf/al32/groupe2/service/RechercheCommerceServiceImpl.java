package fr.afcepf.al32.groupe2.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.afcepf.al32.groupe2.dao.IRechercheCommerceDao;
import fr.afcepf.al32.groupe2.dto.GoogleResponseDto;
import fr.afcepf.al32.groupe2.entity.Shop;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Transactional
@Component
public class RechercheCommerceServiceImpl implements IRechercheCommerceService {
	@Autowired
	private IRechercheCommerceDao commerceDao;

	private static final String API_KEY = "AIzaSyBLj5im_PRa7KdEpQJq-W7akDn4k-m5tfI";
	private static final String PIPE = "%7C";
	OkHttpClient client = new OkHttpClient();

	private Collection<Shop> findShops() {
		return commerceDao.findAll();
	}

	private Collection<String> listeAdressesDeCommerces() {
		Collection<String> addresses = new ArrayList();
		Iterator<Shop> tousShops = (Iterator<Shop>) commerceDao.findAll();
		if (tousShops != null) {
			while (tousShops.hasNext()) {
				Shop shop = tousShops.next();
				if (shop.getAddress() != null && shop.getAddress().getCity() != null) {
					StringBuilder sb = new StringBuilder();
					sb.append(shop.getAddress().getNumber());
					sb.append(StringUtils.SPACE);
					sb.append(shop.getAddress().getName());
					sb.append(StringUtils.SPACE);
					sb.append(shop.getAddress().getCity().getName());
					addresses.add(sb.toString());
				}
			}
		}
		return addresses;

	}

	@Override
	public Collection<Shop> rechercherShopsByPerimetreEtDepart(String source, String perimetre) throws IOException {
		Collection<String> addresses = listeAdressesDeCommerces();

		String response = demanderDistanceGoogleApi(source, addresses);
		ObjectMapper mapper = new ObjectMapper();
		Collection<Shop> shopsFiltres = null;
		GoogleResponseDto googleResponseDto = null;
		if (response != null) {
			googleResponseDto = mapper.readValue(response, GoogleResponseDto.class);

			Map<Shop, String> map = extractJsonFromRequest(googleResponseDto);
			shopsFiltres = filtrerShopDansPerimetre(map, perimetre);
		} else {
			shopsFiltres = findShops();
		}

		return shopsFiltres;
	}

	private String demanderDistanceGoogleApi(String source, Collection<String> destinations) throws IOException {
		String stringResponse = null;
		StringJoiner joiner = new StringJoiner(PIPE);
		for (String destination : destinations) {
			joiner.add(destination);
		}
		String strDestinations = joiner.toString();

		Request request = null;
		Response response = null;
		if (null != source && source.isEmpty()) {
			String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + source + "&destinations="
					+ strDestinations + "&key=" + API_KEY;
			request = new Request.Builder().url(url).build();
			response = client.newCall(request).execute();
		}

		if (response != null) {
			stringResponse = response.body().string();
		}
		return stringResponse;
	}

	private Map<Shop, String> extractJsonFromRequest(GoogleResponseDto response) {

		String distanceTaken = response.getRows().get(0).getElements().get(0).getDistance().getValue();
		String[] distance = distanceTaken.split(StringUtils.SPACE);

		Map<Shop, String> map = new HashMap<>();
		Collection<Shop> tousShops = findShops();
		int i = 0;
		for (Shop shop : tousShops) {
			map.put(shop, distance[i]);
			i++;
		}
		return map;
	}

	private Collection<Shop> filtrerShopDansPerimetre(Map<Shop, String> map, String perimetre) {
		Collection<Shop> shopsFiltres = null;
		Iterator<Entry<Shop, String>> it = map.entrySet().iterator();
		// Stream<Map.Entry<Shop,String>> sorted =
		// map.entrySet().stream().sorted(Map.Entry.comparingByValue());

		while (it.hasNext()) {
			Map.Entry<Shop, String> pair = (Map.Entry<Shop, String>) it.next();
			shopsFiltres.add(pair.getKey());
		}

		return shopsFiltres;
	}

}
