package com.college.service.acl.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.college.service.acl.entity.Permission;
import com.college.service.acl.entity.RolePermission;
import com.college.service.acl.entity.User;
import com.college.service.acl.entity.vo.PermissionVo;
import com.college.service.acl.helper.MemuHelper;
import com.college.service.acl.helper.PermissionHelper;
import com.college.service.acl.mapper.PermissionMapper;
import com.college.service.acl.service.PermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.college.service.acl.service.RolePermissionService;
import com.college.service.acl.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.college.service.acl.helper.PermissionHelper.bulid;

/**
 * <p>
 * 权限 服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-12
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private RolePermissionService rolePermissionService;
    @Autowired
    private UserService userService;


    //根据递归查询出三级菜单
    @Override
    public List<PermissionVo> nestedList() {
        //先查询出所有的数据
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        List<Permission> permissionList = baseMapper.selectList(queryWrapper);

        List<PermissionVo> permissionVoList = new ArrayList<>();
        for (Permission permission : permissionList) {
            //需要将查询出来的数据封装到PermissionVo当中
            //注：集合存储的是对象的引用 必须在遍历数据的同时也创建对象 这样才能保证将数据保存到集合当中
            PermissionVo permissionVo = new PermissionVo();
            BeanUtils.copyProperties(permission, permissionVo);
            permissionVoList.add(permissionVo);
        }
        //把查询所有菜单list集合按照要求进行封装
        List<PermissionVo> resultList = this.bulidPermission(permissionVoList);
        return resultList;
    }

    public List<PermissionVo> bulidPermission(List<PermissionVo> permissionVoList) {

        //创建list集合，用于数据最终封装
        List<PermissionVo> finalNode = new ArrayList<>();

        //遍历全部数据permissionVoList 判断出其中的一级菜单
        for (PermissionVo permissionVo : permissionVoList) {
            //一级菜单的条件为 pid=0
            if ("0".equals(permissionVo.getPid())) {
                //将一级菜单的level的属性设置为1
                permissionVo.setLevel(1);
                //根据顶层菜单，向里面进行查询子菜单，封装到finalNode里面  selectChildren是向下查询子菜单的方法
                finalNode.add(selectChildren(permissionVo, permissionVoList));
            }
        }
        return finalNode;
    }

    //permissionVo为一级菜单  permissionVoList所有菜单(优化方式:每次遍历时将已经遍历过的对象删除,避免重复遍历)
    private PermissionVo selectChildren(PermissionVo permissionVo, List<PermissionVo> permissionVoList) {
        //将传递过来的一级菜单的子菜单属性初始化
        permissionVo.setChildren(new ArrayList<PermissionVo>());

        //遍历全部菜单 第一次遍历等同于查找二级菜单(二级菜单的pid等于一级菜单的id)  (优化是遍历只遍历除一级菜单之外的其他菜单)
        for (PermissionVo it : permissionVoList) {
            if (permissionVo.getId().equals(it.getPid())) {
                //把父菜单的level值+1
                int level = permissionVo.getLevel() + 1;
                it.setLevel(level);
                //将查询出来的子菜单放入到父菜单的children属性当中  it第一次遍历代表的就是二级菜单 使用的是递归
                permissionVo.getChildren().add(selectChildren(it, permissionVoList));
            }
        }
        return permissionVo;
    }

    //==========================================================================================

    //使用递归实现删除菜单
    @Override
    public void removeChild(String id) {
        //创建集合 该集合用来保存所需要删除的所有id
        ArrayList<String> idList = new ArrayList<>();
        //首先将指定id添加到集合当中
        idList.add(id);
        this.selectPermissionChildById(id, idList);
        baseMapper.deleteBatchIds(idList);
    }

    private void selectPermissionChildById(String id, List<String> idList) {
        //查询指定id下的下一级菜单id
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid", id).select("id");
        List<Permission> permissionList = baseMapper.selectList(queryWrapper);
        for (Permission permission : permissionList) {
            //得到子菜单的id
            String childrenId = permission.getId();
            idList.add(childrenId);
            //递归调用
            this.selectPermissionChildById(childrenId, idList);
        }
    }

    //===================================================


    @Override
    public void saveRolePermissionRealtionShip(String roleId, String[] permissionId) {
        //roleId角色id
        //permissionId菜单id 数组形式
        //1 创建list集合，用于封装添加数据
        List<RolePermission> rolePermissionList = new ArrayList<>();
        //遍历所有菜单数组
        for (String perId : permissionId) {
            //RolePermission对象
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(perId);
            //封装到list集合
            rolePermissionList.add(rolePermission);
        }
        //添加到角色菜单关系表
        rolePermissionService.saveBatch(rolePermissionList);
    }

//======================================================================================


    //根据用户id获取用户菜单
    @Override
    public List<String> selectPermissionValueByUserId(String userId) {
        List<String> selectPermissionValueList = null;
        if (this.isSysAdmin(userId)) {
            //如果是系统管理员，获取所有权限
            selectPermissionValueList = baseMapper.selectAllPermissionValue();
        } else {
            selectPermissionValueList = baseMapper.selectPermissionValueByUserId(userId);
        }
        return selectPermissionValueList;
    }

    //判断用户是否系统管理员 用户名为admin代表为管理员权限
    private boolean isSysAdmin(String userId) {
        User user = userService.getById(userId);
        if (null != user && "admin".equals(user.getNickName())) {
            return true;
        }
        return false;
    }


    @Override
    public List<JSONObject> selectPermissionByUserId(String userId) {
        List<Permission> selectPermissionList = null;
        if (this.isSysAdmin(userId)) {
            //如果是超级管理员，获取所有菜单
            selectPermissionList = baseMapper.selectList(null);
        } else {
            selectPermissionList = baseMapper.selectPermissionByUserId(userId);
        }
        List<Permission> permissionList = bulid(selectPermissionList);
        List<JSONObject> result = MemuHelper.bulid(permissionList);
        System.out.println("前端传递数据" + result);
        return result;
    }

    @Override
    public List<Permission> queryAllMenu() {
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        List<Permission> permissionList = baseMapper.selectList(wrapper);

        List<Permission> result = bulid(permissionList);

        return result;
    }

    @Override
    public List<Permission> selectAllMenu(String roleId) {
        List<Permission> allPermissionList = baseMapper.selectList(new QueryWrapper<Permission>().orderByAsc("CAST(id AS SIGNED)"));

        //根据角色id获取角色权限
        List<RolePermission> rolePermissionList = rolePermissionService.list(new QueryWrapper<RolePermission>().eq("role_id", roleId));
        //转换给角色id与角色权限对应Map对象
//        List<String> permissionIdList = rolePermissionList.stream().map(e -> e.getPermissionId()).collect(Collectors.toList());
//        allPermissionList.forEach(permission -> {
//            if(permissionIdList.contains(permission.getId())) {
//                permission.setSelect(true);
//            } else {
//                permission.setSelect(false);
//            }
//        });
        for (int i = 0; i < allPermissionList.size(); i++) {
            Permission permission = allPermissionList.get(i);
            for (int m = 0; m < rolePermissionList.size(); m++) {
                RolePermission rolePermission = rolePermissionList.get(m);
                if (rolePermission.getPermissionId().equals(permission.getId())) {
                    permission.setSelect(true);
                }
            }
        }
        List<Permission> permissionList = bulid(allPermissionList);
        return permissionList;
    }

    @Override
    public void removeChildById(String id) {
        List<String> idList = new ArrayList<>();
        this.selectChildListById(id, idList);

        idList.add(id);
        baseMapper.deleteBatchIds(idList);
    }

    /**
     * 递归获取子节点
     *
     * @param id
     * @param idList
     */
    private void selectChildListById(String id, List<String> idList) {
        List<Permission> childList = baseMapper.selectList(new QueryWrapper<Permission>().eq("pid", id).select("id"));
        childList.stream().forEach(item -> {
            idList.add(item.getId());
            this.selectChildListById(item.getId(), idList);
        });
    }
}
