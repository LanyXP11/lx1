package com.lx.pk.dubbo.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chenjiang on 2018/12/14.
 */
@Activate(group = {Constants.PROVIDER})
public class DubboLogFilter implements Filter {

    private Logger LOGGER = LoggerFactory.getLogger(DubboLogFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        long begin = System.currentTimeMillis();
        try {

            long end = System.currentTimeMillis();
            if (Boolean.TRUE.toString().equals(invocation.getAttachments().get(Constants.ASYNC_KEY))) {
                RpcContext.getContext().getAttachments().remove(Constants.ASYNC_KEY);
            }
            Result result = invoker.invoke(invocation);
            LOGGER.info("DUBBO被调用成功，调用的接口是:{}, 调用的方法是:{},入参:{},返回值:{}, 耗时:{} MS", invoker.getInterface().getName(),
                    invocation.getMethodName(), JSON.toJSONString(invocation.getArguments()),
                    JSON.toJSONString(result),
                    end - begin);
        } catch (Exception e) {
            long end = System.currentTimeMillis();
            LOGGER.error("DUBBO服务调用失败，调用的接口是:{}, 调用的方法是:{},入参:{},耗时:{} ms", invoker.getInterface().getName(),
                    invocation.getMethodName(), JSON.toJSONString(invocation.getArguments()),
                    end - begin, e);
            throw e;

        }
        return invoker.invoke(invocation);
    }
}
