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
public class KakaoService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Value("${kakao.client-id}")
    private String clientId;

    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    @Transactional
    public UserDto.KakaoLoginResponse login(UserDto.KakaoLoginRequest request) {
        String kakaoAccessToken = getKakaoAccessToken(request.getCode(), request.getRedirectUri());
        JsonNode kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken);

        String kakaoId = String.valueOf(kakaoUserInfo.get("id").asLong());
        String email = null;
        String nickname = null;
        String profileImage = null;

        if (kakaoUserInfo.has("kakao_account")) {
            JsonNode kakaoAccount = kakaoUserInfo.get("kakao_account");
            if (kakaoAccount.has("email")) {
                email = kakaoAccount.get("email").asText();
            }
            if (kakaoAccount.has("profile")) {
                JsonNode profile = kakaoAccount.get("profile");
                if (profile.has("nickname")) {
                    nickname = profile.get("nickname").asText();
                }
                if (profile.has("profile_image_url")) {
                    profileImage = profile.get("profile_image_url").asText();
                }
            }
        }

        User user = userRepository.findByLoginTypeAndEmail("kakao", kakaoId).orElse(null);
        boolean isNew = false;

        if (user == null) {
            isNew = true;
            user = User.builder()
                    .type("user")
                    .email(email != null ? email : kakaoId + "@kakao.com")
                    .password("kakao_" + System.currentTimeMillis())
                    .name(nickname != null ? nickname : "카카오 사용자")
                    .image(profileImage)
                    .loginType("kakao")
                    .build();

            if (request.getUser() != null) {
                try {
                    user.setExtra(objectMapper.writeValueAsString(request.getUser()));
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

    private String getKakaoAccessToken(String code, String redirectUri) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
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

    private JsonNode getKakaoUserInfo(String accessToken) {
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