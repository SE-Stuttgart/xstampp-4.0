package de.xstampp.service.auth.util;

import de.xstampp.service.auth.data.User;
import de.xstampp.service.auth.service.UserDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class XStamppTaskScheduler {

    @Autowired
    UserDataService userDataService;

    Logger logger = LoggerFactory.getLogger(XStamppTaskScheduler.class);

    @Scheduled(fixedRate = 86400000) // Run every day
    public void deleteInactiveUsers() {
        userDataService.getAllUsers().stream()
                .filter(this::isNoSystemAdmin)
                .filter(this::isInactiveForTwoYears)
                .forEach(this::deleteUser);
    }

    private boolean isNoSystemAdmin(User user) {
        return !user.isSystemAdmin();
    }

    private boolean isInactiveForTwoYears(User user) {
        return durationSinceLastLogin(user).toDays() >= 2 * 365;
    }

    private Duration durationSinceLastLogin(User user) {
        return Duration.between(user.getLastLogin().toLocalDateTime(), LocalDateTime.now());
    }

    private void deleteUser(User user) {
        boolean isDeleted = userDataService.deleteUserNoChecks(user.getId());
        if (isDeleted)
            logger.info(String.format("User %s deleted due to inactivity.", user.getDisplayName()));
    }
}
