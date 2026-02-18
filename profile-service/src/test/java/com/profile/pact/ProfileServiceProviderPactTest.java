package com.profile.pact;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.profile.entity.ProfileEntity;
import com.profile.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("ProfileService")
@PactFolder("pacts")
public class ProfileServiceProviderPactTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ProfileRepository profileRepository;

    @BeforeEach
    void setup(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("profile exists for user 123")
    public void profileExistsForUser123() {
        profileRepository.deleteAll();
        ProfileEntity profile = new ProfileEntity();
        profile.setUserId("123");
        profile.setAvatar("https://example.com/avatar1.jpg");
        profile.setTheme("dark");
        profile.setLanguage("pt-BR");
        profileRepository.save(profile);
    }

    @State("profile does not exist for user 999")
    public void profileDoesNotExistForUser999() {
        profileRepository.deleteAll();
    }

    @State("profile service is available")
    public void profileServiceIsAvailable() {
        profileRepository.deleteAll();
    }
}
