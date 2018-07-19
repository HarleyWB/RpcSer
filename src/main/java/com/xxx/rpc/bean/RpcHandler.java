package com.xxx.rpc.bean;


import com.xxx.rpc.server.ThreadPool;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcHandler.class);

    private final Map<String, Object> handlerMap;

    public RpcHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        Thread.sleep(5000);//TODO 测试异步运行
        ThreadPool.getInstance().submit(new Runnable() {
            //单例模式 获取线程池 放到线程池运行 防止高并发资源耗尽。
            //netty一个请求一个线程 还是有可能发生线程过多资源耗尽的
            @Override
            public void run() {
                RpcResponse response = new RpcResponse();

                response.setRequestId(request.getRequestId());
                try {
                    Object result = handle(request);
                    response.setResult(result);
                } catch (Throwable t) {
                    response.setError(t);
                }
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }
        });

    }

    private Object handle(RpcRequest request) throws Throwable {


        String interFaceName = request.getClassName();//接口名
        Object serviceBean = handlerMap.get(interFaceName);//根据接口名获得 实现类

        Class<?> serviceClass = serviceBean.getClass();

        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();
        //利用反射 获取方法

/*        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        // return method.invoke(serviceBean, parameters);
        Object o = method.invoke(serviceBean, parameters);*/

        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }
}