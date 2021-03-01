package com.hsbc.auth.mo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;

@Data
@Builder
public class Users implements Serializable {
    private String name;
    private String password;
    private Set<Roles> rolesList = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return name.equals(users.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
