package ua.kpi.nc.controller.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import ua.kpi.nc.controller.auth.Utils.FaceBookUtils;
import ua.kpi.nc.persistence.model.Role;
import ua.kpi.nc.persistence.model.SocialInformation;
import ua.kpi.nc.persistence.model.SocialNetwork;
import ua.kpi.nc.persistence.model.User;
import ua.kpi.nc.persistence.model.enums.RoleEnum;
import ua.kpi.nc.persistence.model.enums.SocialNetworkEnum;
import ua.kpi.nc.persistence.model.impl.real.UserImpl;
import ua.kpi.nc.service.ServiceFactory;
import ua.kpi.nc.service.SocialInformationService;
import ua.kpi.nc.service.UserService;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by IO on 27.05.2016.
 */
public class SocialNetworkAuthenticationManager implements AuthenticationManager {

    private static Logger log = LoggerFactory.getLogger(SocialNetworkAuthenticationManager.class.getName());
    private final static boolean IS_ACTIVE = true;
    private final static String NO_PASSWORD = "";
    private final static String NO_CONFIRM_TOKEN = "";

    private UserAuthServiceSocial userAuthServiceSocial;
    private UserService userService;
    private SocialInformationService socialInformationService;


    private SocialNetworkAuthenticationManager() {
        userAuthServiceSocial = UserAuthServiceSocial.getInstance();
        socialInformationService = ServiceFactory.getSocialInformationService();
        userService = ServiceFactory.getUserService();
    }

    private static class SocialNetworkAuthenticationManagerHolder {
        private static final SocialNetworkAuthenticationManager HOLDER = new SocialNetworkAuthenticationManager();
    }

    public static SocialNetworkAuthenticationManager getInstance() {
        return SocialNetworkAuthenticationManagerHolder.HOLDER;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserAuthentication userAuthentication = ((UserAuthentication) authentication);
        SocialInformation socialInformation = getSocialInformation(userAuthentication);
        socialInformation.setIdUserInSocialNetwork(FaceBookUtils.getSocialUserId(socialInformation.getAccessInfo()));
        SocialNetwork socialNetwork = SocialNetworkEnum.getSocialNetwork(getSocialNetworkId(socialInformation));
        if (Objects.equals(socialNetwork.getTitle(), SocialNetworkEnum.FaceBook.getTitle())) {
            User user = userAuthServiceSocial.loadUserBySocialIdNetworkId(socialInformation.getIdUserInSocialNetwork(), socialNetwork.getId());
            if (null == user) {
                return registerFaceBookUser(socialInformation);
            } else {
                updateFaceBookUser(socialNetwork.getId(), socialInformation.getIdUserInSocialNetwork(), socialInformation.getAccessInfo());
                changeSocialInformation(SocialNetworkEnum.FaceBook.getId(), user, socialInformation.getAccessInfo());
                return new UserAuthentication(user, socialInformation.getIdUserInSocialNetwork(), socialNetwork.getId());
            }
        }


//        Long id = getSocialUserId(userAuthentication.getDetails().getSocialInformations().iterator().next().getAccessInfo());
//        Long idSocialNetwork = getSocialNetworkId(userAuthentication.getDetails().getSocialInformations().iterator().next());
//        User user = ((User) authentication.getDetails());
//        SocialInformation socialInformation = user.getSocialInformations().iterator().next();
//        User existUser = userAuthServiceLoginPassword.loadUserByUsername(user.getEmail());
//
//
//        if (null != existUser) {
//            if (socialInformationService.isExist(user.getEmail(), user.getSocialInformations().iterator().next().getSocialNetwork().getId())) {
//                updateFaceBookUser(socialInformation.getAccessInfo(), existUser);
//                return new UserAuthentication(existUser);
//            } else {
//                return new UserAuthentication(existUser);
//            }
//        } else {
//            return registerFaceBookUser(socialInformation);
//        }
        return null;
    }


    private void registerNewUser(SocialInformation socialInformation) {
//        Long socialNetworkId = getSocialNetworkId(socialInformation);
//        if (Objects.equals(socialNetworkId, SocialNetworkEnum.FaceBook.getId())) {
//        }
//        //user.getSocialInformations().add(socialInformation);
//        socialInformationService.insertSocialInformation(socialInformation, user, socialInformation.getSocialNetwork());
    }

    private UserAuthentication registerFaceBookUser(SocialInformation socialInformation) {
        Facebook facebook = new FacebookTemplate(FaceBookUtils.getFaceBookAccessToken(socialInformation.getAccessInfo()));
        org.springframework.social.facebook.api.User profile = facebook.userOperations().getUserProfile();
        User user = createNewUser(profile);
        userService.insertUser(user, new ArrayList<>(Collections.singletonList(RoleEnum.getRole(RoleEnum.ROLE_STUDENT))));
        socialInformation.setUser(user);
        socialInformationService.insertSocialInformation(socialInformation, user, socialInformation.getSocialNetwork());
        return new UserAuthentication(user, socialInformation.getIdUserInSocialNetwork(), socialInformation.getSocialNetwork().getId());
    }

    private void updateFaceBookUser(Long idSocialNetwork, Long idSocialUser, String info) {
        int i = socialInformationService.updateSocialInformation(idSocialNetwork, idSocialUser, info);
    }

    private User createNewUser(org.springframework.social.facebook.api.User profile) {
        User user = new UserImpl(profile.getEmail(),
                profile.getFirstName(),
                profile.getMiddleName(),
                profile.getLastName(),
                NO_PASSWORD,
                IS_ACTIVE,
                new Timestamp(System.currentTimeMillis()),
                NO_CONFIRM_TOKEN
        );
        user.setRoles(new HashSet<Role>() {{
            add(RoleEnum.getRole(RoleEnum.ROLE_STUDENT));
        }});
        return user;
    }

    private Long getSocialNetworkId(SocialInformation socialInformation) {
        return socialInformation.getSocialNetwork().getId();
    }

    private static SocialInformation getSocialInformation(UserAuthentication userAuthentication) {
        return userAuthentication.getDetails().getSocialInformations().iterator().next();
    }

    private static void changeSocialInformation(Long id, User user, String info){
        user.getSocialInformations().stream().filter(socialInformation -> Objects.equals(socialInformation.getSocialNetwork().getId(), id)).forEach(socialInformation -> {
            socialInformation.setAccessInfo(info);
        });
    }
}

