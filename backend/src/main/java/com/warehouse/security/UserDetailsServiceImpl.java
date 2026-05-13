package com.warehouse.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.warehouse.entity.Role;
import com.warehouse.entity.User;
import com.warehouse.entity.UserRole;
import com.warehouse.mapper.RoleMapper;
import com.warehouse.mapper.UserMapper;
import com.warehouse.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        List<Long> roleIds = userRoleMapper.selectList(
                        new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, user.getId()))
                .stream().map(UserRole::getRoleId).toList();

        List<String> roles = List.of();
        if (!roleIds.isEmpty()) {
            roles = roleMapper.selectBatchIds(roleIds).stream()
                    .map(Role::getRoleCode)
                    .toList();
        }

        return new LoginUser(user.getId(), user.getUsername(), user.getPassword(),
                user.getStatus(), roles);
    }
}
