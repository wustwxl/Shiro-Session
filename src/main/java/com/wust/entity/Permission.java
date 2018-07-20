package com.wust.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;


@Slf4j
@Data
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer permissionId;

    private String permissionName;


}