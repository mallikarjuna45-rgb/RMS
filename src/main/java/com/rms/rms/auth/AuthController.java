package com.rms.rms.auth;
import com.rms.rms.dto.UserResponseDto;
import com.rms.rms.model.User;
import com.rms.rms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody User requestUser) {
        User user = userService.createUser(requestUser);

        UserResponseDto response = UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .address(user.getAddress())
                .userType(user.getUserType().name())
                .profileHeadline(user.getProfileHeadline())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()
                )
        );

        String jwt = jwtUtils.generateJwtToken(authentication);
        UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();

        JwtResponse jwtResponse = JwtResponse.builder()
                .token(jwt)
                .user(String.valueOf(userPrincipal.getUserId()))
                .build();

        return ResponseEntity.ok(jwtResponse);
    }
}
