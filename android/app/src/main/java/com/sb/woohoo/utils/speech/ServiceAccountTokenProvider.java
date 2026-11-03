package com.sb.woohoo.utils.speech;

import android.content.Context;
import android.util.Base64;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sb.woohoo.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServiceAccountTokenProvider {

    private static final String OAUTH_SCOPE = "https://www.googleapis.com/auth/cloud-platform";
    private static final long TOKEN_REFRESH_MARGIN_MILLIS = TimeUnit.MINUTES.toMillis(5);

    private final Context context;
    private final OkHttpClient okHttpClient;
    private volatile AccessToken cachedToken;

    public ServiceAccountTokenProvider(Context context) {
        this.context = context.getApplicationContext();
        this.okHttpClient = new OkHttpClient();
    }

    public synchronized String getAccessToken() throws IOException {
        if (cachedToken != null && cachedToken.isValid()) {
            return cachedToken.value;
        }

        JsonObject credential = readCredential();
        String assertion = createJwtAssertion(credential);
        String tokenUri = credential.has("token_uri")
                ? credential.get("token_uri").getAsString()
                : "https://oauth2.googleapis.com/token";

        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                .add("assertion", assertion)
                .build();

        Request request = new Request.Builder()
                .url(tokenUri)
                .post(requestBody)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("Token request failed with code " + response.code());
            }

            String body = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            String accessToken = jsonObject.get("access_token").getAsString();
            long expiresIn = jsonObject.has("expires_in")
                    ? jsonObject.get("expires_in").getAsLong()
                    : TimeUnit.HOURS.toSeconds(1);
            long expiresAtMillis = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expiresIn);
            cachedToken = new AccessToken(accessToken, expiresAtMillis);
            return accessToken;
        }
    }

    public synchronized String getProjectId() throws IOException {
        JsonObject credential = readCredential();
        if (!credential.has("project_id")) {
            throw new IOException("Service account credential is missing project_id");
        }
        return credential.get("project_id").getAsString();
    }

    private JsonObject readCredential() throws IOException {
        try (InputStream inputStream = context.getResources().openRawResource(R.raw.credential);
             BufferedReader bufferedReader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            return JsonParser.parseString(builder.toString()).getAsJsonObject();
        }
    }

    private String createJwtAssertion(JsonObject credential) throws IOException {
        String clientEmail = credential.get("client_email").getAsString();
        String privateKeyPem = credential.get("private_key").getAsString();
        String tokenUri = credential.has("token_uri")
                ? credential.get("token_uri").getAsString()
                : "https://oauth2.googleapis.com/token";

        long issuedAt = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        long expiresAt = issuedAt + TimeUnit.HOURS.toSeconds(1);

        String headerJson = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";
        String claimJson = "{\"iss\":\"" + clientEmail + "\","
                + "\"scope\":\"" + OAUTH_SCOPE + "\","
                + "\"aud\":\"" + tokenUri + "\","
                + "\"iat\":" + issuedAt + ","
                + "\"exp\":" + expiresAt + "}";

        String encodedHeader = encodeUrlSafe(headerJson.getBytes(StandardCharsets.UTF_8));
        String encodedClaim = encodeUrlSafe(claimJson.getBytes(StandardCharsets.UTF_8));
        String signingInput = encodedHeader + "." + encodedClaim;

        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(parsePrivateKey(privateKeyPem));
            signature.update(signingInput.getBytes(StandardCharsets.UTF_8));
            String encodedSignature = encodeUrlSafe(signature.sign());
            return signingInput + "." + encodedSignature;
        } catch (Exception e) {
            throw new IOException("Unable to sign OAuth assertion", e);
        }
    }

    private PrivateKey parsePrivateKey(String privateKeyPem) throws Exception {
        String normalizedKey = privateKeyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decodedKey = Base64.decode(normalizedKey, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    private String encodeUrlSafe(byte[] data) {
        return Base64.encodeToString(data, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
    }

    private static final class AccessToken {
        private final String value;
        private final long expiresAtMillis;

        private AccessToken(String value, long expiresAtMillis) {
            this.value = value;
            this.expiresAtMillis = expiresAtMillis;
        }

        private boolean isValid() {
            return System.currentTimeMillis() + TOKEN_REFRESH_MARGIN_MILLIS < expiresAtMillis;
        }
    }
}
