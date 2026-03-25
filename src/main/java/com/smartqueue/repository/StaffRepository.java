package com.smartqueue.repository;

import com.smartqueue.model.Role;
import com.smartqueue.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<Staff> findBySpecialty(String specialty);
    List<Staff> findByRole(Role role);
}
