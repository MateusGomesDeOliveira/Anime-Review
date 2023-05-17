package mateus.AnimeReview.controller;

import mateus.AnimeReview.model.UserInput;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Controller
public class LoadController {
    @RequestMapping("/")
    public String load(Model model) throws URISyntaxException, IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream("C:\\Users\\Mateus\\Desktop\\COMMITS\\Anime-Review\\src\\main\\resources\\api_key.properties"));

        List<Integer> ids;
        List<String> imagesUrls;
        List<String> titles;

        URI uri = new URI("https://api.myanimelist.net/v2/anime/ranking?ranking_type=airing&limit=4");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(uri).header("X-MAL-CLIENT-ID",prop.getProperty("api_key")).build();

        if(client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join().statusCode() == 200){
            ids = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(this::parseIds)
                    .join();

            imagesUrls = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(this::parseImgs)
                    .join();

            titles = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(this::parseTitles)
                    .join();

            model.addAttribute("id1", "https://myanimelist.net/anime/"+ids.get(0));
            model.addAttribute("id2", "https://myanimelist.net/anime/"+ids.get(1));
            model.addAttribute("id3", "https://myanimelist.net/anime/"+ids.get(2));
            model.addAttribute("id4", "https://myanimelist.net/anime/"+ids.get(3));

            model.addAttribute("img1", imagesUrls.get(0));
            model.addAttribute("img2", imagesUrls.get(1));
            model.addAttribute("img3", imagesUrls.get(2));
            model.addAttribute("img4", imagesUrls.get(3));

            model.addAttribute("alt1", titles.get(0));
            model.addAttribute("alt2", titles.get(1));
            model.addAttribute("alt3", titles.get(2));
            model.addAttribute("alt4", titles.get(3));

            model.addAttribute("status", "ONLINE");
            model.addAttribute("statusColor", "color:green");
        } else {
            model.addAttribute("status", "OFFLINE");
            model.addAttribute("statusColor", "color:red");
        }

        model.addAttribute("userInput", new UserInput());

        return "homepage";
    }
    public List<Integer> parseIds(String responseBody){
        List<Integer> ids = new ArrayList<>();
        for (int i = 0;i < 4;i++){
            JSONObject jsonObject = new JSONObject(responseBody).getJSONArray("data")
                    .getJSONObject(i).getJSONObject("node");
            ids.add(jsonObject.getInt("id"));
        }
        return ids;
    }
    public List<String> parseImgs(String responseBody){
        List<String> urls = new ArrayList<>();
        for (int i = 0;i < 4;i++){
            JSONObject jsonObject = new JSONObject(responseBody).getJSONArray("data")
                    .getJSONObject(i).getJSONObject("node").getJSONObject("main_picture");
            urls.add(jsonObject.getString("medium"));
        }
        return urls;
    }
    public List<String> parseTitles(String responseBody){
        List<String> titles = new ArrayList<>();
        for (int i = 0;i < 4;i++){
            JSONObject jsonObject = new JSONObject(responseBody).getJSONArray("data")
                    .getJSONObject(i).getJSONObject("node");
            titles.add(jsonObject.getString("title"));
        }
        return titles;
    }
}