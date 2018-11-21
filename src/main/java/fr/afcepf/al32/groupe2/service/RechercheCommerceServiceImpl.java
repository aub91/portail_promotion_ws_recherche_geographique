package fr.afcepf.al32.groupe2.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.afcepf.al32.groupe2.dao.IRechercheCommerceDao;
import fr.afcepf.al32.groupe2.dto.Element;
import fr.afcepf.al32.groupe2.dto.GoogleResponseDto;
import fr.afcepf.al32.groupe2.dto.ShopDto;
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
		return commerceDao.findAllShops();
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
	public Collection<ShopDto> rechercherShopsByPerimetreEtDepart(String source, Integer perimetre) throws IOException {
		Collection<String> addresses = listeAdressesDeCommerces();

		String response = demanderDistanceGoogleApi(source, addresses);
		ObjectMapper mapper = new ObjectMapper();
		Collection<ShopDto> shopsFiltres = new ArrayList<>();
		GoogleResponseDto googleResponseDto = null;

		googleResponseDto = mapper.readValue(response, GoogleResponseDto.class);
		Map<ShopDto, Integer> map = extractJsonFromRequest(googleResponseDto);
		if (!map.isEmpty()) {
			shopsFiltres = filtrerShopDansPerimetre(map, perimetre);
		} else {
			Collection<Shop> list = findShops();
			for (Shop shop : list) {
				shopsFiltres.add(new ShopDto(shop));
			}
		}

		return shopsFiltres;
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

	private Map<ShopDto, Integer> extractJsonFromRequest(GoogleResponseDto response) {

		List<Element> elementList = response.getRows().get(0).getElements();

		Map<ShopDto, Integer> map = new HashMap<>();
		Collection<Shop> tousShops = findShops();
		int i = 0;
		for (Shop shop : tousShops) {
			if (elementList.get(i).getDistance() != null) {
				map.put(new ShopDto(shop), elementList.get(i).getDistance().getValue());
			}
			i++;
		}
		return map;
	}

	private Collection<ShopDto> filtrerShopDansPerimetre(Map<ShopDto, Integer> map, Integer perimetre) {
		ArrayList<ShopDto> listeshop = new ArrayList<>();
		perimetre *= 1000;
		Iterator<Map.Entry<ShopDto, Integer>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<ShopDto, Integer> mentry = iterator.next();
			if (mentry.getValue() <= perimetre) {
				listeshop.add(mentry.getKey());
			}
		}
		return listeshop;
	}

}
