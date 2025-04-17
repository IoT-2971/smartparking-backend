package com.smartparking.backend.v1.iam.domain.services;

import com.smartparking.backend.v1.iam.domain.model.entities.Role;
import com.smartparking.backend.v1.iam.domain.model.queries.GetAllRolesQuery;
import com.smartparking.backend.v1.iam.domain.model.queries.GetRoleByNameQuery;

import java.util.List;
import java.util.Optional;

public interface RoleQueryService {
    List<Role> handle(GetAllRolesQuery query);
    Optional<Role> handle(GetRoleByNameQuery query);
}
