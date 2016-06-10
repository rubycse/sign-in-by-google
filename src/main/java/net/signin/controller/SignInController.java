package net.signin.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import net.signin.utils.PropertyReader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author lutfun
 * @since 3/28/16
 */

@Controller
public class SignInController {

    @RequestMapping(path = "/signin", method = RequestMethod.GET)
    public String signin(ModelMap model) {
        model.put("gapiClientId", PropertyReader.getProperty("gapi.clientId"));
        return "signin/signin";
    }

    @ResponseBody
    @RequestMapping(path = "/validateSignIn", method = RequestMethod.POST)
    public String validateSignIn(@RequestParam String authCode) throws IOException {
        if (authCode == null || authCode.trim().equals("")) {
            return "ERROR";
        }

        GoogleTokenResponse tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(),
                        JacksonFactory.getDefaultInstance(),
                        "https://www.googleapis.com/oauth2/v4/token",
                        PropertyReader.getProperty("gapi.clientId"),
                        PropertyReader.getProperty("gapi.clientSecret"),
                        authCode,
                        "postmessage")
                        .execute();

        String accessToken = tokenResponse.getAccessToken();

        // Get profile info from ID token
        GoogleIdToken idToken = tokenResponse.parseIdToken();
        GoogleIdToken.Payload payload = idToken.getPayload();
        String userId = payload.getSubject();  // Use this value as a key to identify a user.
        String email = payload.getEmail();
        boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");
        String locale = (String) payload.get("locale");
        String familyName = (String) payload.get("family_name");
        String givenName = (String) payload.get("given_name");

        return "SUCCESS";
    }

    @RequestMapping(path = "/showSignedIn", method = RequestMethod.GET)
    public String showSignedIn() {
        return "signin/showSignedIn";
    }
}
