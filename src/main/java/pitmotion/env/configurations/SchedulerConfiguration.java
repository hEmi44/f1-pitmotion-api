package pitmotion.env.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import pitmotion.env.enums.ProfileName;

@Configuration
@Profile(ProfileName.SCHEDULER)
@EnableScheduling
public class SchedulerConfiguration {}
