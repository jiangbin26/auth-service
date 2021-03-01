package com.hsbc.auth.service;

import com.hsbc.auth.exception.AuthException;
import com.hsbc.auth.mo.Roles;
import com.hsbc.auth.mo.Token;
import com.hsbc.auth.mo.Users;
import com.hsbc.auth.vo.Role;
import com.hsbc.auth.vo.User;

import java.util.Map;
import java.util.Set;

public interface AuthManagementService {

    void grantRoleToUser(Role role, User user) throws AuthException;

    String authenticate(String userName, String password) throws AuthException;

    String authenticateAnonymous() throws AuthException;

    void invaildateToken(String token) throws AuthException;;

    boolean permissionCheckForRole(String token, Role role) throws AuthException;

    Set<Roles> fetchRoles(String token) throws AuthException;

    Map<String, Token> getAuthStore();
}
