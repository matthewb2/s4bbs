package com.example.board.service;

import com.example.board.config.JwtTokenProvider;
import com.example.board.dto.UserDto;
import com.example.board.entity.User;
import com.example.board.global.CustomException;
import com.example.board.global.ErrorCode;
import com.example.board.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

    @Transactional
    public UserDto.KakaoLoginResponse login(UserDto.GoogleLoginRequest request) {
        String googleAccessToken = getGoogleAccessToken(request.getCode(), request.getRedirectUri());
        JsonNode googleUserInfo = getGoogleUserInfo(googleAccessToken);

        String googleId = googleUserInfo.has("sub") ? googleUserInfo.get("sub").asText() : null;
        String email = googleUserInfo.has("email") ? googleUserInfo.get("email").asText() : null;
        String name = googleUserInfo.has("name") ? googleUserInfo.get("name").asText() : null;
        String profileImage = googleUserInfo.has("picture") ? googleUserInfo.get("picture").asText() : null;

        User user = userRepository.findByLoginTypeAndEmail("google", googleId).orElse(null);
        boolean isNew = false;

        if (user == null) {
            isNew = true;
            user = User.builder()
                    .type("user")
                    .email(email != null ? email : googleId + "@google.com")
                    .password("google_" + System.currentTimeMillis())
                    .name(name != null ? name : "구글 사용자")
                    .image(profileImage)
                    .loginType("google")
                    .build();

            if (request.getUser() != null) {
                try {
                    user.setExtra(objectMapper.writeValueAsString(request.getUser()));
                } catch (JsonProcessingException e) {
                    throw new CustomException(ErrorCode.INTERNAL_ERROR);
                }
            } else {
                try {
                    Map<String, Object> extra = objectMapper.convertValue(googleUserInfo, Map.class);
                    extra.put("providerAccountId", googleId);
                    user.setExtra(objectMapper.writeValueAsString(extra));
                } catch (JsonProcessingException e) {
                    throw new CustomException(ErrorCode.INTERNAL_ERROR);
                }
            }

            user = userRepository.save(user);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getType());
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        UserDto.UserResponseWithToken userResponse = UserDto.UserResponseWithToken.fromEntity(user);
        userResponse.setToken(UserDto.Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());

        return UserDto.KakaoLoginResponse.builder()
                .ok(1)
                .isNew(isNew)
                .item(userResponse)
                .build();
    }

    private String getGoogleAccessToken(String code, String redirectUri) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = rt.exchange(TOKEN_URL, HttpMethod.POST, request, String.class);

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private JsonNode getGoogleUserInfo(String accessToken) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = rt.exchange(USER_INFO_URL, HttpMethod.GET, request, String.class);

        try {
            return objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }
}