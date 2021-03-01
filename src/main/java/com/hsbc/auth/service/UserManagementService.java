package com.hsbc.auth.service;

import com.hsbc.auth.exception.AuthException;
import com.hsbc.auth.mo.Users;
import com.hsbc.auth.vo.User;

import java.util.Set;

public interface UserManagementService {

    void createUser(String userName, String password) throws AuthException;

    void deleteUser(User user) throws AuthException;

    Users getUserByName(String name);

    Users getUserByNameAndPassword(String name, String password) throws AuthException;

    Users getAnonymousUser();

    Set<Users> getUsersStore();
}
