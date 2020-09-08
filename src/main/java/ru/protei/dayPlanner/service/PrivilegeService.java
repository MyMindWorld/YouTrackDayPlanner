package ru.protei.dayPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.dayPlanner.model.Privilege;
import ru.protei.dayPlanner.repository.PrivilegeRepository;
import ru.protei.dayPlanner.repository.RoleRepository;
import ru.protei.dayPlanner.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PrivilegeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;


    @Transactional
    public Privilege createPrivilegeIfNotFound(String name) {

        Privilege privilege = privilegeRepository.findByNameEquals(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    public List<Privilege> returnAllPrivileges() {
        return privilegeRepository.findAll();
    }
}
