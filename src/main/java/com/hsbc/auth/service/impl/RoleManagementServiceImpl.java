package com.hsbc.auth.service.impl;

import com.hsbc.auth.exception.AuthException;
import com.hsbc.auth.mo.Roles;
import com.hsbc.auth.mo.Users;
import com.hsbc.auth.service.RoleManagementService;
import com.hsbc.auth.service.UserManagementService;
import com.hsbc.auth.vo.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


@Service
@Slf4j
public class RoleManagementServiceImpl implements RoleManagementService {

    @Autowired
    UserManagementService userManagementService;

    private Set<Roles> rolesStore;

    @PostConstruct
    private void init(){
        rolesStore = new HashSet<>();
    }

    @Override
    public void createRole(Role role) throws AuthException {
        if (role == null || StringUtils.isEmpty(role.getRoleName())) throw new AuthException("Please specify the role name.");
        String roleName = role.getRoleName();
        for(Roles roles : rolesStore) {
            if(roleName.equals(roles.getName())) {
                throw new AuthException("Role is already existed");
            }
        }
        Roles roles = Roles.builder().name(roleName).build();
        rolesStore.add(roles);
    }

    @Override
    public void deleteRole(Role role) throws AuthException {
        if (role == null || StringUtils.isEmpty(role.getRoleName())) throw new AuthException("Please specify the role name.");
        Iterator<Roles> rolesList = rolesStore.iterator();
        while(rolesList.hasNext()) {
            Roles roles = rolesList.next();
            if(role.getRoleName().equals(roles.getName())) {
                rolesList.remove();
                Set<Users> usersList = userManagementService.getUsersStore();
                for(Users users : usersList) {
                    Set<Roles> rolesSet = users.getRolesList();
                    if(rolesSet != null && rolesSet.contains(roles)) rolesSet.remove(roles);
                }
                return;
            }
        }
        throw new AuthException("Role is not existed");
    }

    public Set<Roles> getRoleStore(){
        return this.rolesStore;
    }
}
