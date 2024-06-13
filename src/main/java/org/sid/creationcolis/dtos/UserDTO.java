package org.sid.creationcolis.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.sid.creationcolis.enums.Role;
import org.sid.creationcolis.enums.TypeChargeur;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String email;
    private String password;

    private Role role;
}
