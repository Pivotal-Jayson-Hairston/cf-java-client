/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.uaa.users;

import org.junit.Test;

public final class CreateUserRequestTest {

    @Test(expected = IllegalStateException.class)
    public void incompleteName() {
        CreateUserRequest.builder()
            .name(Name.builder()
                .familyName("test-familyName")
                .build())
            .password("test-password")
            .userName("test-userName")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noName() {
        CreateUserRequest.builder()
            .password("test-password")
            .userName("test-userName")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noPassword() {
        CreateUserRequest.builder()
            .name(Name.builder()
                .familyName("test-familyName")
                .givenName("test-givenName")
                .build())
            .userName("test-userName")
            .build();
    }

    @Test(expected = IllegalStateException.class)
    public void noUserName() {
        CreateUserRequest.builder()
            .name(Name.builder()
                .familyName("test-familyName")
                .givenName("test-givenName")
                .build())
            .password("test-password")
            .build();
    }

    @Test
    public void valid() {
        CreateUserRequest.builder()
            .name(Name.builder()
                .familyName("test-familyName")
                .givenName("test-givenName")
                .build())
            .password("test-password")
            .userName("test-userName")
            .build();
    }

}
