package com.linearpast.minecraftmanager.utils.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpApiUtils {
	public static String minecraftAccountQuery(String mcName){
		String host = "https://api.minecraftservices.com";
		String path = "/minecraft/profile/lookup/name/" + mcName;
		String method = "GET";
		Map<String, String> querys = new HashMap<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		try {
			HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
			String string = EntityUtils.toString(response.getEntity());
			System.out.println(string);
			JsonObject jsonObject = JsonParser.parseString(string).getAsJsonObject();
			if(Optional.ofNullable(jsonObject).isPresent() && jsonObject.has("id")){
				StringBuilder id = new StringBuilder(jsonObject.get("id").getAsString()).insert(8, "-").insert(13, "-").insert(18, "-").insert(23, "-");
				return id.toString();
			}
		}catch (Exception ignored){}
		return null;
	}

	public static String qqAvatarQuery(String qq){
		String host = "https://q.qlogo.cn";
		String path = "/headimg_dl";
		String method = "GET";
		Map<String, String> querys = new HashMap<>();
		querys.put("dst_uin", qq);
		querys.put("img_type", "jpg");
		querys.put("spec", "640");
		Map<String, String> headers = new HashMap<>();
		try {
			HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
			byte[] pic = EntityUtils.toByteArray(response.getEntity());
			Base64.Encoder encoder = Base64.getEncoder();
			return encoder.encodeToString(pic);
		}catch (Exception ignored){}
		return null;
	}
}
