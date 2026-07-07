package service;

import dao.InternshipDAO;
import model.Internship;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class APIService {

    // 🔑 Replace with your actual RapidAPI key
    private static final String API_KEY  = "YOUR_RAPIDAPI_KEY_HERE";
    private static final String API_HOST = "jsearch.p.rapidapi.com";

    public int fetchAndStoreInternships(String skill) {
        int stored = 0;

        try {
            String query = URLEncoder.encode(skill + " internship India", StandardCharsets.UTF_8);
            String urlStr = "https://jsearch.p.rapidapi.com/search?query=" 
                            + query + "&num_pages=1&date_posted=month";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-RapidAPI-Key", API_KEY);
            conn.setRequestProperty("X-RapidAPI-Host", API_HOST);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int httpCode = conn.getResponseCode();

            // ❌ Handle API errors properly
            if (httpCode != 200) {
                System.out.println("❌ API Error: HTTP " + httpCode);
                return 0;
            }

            // ✅ Read response
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            conn.disconnect();

            // ✅ Parse JSON
            JSONObject json = new JSONObject(response.toString());
            JSONArray data = json.optJSONArray("data");

            if (data == null || data.length() == 0) {
                System.out.println("⚠️ No internships found from API.");
                return 0;
            }

            InternshipDAO dao = new InternshipDAO();

            // ✅ Loop through jobs
            for (int i = 0; i < data.length(); i++) {
                JSONObject job = data.getJSONObject(i);

                Internship intern = new Internship();

                // Safe parsing
                String company = job.optString("employer_name", "Unknown");
                String role = job.optString("job_title", "Internship");

                String city = job.optString("job_city", "");
                String country = job.optString("job_country", "");
                String location = (city + " " + country).trim();

                String applyLink = job.optString("job_apply_link", "");

                intern.setCompany(company);
                intern.setRole(role);
                intern.setLocation(location.isEmpty() ? "Remote" : location);
                intern.setStipend(0); // API doesn't provide stipend
                intern.setRequiredSkills(skill);
                intern.setSource("API");
                intern.setJobUrl(applyLink);

                try {
                    dao.addInternship(intern);
                    stored++;
                } catch (Exception dbError) {
                    System.out.println("⚠️ DB Insert Failed: " + dbError.getMessage());
                }
            }

            System.out.println("✅ Stored " + stored + " internships from API.");

        } catch (Exception e) {
            System.out.println("❌ Error fetching internships: " + e.getMessage());
        }

        return stored;
    }
}