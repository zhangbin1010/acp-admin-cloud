package pers.acp.admin.common.constant.path;

/**
 * @author zhang by 10/01/2019
 * @since JDK 11
 */
public interface OauthApi {

    String basePath = "/oauth";

    String currUser = "/userinfo";

    String modifiableUser = "/moduserlist";

    String currMenu = "/menulist";

    String appConfig = "/app";

    String updateSecret = appConfig + "/updatesecret";

    String roleConfig = "/role";

    String roleCodes = roleConfig + "/rolecodes";

    String authConfig = "/auth";

    String moduleFuncCodes = authConfig + "/modulefunccodes";

    String menuList = authConfig + "/menulist";

    String moduleFuncList = authConfig + "/modulefunclist";

    String userConfig = "/user";

    String orgConfig = "/org";

    String runtimeConfig = "/runtime";

}