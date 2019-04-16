package pers.acp.admin.oauth.controller.api;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.acp.admin.common.base.BaseController;
import pers.acp.admin.common.constant.ModuleFuncCode;
import pers.acp.admin.oauth.constant.OauthApi;
import pers.acp.admin.oauth.constant.AuthConfigExpression;
import pers.acp.admin.common.vo.InfoVO;
import pers.acp.admin.oauth.domain.MenuDomain;
import pers.acp.admin.oauth.domain.ModuleFuncDomain;
import pers.acp.admin.oauth.entity.Menu;
import pers.acp.admin.oauth.entity.ModuleFunc;
import pers.acp.admin.oauth.po.MenuPO;
import pers.acp.admin.oauth.po.ModuleFuncPO;
import pers.acp.admin.oauth.vo.MenuVO;
import pers.acp.admin.oauth.vo.ModuleFuncVO;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springboot.core.vo.ErrorVO;
import pers.acp.springcloud.common.log.LogInstance;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(OauthApi.basePath)
@Api("权限信息")
public class AuthController extends BaseController {

    private final LogInstance logInstance;

    private final MenuDomain menuDomain;

    private final ModuleFuncDomain moduleFuncDomain;

    private List<String> moduleFuncCodeList = new ArrayList<>();

    @Autowired
    public AuthController(LogInstance logInstance, MenuDomain menuDomain, ModuleFuncDomain moduleFuncDomain) {
        this.logInstance = logInstance;
        this.menuDomain = menuDomain;
        this.moduleFuncDomain = moduleFuncDomain;
    }

    @PostConstruct
    public void init() {
        try {
            for (Field field : ModuleFuncCode.class.getDeclaredFields()) {
                moduleFuncCodeList.add(field.get(ModuleFuncCode.class).toString());
            }
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
        }
    }

    @ApiOperation(value = "获取模块功能编码列表")
    @PreAuthorize(AuthConfigExpression.authConfig)
    @GetMapping(value = OauthApi.moduleFuncCodes, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<String>> getModuleFuncCode() {
        return ResponseEntity.ok(moduleFuncCodeList);
    }

    @ApiOperation(value = "获取当前用户所属菜单", notes = "根据当前登录的用户信息，查询有权访问的菜单列表")
    @GetMapping(value = OauthApi.currMenu, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Menu>> currMenuList(OAuth2Authentication user) {
        return ResponseEntity.ok(menuDomain.getMenuList(user.getOAuth2Request().getClientId(), user.getName()));
    }

    @ApiOperation(value = "获取指定应用下的菜单列表", notes = "查询指定应用的菜单列表，供选择配置")
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = OauthApi.menuList + "/{appId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Menu>> menuList(@PathVariable String appId) throws ServerException {
        if (CommonTools.isNullStr(appId)) {
            throw new ServerException("应用ID不能为空");
        }
        return ResponseEntity.ok(menuDomain.getMenuListByAppId(appId));
    }

    @ApiOperation(value = "获取指定应用下的模块功能列表", notes = "查询指定应用的模块功能列表，供选择配置")
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = OauthApi.moduleFuncList + "/{appId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<ModuleFunc>> moduleFuncList(@PathVariable String appId) throws ServerException {
        if (CommonTools.isNullStr(appId)) {
            throw new ServerException("应用ID不能为空");
        }
        return ResponseEntity.ok(moduleFuncDomain.getModuleFuncListByAppId(appId));
    }

    @ApiOperation(value = "获取菜单列表", notes = "查询所有菜单列表")
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = OauthApi.menuConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Menu>> allMenuList() {
        return ResponseEntity.ok(menuDomain.getAllMenuList());
    }

    @ApiOperation(value = "获取模块功能列表", notes = "查询所有模块功能列表")
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = OauthApi.moduleFuncConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<ModuleFunc>> allModuleFuncList() {
        return ResponseEntity.ok(moduleFuncDomain.getAllModuleFuncList());
    }

