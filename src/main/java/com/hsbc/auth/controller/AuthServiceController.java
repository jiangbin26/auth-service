package com.hsbc.auth.controller;

import com.hsbc.auth.exception.AuthException;
import com.hsbc.auth.mo.Roles;
import com.hsbc.auth.service.AuthManagementService;
import com.hsbc.auth.service.RoleManagementService;
import com.hsbc.auth.service.UserManagementService;
import com.hsbc.auth.vo.Role;
import com.hsbc.auth.vo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequestMapping("/auth")
@Api(description = "Authentication And Authorization controller")
public class AuthServiceController {
    @Autowired
    UserManagementService userManagementService;

    @Autowired
    RoleManagementService roleManagementService;

    @Autowired
    AuthManagementService authManagementService;

    @PostMapping(value = "/user/create")
    @ApiOperation(value = "create user", notes = "create an user")
    public void createUser(@RequestParam(required = true) String name, @RequestParam(required = true) String password) {
        try {
            userManagementService.createUser(name, password);
        } catch (AuthException exception) {
            throw new ResponseStatusException(
                    HttpStatus.EXPECTATION_FAILED, exception.getMessage(), null);
        }
    }

    @PostMapping(value = "/user/delete")
    @ApiOperation(value = "delete user", notes = "delete an user")
    public void deleteUser(@RequestBody(required = true) User user) {
        try {
            userManagementService.deleteUser(user);
        } catch (AuthException exception) {
            throw new ResponseStatusException(
                    HttpStatus.EXPECTATION_FAILED, exception.getMessage(), null);
        }
    }

    @PostMapping(value = "/role/create")
    @ApiOperation(value = "create role", notes = "create a role")
    public void createRole(@RequestBody(required = true) Role role) {
        try {
            roleManagementService.createRole(role);
        } catch (AuthException exception) {
            throw new ResponseStatusException(
                    HttpStatus.EXPECTATION_FAILED, exception.getMessage(), null);
        }
    }

    @PostMapping(value = "/role/delete")
    @ApiOperation(value = "delete role", notes = "delete a role")
    public void deleteRole(@RequestBody(required = true) Role role) {
        try {
            roleManagementService.deleteRole(role);
        } catch (AuthException exception) {
            throw new ResponseStatusException(
                    HttpStatus.EXPECTATION_FAILED, exception.getMessage(), null);
        }
    }

    @PostMapping(value = "/permission")
    @ApiOperation(value = "add role to user", notes = "add role to user")
    public void addRoleToUser(@RequestParam(required = true) String userName, @RequestParam(required = true) String roleName) {
        try {
            Role role = new Role();
            role.setRoleName(roleName);
            User user = new User();
            user.setUserName(userName);
            authManagementService.grantRoleToUser(role, user);
        } catch (AuthException exception) {
            throw new ResponseStatusException(
                    HttpStatus.EXPECTATION_FAILED, exception.getMessage(), null);
        }
    }

    @GetMapping(value = "/token")
    @ApiOperation(value = "generate auth token based on user name and password", notes = "generate auth token based on user name and password")
    public String generateAuthToken(@RequestParam(required = true) String userName, @RequestParam(required = true) String password) {
        try {
            String token = authManagementService.authenticate(userName, password);
            return token;
        } catch (AuthException exception) {
            throw new ResponseStatusException(
                    HttpStatus.EXPECTATION_FAILED, exception.getMessage(), null);
        }
    }

    @GetMapping(value = "/token/anonymous")
    @ApiOperation(value = "generate auth token for anonymous user", notes = "generate auth token for anonymous user")
    public String generateAuthTokenForAnonymousUser() {
        try {
            String token = authManagementService.authenticateAnonymous();
            return token;
        } catch (AuthException exception) {
            throw new ResponseStatusException(
                    HttpStatus.EXPECTATION_FAILED, exception.getMessage(), null);
        }
    }

    @PostMapping(value = "/token/invalidate")
    @ApiOperation(value = "invalidate auth token", notes = "invalidate auth token")
    public void invalidateAuthToken(@RequestParam(required = true) String token) {
        try {
            authManagementService.invaildateToken(token);
        } catch (AuthException exception) {
            throw new ResponseStatusException(
                    HttpStatus.EXPECTATION_FAILED, exception.getMessage(), null);
        }
    }

    @PostMapping(value = "/role/token")
    @ApiOperation(value = "check if the role supported based on given token", notes = "check if the role supported based on given token")
    public boolean invalidateAuthToken(@RequestParam(required = true) String token, @RequestBody(required = true) Role role) {
        try {
           return authManagementService.permissionCheckForRole(token, role);
        } catch (AuthException exception) {
            throw new ResponseStatusException(
                    HttpStatus.EXPECTATION_FAILED, exception.getMessage(), null);
        }
    }

    @GetMapping(value = "/roles/token")
    @ApiOperation(value = "get all the roles based on given token", notes = "check if the role supported based on given token")
    public Set<Roles> getAllRolesBasedOnToken(@RequestParam(required = true) String token) {
        try {
            return authManagementService.fetchRoles(token);
        } catch (AuthException exception) {
            throw new ResponseStatusException(
                    HttpStatus.EXPECTATION_FAILED, exception.getMessage(), null);
        }
    }
}
