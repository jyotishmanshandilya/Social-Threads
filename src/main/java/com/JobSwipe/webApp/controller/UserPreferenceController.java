package com.JobSwipe.webApp.controller;

import com.JobSwipe.webApp.entities.UserPreference;
import com.JobSwipe.webApp.service.UserPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/preferences")
@RequiredArgsConstructor
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;

    @PostMapping
    public UserPreference create(@RequestBody UserPreference pref) {
        return userPreferenceService.create(pref);
    }

//    @PutMapping("/{id}")
//    public UserPreference update(@PathVariable UUID id, @RequestBody UserPreference pref) {
//        return userPreferenceService.update(id, pref);
//    }
//
//    @DeleteMapping("/{id}")
//    public void delete(@PathVariable UUID id) {
//        userPreferenceService.delete(id);
//    }
}
