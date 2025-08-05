package pitmotion.env.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import pitmotion.env.enums.ProfileName;

@Component
@ConfigurationProperties(prefix = ProfileName.IMPORT)
public class ImportProperties {
    private long delayMs = 500L;
    private long retryDelayMs = 60_000L;
    private int pageSize = 100;

    public long getDelayMs() { return delayMs; }
    public void setDelayMs(long delayMs) { this.delayMs = delayMs; }

    public long getRetryDelayMs() { return retryDelayMs; }
    public void setRetryDelayMs(long retryDelayMs) { this.retryDelayMs = retryDelayMs; }
    
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}
