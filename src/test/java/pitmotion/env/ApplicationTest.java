package pitmotion.env;

import jakarta.transaction.Transactional;
import pitmotion.env.enums.ProfileName;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({ProfileName.TEST, ProfileName.HTTP, ProfileName.QUEUE})
@Transactional
@AutoConfigureMockMvc
public abstract class ApplicationTest {}
