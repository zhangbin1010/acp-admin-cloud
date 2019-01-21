package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.base.OauthBaseDomain;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.repo.UserRepository;

import java.util.List;

/**
 * @author zhang by 19/12/2018
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class UserDomain extends OauthBaseDomain {

    @Autowired
    public UserDomain(UserRepository userRepository) {
        super(userRepository);
    }

    @Transactional
    public User doSaveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> findModifiableUserList(String loginNo) {
        User user = findCurrUserInfo(loginNo);
        if (isAdmin(user)) {
            return userRepository.findAll();
        } else {
            return userRepository.findByLevelsGreaterThan(user.getLevels());
        }
    }

}
