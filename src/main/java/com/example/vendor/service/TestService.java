
package com.example.vendor.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.example.vendor.repository.TestRepository;
import com.example.vendor.dto.TestDTO;
import com.example.vendor.mapper.TestMapper;

@Service
public class TestService {

    private final TestRepository repo;

    public TestService(TestRepository repo) {
        this.repo = repo;
    }

    public TestDTO save(TestDTO dto) {
        return TestMapper.toDTO(repo.save(TestMapper.toEntity(dto)));
    }

    public List<TestDTO> findAll() {
        return repo.findAll().stream().map(TestMapper::toDTO).toList();
    }
}
