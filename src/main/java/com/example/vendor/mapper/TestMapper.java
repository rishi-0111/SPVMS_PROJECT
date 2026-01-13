
package com.example.vendor.mapper;

import com.example.vendor.dto.TestDTO;
import com.example.vendor.entity.TestEntity;

public class TestMapper {

    public static TestDTO toDTO(TestEntity e) {
        TestDTO d = new TestDTO();
        d.setId(e.getId());
        d.setName(e.getName());
        return d;
    }

    public static TestEntity toEntity(TestDTO d) {
        TestEntity e = new TestEntity();
        e.setId(d.getId());
        e.setName(d.getName());
        return e;
    }
}
