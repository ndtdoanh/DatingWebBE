package org.kiennguyenfpt.datingapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kiennguyenfpt.datingapp.controllers.ProfileController;
import org.kiennguyenfpt.datingapp.dtos.mapper.UserMapper;
import org.kiennguyenfpt.datingapp.entities.Profile;
import org.kiennguyenfpt.datingapp.entities.User;
import org.kiennguyenfpt.datingapp.security.JwtUtil;
import org.kiennguyenfpt.datingapp.services.AuthService;
import org.kiennguyenfpt.datingapp.services.EmailService;
import org.kiennguyenfpt.datingapp.services.ProfileService;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
public class AuthorControllerTest {
    /*

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthService authService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private UserMapper userMapper;

    @InjectMocks
    private ProfileController profileController;

    private final String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJraWVubmN0cWUxNzAyMDdAZnB0LmVkdS52biIsImV4cCI6MTcyNzkyNTUwOSwiaWF0IjoxNzI3OTIxOTA5fQ.xQEDy6hksEnn8m5YZN3ZBQz5idAYUumkFX7RbydbOhb61WBC0ZNjJg6xnlBb-BrgjFxtG85XJAwaWBOO5EEzJg";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "kiennctqe170207@fpt.edu.vn", roles = {"USER"})
    public void testViewMyProfile() throws Exception {
        // Mock the JWT token validation and extraction
        String email = "kiennctqe170207@fpt.edu.vn";
        when(jwtUtil.extractUsername(jwtToken)).thenReturn(email);

        // Mock the user details service
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(email).password("password").roles("USER").build();
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        // Mock the profile service
        User user = new User();
        user.setEmail(email);
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setName("Kien Nguyen");
        when(profileService.getProfileByEmail(email)).thenReturn(profile);

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/profiles/me")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.name").value("Kien Nguyen"));
    }

     */
}