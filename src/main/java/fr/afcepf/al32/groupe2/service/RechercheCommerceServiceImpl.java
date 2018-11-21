package fr.afcepf.al32.groupe2.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.StringJoiner;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.afcepf.al32.groupe2.dao.IRechercheCommerceDao;
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

	@Override
	public Collection<String> listeAdressesDeCommerces() {
		Collection<String> addresses = new ArrayList();
		Iterator<Shop> tousShops = (Iterator<Shop>) commerceDao.findAll();
		if (tousShops != null) {
			while (tousShops.hasNext()) {
				Shop shop = tousShops.next();
				if (shop.getAddress() != null) {
					addresses.add(shop.getAddress().getNumber() + StringUtils.SPACE + shop.getAddress().getName());
				}
			}
		}
		return addresses;

	}

	@Override
	public Collection<Shop> rechercherShopsByPerimetreEtDepart(String source, String perimetre) throws IOException {
		Collection<Shop> tousShops = findShops();
		Collection<String> addresses = listeAdressesDeCommerces();

		String response = demanderDistanceGoogleApi(source, addresses);

		return null;
	}

	private String demanderDistanceGoogleApi(String source, Collection<String> destinations) throws IOException {

		StringJoiner joiner = new StringJoiner(PIPE);
		for (String destination : destinations) {
			joiner.add(destination);
		}
		String strDestinations = joiner.toString();

		String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + source + "&destinations="
				+ strDestinations + "&key=" + API_KEY;
		Request request = new Request.Builder().url(url).build();

		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	private Map<Shop, String> recupererResponseJson(Map response) throws IOException {
		Map<Shop, String> map = null;
		
		
		
//		JSONParser parser = new JSONParser();
//		try {
//
//			Object obj = parser.parse(response);
//			JSONObject jsonobj = (JSONObject) obj;
//
//			JSONArray dist = (JSONArray) jsonobj.get("rows");
//			JSONObject obj2 = (JSONObject) dist.get(0);
//			JSONArray disting = (JSONArray) obj2.get("elements");
//			JSONObject obj3 = (JSONObject) disting.get(0);
//			JSONObject obj4 = (JSONObject) obj3.get("distance");
//			String distance = (String) obj4.get("value");
//			
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return map;

	}

}
