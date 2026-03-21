package com.JobSwipe.webApp.controller;

import com.JobSwipe.webApp.filter.JwtAuthenticationFilter;
import com.JobSwipe.webApp.service.SeedListBulkLoaderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SeedListController.class)
class SeedListControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean SeedListBulkLoaderService seedListBulkLoaderService;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean com.JobSwipe.webApp.service.JwtService jwtService;
    @MockBean com.JobSwipe.webApp.repository.UserConfigRepository userConfigRepository;

    @BeforeEach
    void stubFilter() throws Exception {
        doAnswer(inv -> {
            FilterChain chain = inv.getArgument(2);
            chain.doFilter(inv.getArgument(0), inv.getArgument(1));
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class), any(FilterChain.class));
    }

    @Test
    @WithMockUser
    void loadBulkSeedLists_returns200AndCallsService() throws Exception {
        when(seedListBulkLoaderService.loadSeedListsFromDirectory(anyString(), any()))
                .thenReturn(42);

        String body = objectMapper.writeValueAsString(
                Map.of("path", "/data/seeds", "contentType", "SEED_LIST"));

        mockMvc.perform(post("/api/seedlist/loadBulkSeedLists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(seedListBulkLoaderService).loadSeedListsFromDirectory(eq("/data/seeds"), any());
    }

    @Test
    void loadBulkSeedLists_returns401WhenUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/seedlist/loadBulkSeedLists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"path\":\"/data\",\"contentType\":\"SEED_LIST\"}")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
