package org.kiennguyenfpt.datingapp.services.impl;

import org.kiennguyenfpt.datingapp.repositories.RoleRepository;
import org.kiennguyenfpt.datingapp.services.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
}
