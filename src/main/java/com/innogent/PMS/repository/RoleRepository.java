package com.innogent.PMS.repository;

import com.innogent.PMS.entities.Role;
import com.innogent.PMS.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    public Role findByName(RoleName name);
}
