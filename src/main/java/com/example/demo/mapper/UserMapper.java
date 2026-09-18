package com.example.demo.mapper;

import com.example.demo.dto.UserResponse;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserResponse toUserResponse(User user);
}