package com.example.demo.mapper;

import com.example.demo.dto.PermissionResponse;
import com.example.demo.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    PermissionResponse toPermissionResponse(Permission permission);
}