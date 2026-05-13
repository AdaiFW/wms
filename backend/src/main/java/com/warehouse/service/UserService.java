package com.warehouse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.dto.UserDto;
import com.warehouse.entity.User;

import java.util.List;

public interface UserService {
    Page<User> page(Integer pageNum, Integer pageSize, String keyword);
    User getById(Long id);
    User create(UserDto dto);
    User update(Long id, UserDto dto);
    void delete(Long id);
    void updatePassword(Long userId, String oldPassword, String newPassword);
    List<String> getUserRoles(Long userId);
}
