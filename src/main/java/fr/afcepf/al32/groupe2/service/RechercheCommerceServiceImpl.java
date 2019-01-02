package fr.afcepf.al32.groupe2.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.afcepf.al32.groupe2.dao.IRechercheCommerceDao;
import fr.afcepf.al32.groupe2.entity.Shop;
import fr.afcepf.al32.groupe2.ws.dto.Element;
import fr.afcepf.al32.groupe2.ws.dto.GoogleResponseDto;
import fr.afcepf.al32.groupe2.ws.dto.ResponseGeoApiDto;
import fr.afcepf.al32.groupe2.ws.dto.ResponseWsDto;
import fr.afcepf.al32.groupe2.ws.dto.ShopDto;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Transactional
@Component
public class RechercheCommerceServiceImpl implements IRechercheCommerceService {

	@Autowired
	private IRechercheCommerceDao commerceDao;

	private static final String API_KEY = "AIzaSyCOgjLuPdzJDCJW78z26UuL8k3vhF5OsHs";
	private static final String PIPE = "%7C";
	private static final String SPACE = "%20";
	OkHttpClient client = new OkHttpClient();

	private Collection<Shop> findShops() {
		return commerceDao.findAllShops();
	}

	@Override
	public ResponseWsDto shopToResponseWsDtoConverter(String source, Integer perimetre) {
		Collection<Shop> tousShops = findShops();
		List<ShopDto> list = new ArrayList<>();
		for (Shop shop : tousShops) {
			list.add(new ShopDto(shop.getId()));
		}
		return new ResponseWsDto(list, source, perimetre, null);
	}

	private Collection<String> listeAdressesDeCommerces() {
		Collection<String> addresses = new ArrayList();
		Iterator<Shop> tousShops = commerceDao.findAllShops().iterator();
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
	public ResponseWsDto rechercherShopsByPerimetreEtDepart(String source, Integer perimetre) throws IOException {
		Collection<String> addresses = listeAdressesDeCommerces();
		ResponseWsDto responseWsDto = new ResponseWsDto();
		String response = demanderDistanceGoogleApi(source, addresses);
		ObjectMapper mapper = new ObjectMapper();
		GoogleResponseDto googleResponseDto = null;

		googleResponseDto = mapper.readValue(response, GoogleResponseDto.class);
		Map<ShopDto, Integer> map = extractJsonFromRequest(googleResponseDto);
		if (!map.isEmpty()) {
			responseWsDto = filtrerShopDansPerimetre(map, perimetre);
		}

		return responseWsDto;
	}

	private String demanderDistanceGoogleApi(String source, Collection<String> destinations) throws IOException {
		StringJoiner joiner = new StringJoiner(PIPE);
		for (String destination : destinations) {
			joiner.add(destination);
		}
		String strDestinations = joiner.toString();

		Request request = null;
		Response response = null;
		if (null != source && !source.isEmpty()) {
			String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + source + "&destinations="
					+ strDestinations + "&key=" + API_KEY;
			request = new Request.Builder().url(url).build();
			response = client.newCall(request).execute();
		}

		return response.body().string();
	}

	@Override
	public ResponseGeoApiDto verifierVraiAdresse(String source) {
		ResponseGeoApiDto responseGeoApiDto = new ResponseGeoApiDto();
		String url = null;
		try {
			String sourceModifiee = source.replaceAll(StringUtils.SPACE, SPACE);

			if (source != null && !source.isEmpty()) {
				url = "https://maps.googleapis.com/maps/api/geocode/json?address="
						+ URLEncoder.encode(sourceModifiee, "UTF-8") + "&key=" + URLEncoder.encode(API_KEY, "UTF-8");
			}

			URL urlObj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");

			int responseCode = con.getResponseCode();	

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Read JSON response and print
			responseJsonGeoApi(responseGeoApiDto, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseGeoApiDto;
	}

	private void responseJsonGeoApi(ResponseGeoApiDto responseGeoApiDto, StringBuffer response) {
		JSONObject myResponse = new JSONObject(response.toString());
		// String status = myResponse.get("status")
		JSONArray listeResultats = ((JSONArray) myResponse.get("results"));
		if (listeResultats.isEmpty()) {
			responseGeoApiDto.setStatus("ZERO_RESULTS");
		} else {
			double laditude = ((JSONArray) myResponse.get("results")).getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("location").getDouble("lat");
			double longitude = ((JSONArray) myResponse.get("results")).getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("location").getDouble("lng");
			String formattedAddress = ((JSONArray) myResponse.get("results")).getJSONObject(0)
					.getString("formatted_address");
			String coordinates = (laditude + "," + longitude);
			System.out.println(coordinates);
			String statut = myResponse.get("status").toString();
			responseGeoApiDto.setStatus(statut);
			responseGeoApiDto.setSource(formattedAddress);
		}

	}

	private Map<ShopDto, Integer> extractJsonFromRequest(GoogleResponseDto response) {

		List<Element> elementList = response.getRows().get(0).getElements();

		Map<ShopDto, Integer> map = new HashMap<>();
		Collection<Shop> tousShops = findShops();
		int i = 0;
		for (Shop shop : tousShops) {
			if (elementList.get(i).getDistance() != null) {
				map.put(new ShopDto(shop.getId()), elementList.get(i).getDistance().getValue());
			}
			i++;
		}
		return map;
	}

	private ResponseWsDto filtrerShopDansPerimetre(Map<ShopDto, Integer> map, Integer perimetre) {
		ArrayList<ShopDto> listeshop = new ArrayList<>();
		perimetre *= 1000;
		Iterator<Map.Entry<ShopDto, Integer>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<ShopDto, Integer> mentry = iterator.next();
			if (mentry.getValue() <= perimetre) {
				listeshop.add(mentry.getKey());
			}
		}
		return new ResponseWsDto(listeshop, null, perimetre, null);
	}

}
