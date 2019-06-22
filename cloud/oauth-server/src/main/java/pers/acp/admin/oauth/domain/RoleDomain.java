package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.common.constant.RoleCode;
import pers.acp.admin.oauth.base.OauthBaseDomain;
import pers.acp.admin.oauth.entity.Menu;
import pers.acp.admin.oauth.entity.ModuleFunc;
import pers.acp.admin.oauth.entity.Role;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.po.RolePO;
import pers.acp.admin.oauth.repo.MenuRepository;
import pers.acp.admin.oauth.repo.ModuleFuncRepository;
import pers.acp.admin.oauth.repo.RoleRepository;
import pers.acp.admin.oauth.repo.UserRepository;
import pers.acp.admin.oauth.vo.RoleVO;
import pers.acp.spring.boot.exceptions.ServerException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class RoleDomain extends OauthBaseDomain {

    private final RoleRepository roleRepository;

    private final MenuRepository menuRepository;

    private final ModuleFuncRepository moduleFuncRepository;

    @Autowired
    public RoleDomain(UserRepository userRepository, RoleRepository roleRepository, MenuRepository menuRepository, ModuleFuncRepository moduleFuncRepository) {
        super(userRepository);
        this.roleRepository = roleRepository;
        this.menuRepository = menuRepository;
        this.moduleFuncRepository = moduleFuncRepository;
    }

    public List<Role> getRoleList() {
        return roleRepository.findAllByOrderBySortAsc();
    }

    public List<Role> getRoleListByAppId(String loginNo, String appId) {
        User user = findCurrUserInfo(loginNo);
        if (isAdmin(user)) {
            return roleRepository.findByAppidOrderBySortAsc(appId);
        } else {
            int currLevel = getRoleMinLevel(appId, user);
            return roleRepository.findByAppidAndLevelsGreaterThanOrderBySortAsc(appId, currLevel);
        }
    }

    private Role doSave(Role role, RolePO rolePO) {
        role.setName(rolePO.getName());
        role.setCode(rolePO.getCode());
        role.setSort(rolePO.getSort());
        role.setLevels(rolePO.getLevels());
        role.setUserSet(new HashSet<>(userRepository.findAllById(rolePO.getUserIds())));
        role.setMenuSet(new HashSet<>(menuRepository.findAllById(rolePO.getMenuIds())));
        role.setModuleFuncSet(new HashSet<>(moduleFuncRepository.findAllById(rolePO.getModuleFuncIds())));
        return roleRepository.save(role);
    }

    @Transactional
    public Role doCreate(RolePO rolePO, String loginNo) throws ServerException {
        User user = findCurrUserInfo(loginNo);
        int currLevel = getRoleMinLevel(rolePO.getAppid(), user);
        if (currLevel >= rolePO.getLevels()) {
            throw new ServerException("没有权限做此操作，角色级别必须大于 " + currLevel);
        }
        if (rolePO.getCode().equals(RoleCode.ADMIN)) {
            throw new ServerException("不允许创建超级管理员");
        }
        Role role = new Role();
        role.setAppid(rolePO.getAppid());
        return doSave(role, rolePO);
    }

    @Transactional
    public void doDelete(String loginNo, List<String> idList) throws ServerException {
        User user = findCurrUserInfo(loginNo);
        Map<String, Integer> roleMinLevel = getRoleMinLevel(user);
        List<Role> roleList = roleRepository.findAllById(idList);
        for (Role role : roleList) {
            if (!roleMinLevel.containsKey(role.getAppid()) || roleMinLevel.get(role.getAppid()) >= role.getLevels()) {
                throw new ServerException("没有权限做此操作");
            }
        }
        roleRepository.deleteByIdIn(idList);
    }

    @Transactional
    public Role doUpdate(String loginNo, RolePO rolePO) throws ServerException {
        User user = findCurrUserInfo(loginNo);
        Optional<Role> roleOptional = roleRepository.findById(rolePO.getId());
        if (roleOptional.isEmpty()) {
            throw new ServerException("找不到角色信息");
        }
        int currLevel = getRoleMinLevel(rolePO.getAppid(), user);
        Role role = roleOptional.get();
        if (!isAdmin(user)) {
            if (currLevel > 0 && currLevel >= rolePO.getLevels()) {
                throw new ServerException("没有权限做此操作，角色级别必须大于 " + currLevel);
            }
            if (currLevel > 0 && currLevel >= role.getLevels()) {
                throw new ServerException("没有权限做此操作，请联系系统管理员");
            }
        } else {
            if (!rolePO.getCode().equals(role.getCode()) && role.getCode().equals(RoleCode.ADMIN)) {
                throw new ServerException("超级管理员编码不允许修改");
            }
            if (rolePO.getLevels() != role.getLevels() && role.getLevels() <= 0) {
                throw new ServerException("超级管理员级别不允许修改");
            }
            if (rolePO.getLevels() != role.getLevels() && rolePO.getLevels() <= 0) {
                throw new ServerException("不允许修改为超级管理员级别[" + rolePO.getLevels() + "]");
            }
        }
        return doSave(role, rolePO);
    }

    public RoleVO getRoleInfo(String roleId) throws ServerException {
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        if (roleOptional.isEmpty()) {
            throw new ServerException("找不到角色信息");
        }
        Role role = roleOptional.get();
        RoleVO roleVO = new RoleVO();
        roleVO.setId(role.getId());
        roleVO.setAppid(role.getAppid());
        roleVO.setCode(role.getCode());
        roleVO.setLevels(role.getLevels());
        roleVO.setName(role.getName());
        roleVO.setSort(role.getSort());
        roleVO.setUserIds(role.getUserSet().stream().map(User::getId).collect(Collectors.toList()));
        roleVO.setMenuIds(role.getMenuSet().stream().map(Menu::getId).collect(Collectors.toList()));
        roleVO.setModuleFuncIds(role.getModuleFuncSet().stream().map(ModuleFunc::getId).collect(Collectors.toList()));
        return roleVO;
    }
}
