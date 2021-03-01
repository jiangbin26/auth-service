package com.hsbc.auth.service.impl;

import com.hsbc.auth.exception.AuthException;
import com.hsbc.auth.mo.Roles;
import com.hsbc.auth.mo.Token;
import com.hsbc.auth.mo.Users;
import com.hsbc.auth.service.AuthManagementService;
import com.hsbc.auth.service.UserManagementService;
import com.hsbc.auth.utils.AuthUtils;
import com.hsbc.auth.vo.Role;
import com.hsbc.auth.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
public class AuthManagementServiceImpl implements AuthManagementService {
    @Value("${expiryHours}")
    private int expiryHours;

    private Map<String, Token> tokenMap;

    @PostConstruct
    private void init(){
        tokenMap = new HashMap<>();
    }

    @Autowired
    UserManagementService userManagementService;

    @Override
    public void grantRoleToUser(Role role, User user) throws AuthException {
        String userName = user.getUserName();
        String roleName = role.getRoleName();
        if(user == null || StringUtils.isEmpty(userName)) throw new AuthException("Please specify the user.");
        if(role == null || StringUtils.isEmpty(roleName)) throw new AuthException("Please specify the role.");
        Users users = userManagementService.getUserByName(userName);
        if (users == null) throw new AuthException("User is not existed.");
        Set<Roles> rolesList = users.getRolesList();
        if (rolesList == null) {
            rolesList = new HashSet<>();
            users.setRolesList(rolesList);
        } else {
            for (Roles roles : rolesList) {
                if (roleName.equals(roles.getName())) {
                    throw new AuthException("The role is already associated to the user.");
                }
            }
        }
        Roles roles = Roles.builder().name(role.getRoleName()).build();
        rolesList.add(roles);
        userManagementService.getUsersStore().add(users);
    }

    @Override
    public String authenticate(String userName, String password) throws AuthException {
        Users users = userManagementService.getUserByNameAndPassword(userName, password);
        if(users == null) throw new AuthException("User Name or Password is not correct");
        String tokenInfo = AuthUtils.generateAuthToken();
        Token token = Token.builder().info(tokenInfo).users(users).expiryTime(AuthUtils.getTokenExpiryTime(expiryHours)).build();
        tokenMap.put(tokenInfo, token);
        return tokenInfo;
    }

    @Override
    public String authenticateAnonymous() throws AuthException {
        String tokenInfo = AuthUtils.generateAuthToken();
        Users anonymous = userManagementService.getAnonymousUser();
        Token token = Token.builder().info(tokenInfo).users(anonymous).expiryTime(AuthUtils.getTokenExpiryTime(expiryHours)).build();
        tokenMap.put(tokenInfo, token);
        return tokenInfo;
    }

    @Override
    public void invaildateToken(String token) throws AuthException {
        if(StringUtils.isEmpty(token)) throw new AuthException("Please specify the token you want to invalidate.");
        tokenMap.remove(token);
    }

    @Override
    public boolean permissionCheckForRole(String token, Role role) throws AuthException {
        if(StringUtils.isEmpty(token)) throw new AuthException("Please specify the token.");
        if(role == null || StringUtils.isEmpty(role.getRoleName())) throw new AuthException("Please specify the role.");
        Token tokenObj = tokenMap.get(token);
        if(tokenObj == null) throw new AuthException("The token is invalid, pls login in.");
        boolean isTokenExpired = AuthUtils.isTokenExpired(tokenObj.getExpiryTime());
        if(isTokenExpired) throw new AuthException("The token is expired, pls login in.");
        Set<Roles> rolesList = tokenObj.getUsers().getRolesList();
        for(Roles roles: rolesList){
            if (role.getRoleName().equals(roles.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<Roles> fetchRoles(String token) throws AuthException {
        if(StringUtils.isEmpty(token)) throw new AuthException("Please specify the token.");
        Token tokenObj = tokenMap.get(token);
        if(tokenObj == null) throw new AuthException("The token is invalid, pls login in.");
        return tokenObj.getUsers().getRolesList();
    }

    @Override
    public Map<String, Token> getAuthStore() {
        return this.tokenMap;
    }
}
