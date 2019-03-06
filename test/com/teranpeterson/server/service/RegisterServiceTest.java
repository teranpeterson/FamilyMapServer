package com.teranpeterson.server.service;

import com.teranpeterson.server.request.RegisterRequest;
import com.teranpeterson.server.result.RegisterResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RegisterServiceTest {
    private RegisterRequest request;

    @Before
    public void setUp() throws Exception {
        request = new RegisterRequest("sams", "music", "sam@gmail.com", "Sam", "Smith", "m");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void register() throws Exception {
        RegisterService service = new RegisterService();
        RegisterResult result = service.register(request);
        if (result.isSuccess()) {
            System.out.println(result.getAuthToken());
        }
        else {
            System.out.println(result.getMessage());
        }
    }
}