    @ApiOperation(value = "新建菜单信息",
            notes = "名称、应用ID、图标、链接、上级、序号、模式、状态、关联角色")
    @ApiResponses({
            @ApiResponse(code = 201, message = "创建成功", response = Menu.class),
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(AuthConfigExpression.authAdd)
    @PutMapping(value = OauthApi.menuConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Menu> addMenu(@RequestBody @Valid MenuPO menuPO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuDomain.doCreate(menuPO));
    }

    @ApiOperation(value = "新建模块功能信息",
            notes = "名称、应用ID、编码、上级、关联角色")
    @ApiResponses({
            @ApiResponse(code = 201, message = "创建成功", response = Menu.class),
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(AuthConfigExpression.authAdd)
    @PutMapping(value = OauthApi.moduleFuncConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ModuleFunc> addModuleFunc(@RequestBody @Valid ModuleFuncPO moduleFuncPO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleFuncDomain.doCreate(moduleFuncPO));
    }

    @ApiOperation(value = "删除指定的菜单信息")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；存在下级，不允许删除；", response = ErrorVO.class)
    })
    @PreAuthorize(AuthConfigExpression.authDelete)
    @DeleteMapping(value = OauthApi.menuConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> deleteMenu(@ApiParam(value = "id列表", required = true) @NotEmpty(message = "id不能为空") @NotNull(message = "id不能为空")
                                             @RequestBody List<String> idList) throws ServerException {
        menuDomain.doDelete(idList);
        InfoVO infoVO = new InfoVO();
        infoVO.setMessage("删除成功");
        return ResponseEntity.ok(infoVO);
    }

    @ApiOperation(value = "删除指定的模块功能信息")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；存在下级，不允许删除；", response = ErrorVO.class)
    })
    @PreAuthorize(AuthConfigExpression.authDelete)
    @DeleteMapping(value = OauthApi.moduleFuncConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> deleteModuleFunc(@ApiParam(value = "id列表", required = true) @NotEmpty(message = "id不能为空") @NotNull(message = "id不能为空")
                                                   @RequestBody List<String> idList) throws ServerException {
        moduleFuncDomain.doDelete(idList);
        InfoVO infoVO = new InfoVO();
        infoVO.setMessage("删除成功");
        return ResponseEntity.ok(infoVO);
    }

    @ApiOperation(value = "更新菜单信息",
            notes = "名称、应用ID、图标、链接、上级、序号、模式、状态、关联角色")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVO.class)
    })
    @PreAuthorize(AuthConfigExpression.authUpdate)
    @PatchMapping(value = OauthApi.menuConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Menu> updateMenu(@RequestBody @Valid MenuPO menuPO) throws ServerException {
        return ResponseEntity.ok(menuDomain.doUpdate(menuPO));
    }

    @ApiOperation(value = "更新模块功能信息",
            notes = "名称、应用ID、编码、上级、关联角色")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；模块功能编码非法，请重新输入；没有权限做此操作；ID不能为空；找不到信息；", response = ErrorVO.class)
    })
    @PreAuthorize(AuthConfigExpression.authUpdate)
    @PatchMapping(value = OauthApi.moduleFuncConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ModuleFunc> updateModuleFunc(@RequestBody @Valid ModuleFuncPO moduleFuncPO) throws ServerException {
        if (!moduleFuncCodeList.contains(moduleFuncPO.getCode())) {
            throw new ServerException("模块功能编码非法，请重新输入");
        }
        return ResponseEntity.ok(moduleFuncDomain.doUpdate(moduleFuncPO));
    }

    @ApiOperation(value = "获取菜单详细信息")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVO.class)
    })
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = OauthApi.menuConfig + "/{menuId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<MenuVO> menuInfo(@ApiParam(value = "菜单id", required = true) @NotBlank(message = "菜单id不能为空")
                                           @PathVariable String menuId) throws ServerException {
        return ResponseEntity.ok(menuDomain.getMenuInfo(menuId));
    }

    @ApiOperation(value = "获取模块功能详细信息")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVO.class)
    })
    @PreAuthorize(AuthConfigExpression.authQuery)
    @GetMapping(value = OauthApi.moduleFuncConfig + "/{moduleFuncId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ModuleFuncVO> moduleFuncInfo(@ApiParam(value = "模块功能id", required = true) @NotBlank(message = "模块功能id不能为空")
                                                       @PathVariable String moduleFuncId) throws ServerException {
        return ResponseEntity.ok(moduleFuncDomain.getModuleFuncInfo(moduleFuncId));
    }

}
