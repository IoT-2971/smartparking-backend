package com.smartparking.backend.v1.iam.domain.model.commands;






import com.smartparking.backend.v1.iam.domain.model.entities.Role;

import java.util.List;

public record SignUpCommand(String username, String password, List<Role> roles) {
}