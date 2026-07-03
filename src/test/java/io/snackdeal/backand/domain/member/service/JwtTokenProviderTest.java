package io.snackdeal.backand.domain.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class JwtTokenProviderTest {

    private final JwtTokenProvider jwtTokenProvider =
            new JwtTokenProvider("testsecretkeytestsecretkeytestsecretkeytestsecretkeytestsecretkey");

    @Disabled("TODO: implement")
    @Test
    @DisplayName("issue - TODO")
    void issue_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("issueRefreshToken - TODO")
    void issueRefreshToken_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("validate - TODO")
    void validate_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("getClaims - TODO")
    void getClaims_Success() {
        fail("not implemented");
    }

}