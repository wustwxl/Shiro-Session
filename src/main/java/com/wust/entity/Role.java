package com.wust.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer roleId;

    private String roleName;

    // 一个角色对应多个权限
    private List<Permission> permissionList;

    /**
     * 获得权限名：add/update等
     * @return
     */
    public List<String> getPermissionName() {
        List<String> list = new ArrayList<String>();
        List<Permission> perlist = getPermissionList();
        for (Permission per : perlist) {
            list.add(per.getPermissionName());
        }
        return list;
    }
}