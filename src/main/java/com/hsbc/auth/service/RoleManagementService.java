package com.hsbc.auth.service;

import com.hsbc.auth.exception.AuthException;
import com.hsbc.auth.vo.Role;

public interface RoleManagementService {

    void createRole(Role role) throws AuthException;

    void deleteRole(Role role) throws AuthException;
}
