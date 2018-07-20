package com.wust.shiro;

import com.wust.entity.Permission;
import com.wust.entity.Role;
import com.wust.entity.User;
import com.wust.service.UserService;
import com.wust.utils.JsonUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Desc :  身份校验核心类
 */
public class MyShiroRealm extends AuthorizingRealm {

	@Autowired
    private UserService userService;

	private static final Logger logger = LoggerFactory.getLogger(MyShiroRealm.class);
    /**
     * Desc :  认证信息(身份验证)
     * Authentication 是用来验证用户身份
     * User : RICK
     * Time : 2017/8/25 11:01
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

	    System.out.println("MyShiroRealm.doGetAuthenticationInfo().AuthenticationToken" + authenticationToken);
	    logger.info("身份验证-->MyShiroRealm.doGetAuthenticationInfo()");

        //获取用户的输入的账号.
        //String phone = (String) authenticationToken.getPrincipal();

	    UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
	    String phone=token.getUsername();
        System.out.println("phone:" + phone);

	    //利用緩存获取用戶信息
	    User user = userService.getUserCache(phone);
	    System.out.println("userCache------>" + user);

	    if(null == user){
		    user = userService.selectByPhone(phone);
		    System.out.println("user------>" + user);

		    //利用緩存存储用戶信息
		    userService.insertUserCache(user);
	    }

	    if(null == user){
		    throw new UnknownAccountException();
	    }

	    if ("0".equals(user.getStatus().toString())) {
		    // 用户被管理员锁定抛出异常
		    throw new LockedAccountException();
	    }

	    int num = Integer.valueOf(userService.getLoginCount("user:" + phone));
	    if (num > 3){
		    throw new DisabledAccountException("由于密码输入错误次数大于3次，帐号已经禁止登录！");
	    }

        //账号判断;
        //加密方式;
        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
		        user,//用户名
		        user.getPwd(),//密码
                ByteSource.Util.bytes(user.getCredentialsSalt()),//salt
                getName()//realm name
        );


        return authenticationInfo;
    }


    /**
     * 此方法调用  hasRole,hasPermission的时候才会进行回调.
     * 权限信息(授权):
     * 1、如果用户正常退出，缓存自动清空；
     * 2、如果用户非正常退出，缓存自动清空；
     * 3、如果我们修改了用户的权限，而用户不退出系统，修改的权限无法立即生效。
     * （需要手动编程进行实现；放在service进行调用）
     * 在权限修改后调用realm中的方法，realm已经由spring管理，所以从spring中获取realm实例，
     * 调用clearCached方法；
     * Authorization 是授权访问控制，用于对用户进行的操作授权，证明该用户是否允许进行当前操作，如访问某个链接，某个资源文件等。
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        /**
         * 缓存名为：com.wust.shiro.MyShiroRealm.authorizationCache
		 * 当没有使用缓存的时候，不断刷新页面的话，这个代码会不断执行，
		 * 当其实没有必要每次都重新设置权限信息，所以我们需要放到缓存中进行管理；
		 * 当放到缓存中时，这样的话，doGetAuthorizationInfo就只会执行一次了，
		 * 缓存过期之后会再次执行。
		 */
        System.out.println("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");

	    //为当前用户设置角色和权限
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

	    Object key = principals.getPrimaryPrincipal();
	    System.out.println("++++"+key.toString());
	    //User user = (User) principals.getPrimaryPrincipal();

	    User user = new User();
	    try {
		    BeanUtils.copyProperties(user, key);
	    } catch (Exception e) {
		    System.out.println("++++User转换失败");
		    e.printStackTrace();
	    }

	    if (user == null) {
		    return null;
	    }

	    //获取该用户角色列表
	    List<Role> rolesList = new ArrayList();

	    for (int i=0; i< user.getRoleList().size(); i++){
	    	Role currRole = new Role();

		    try {
			    BeanUtils.copyProperties(currRole,user.getRoleList().get(i));
			    rolesList.add(currRole);

		    } catch (Exception e) {
			    System.out.println("++++Role转换失败");
			    e.printStackTrace();
		    }
	    }
	    System.out.println("++++rolesList" + rolesList);

	    //获取该用户所有权限列表
	    List<Permission> permissionsList = new ArrayList();

	    for (Role role: rolesList) {

		    for (int j=0; j< role.getPermissionList().size(); j++){
			    Permission currPermission = new Permission();

			    try {
				    BeanUtils.copyProperties(currPermission,role.getPermissionList().get(j));
				    permissionsList.add(currPermission);

			    } catch (Exception e) {
				    System.out.println("++++Permission转换失败");
				    e.printStackTrace();
			    }
		    }

	    }
	    for (Permission permission:permissionsList) {
		    if(permissionsList.contains(permission)){
			    //同一用户可能有多重角色,不同角色可能有相同权限
			    //判断相同权限是否重新添加
		    }else{
			    permissionsList.add(permission);
		    }
	    }
	    System.out.println("permissionsList------>" + permissionsList);

	    //对用户进行角色赋值
        for (Role role : rolesList) {
            authorizationInfo.addRole(role.getRoleName());
        }
        //设置权限信息
        authorizationInfo.addStringPermissions(getStringPermissions(permissionsList));

	    //authorizationInfo.addRole("admin");
	    //authorizationInfo.addStringPermission("list");
        return authorizationInfo;
    }

    /**
     * 将权限对象中的 权限name取出.
     * @param permissions
     * @return
     */
     public Set<String> getStringPermissions(List<Permission> permissions){
     	Set<String> stringPermissions = new HashSet<String>();
		 if(permissions != null){
		 	for(Permission p : permissions) {
		       stringPermissions.add(p.getPermissionName());
		 	}
		 }
	     return stringPermissions;
	}


}
