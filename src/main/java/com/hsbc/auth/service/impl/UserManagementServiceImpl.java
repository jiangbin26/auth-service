package com.hsbc.auth.service.impl;

import com.hsbc.auth.exception.AuthException;
import com.hsbc.auth.mo.Roles;
import com.hsbc.auth.mo.Token;
import com.hsbc.auth.mo.Users;
import com.hsbc.auth.service.AuthManagementService;
import com.hsbc.auth.service.UserManagementService;
import com.hsbc.auth.utils.AuthUtils;
import com.hsbc.auth.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;


@Service
public class UserManagementServiceImpl implements UserManagementService {
    @Value("${salt}")
    private String salt;

    @Autowired
    AuthManagementService authManagementService;

    private Set<Users> usersStore;

    @PostConstruct
    private void init(){
        usersStore = new HashSet<>();
    }

    @Override
    public void createUser(String userName, String password) throws AuthException {
        for(Users users : usersStore) {
            if(userName.equals(users.getName())) {
                throw new AuthException("User is already existed");
            }
        }

        Users user = Users.builder().name(userName).password(AuthUtils.encode(salt, password)).build();
        usersStore.add(user);
    }

    @Override
    public void deleteUser(User user) throws AuthException {
        if (user == null || StringUtils.isEmpty(user.getUserName())) throw new AuthException("Please specify the user name.");
        Iterator<Users> usersList = usersStore.iterator();
        while(usersList.hasNext()) {
            Users users = usersList.next();
            if(user.getUserName().equals(users.getName())) {
                usersList.remove();
                Map<String, Token> tokenMap = authManagementService.getAuthStore();
                for(Token token : tokenMap.values()) {
                    Users usersFromToken = token.getUsers();
                    if(users.getName().equals(usersFromToken.getName())) {
                        authManagementService.invaildateToken(token.getInfo());
                    }
                }
                return;
            }
        }
        throw new AuthException("User is not existed");
    }

    @Override
    public Users getUserByName(String name) {
        if (StringUtils.isEmpty(name)) return null;
        Optional<Users> usersOp = usersStore.stream().filter(users -> name.equals(users.getName())).findAny();
        if (!usersOp.isPresent()) return null;
        return usersOp.get();
    }

    @Override
    public Users getUserByNameAndPassword(String name, String password) throws AuthException {
        if(StringUtils.isEmpty(name)) throw new AuthException("Please specify the user name.");
        if(StringUtils.isEmpty(password)) throw new AuthException("Please specify the password.");

        return usersStore.stream().filter(users -> {
            try {
                return name.equals(users.getName()) && password.equals(AuthUtils.decode(salt, users.getPassword()));
            } catch (AuthException e) {
                e.printStackTrace();
            }
            return false;
        }).findAny().get();
    }

    @Override
    public Users getAnonymousUser() {
        for(Users users : usersStore) {
            if ("ANONYMOUS".equals(users.getName())){
                return users;
            }
        }
        Roles roles = Roles.builder().name("GUEST").build();
        Set<Roles> rolesList = new HashSet<>();
        rolesList.add(roles);
        Users users = Users.builder().name("ANONYMOUS").rolesList(rolesList).build();
        usersStore.add(users);
        return users;
    }

    @Override
    public Set<Users> getUsersStore(){
        return this.usersStore;
    }
}
