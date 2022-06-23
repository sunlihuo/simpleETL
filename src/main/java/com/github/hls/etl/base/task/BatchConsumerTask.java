package com.github.hls.etl.base.task;

import com.github.hls.etl.utils.SimpleDBBatchUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Service
public class BatchConsumerTask extends Thread{

    public static boolean runflag = true;
    @Resource
    private DataSource targetDatesource;

    @Override
    public void run() {
        runflag = true;
        while (runflag) {
            SimpleDBBatchUtils.insertBatchJDBC(targetDatesource);
            SimpleDBBatchUtils.updateBatchJDBC(targetDatesource);
        }
    }

}
