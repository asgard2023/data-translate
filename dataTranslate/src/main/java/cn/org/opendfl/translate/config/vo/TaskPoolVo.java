package cn.org.opendfl.translate.config.vo;

import cn.org.opendfl.translate.config.TaskPoolConfig;
import lombok.Data;

@Data
public class TaskPoolVo {
    public TaskPoolVo(){

    }
    public TaskPoolVo(TaskPoolConfig taskPoolConfig){
        this.corePoolSize=taskPoolConfig.getCorePoolSize();
        this.maxPoolSize=taskPoolConfig.getMaxPoolSize();
        this.queueCapacity=taskPoolConfig.getQueueCapacity();
        this.threadNamePrefix=taskPoolConfig.getThreadNamePrefix();
    }

    private int corePoolSize = 10;

    private int maxPoolSize = 10;

    private int queueCapacity = 50;

    private String threadNamePrefix = "async-";

}
