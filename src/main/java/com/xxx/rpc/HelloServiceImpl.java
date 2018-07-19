package com.xxx.rpc;

import com.xxx.rpc.sample.api.RpcService;


@RpcService(HelloService.class)

public class HelloServiceImpl {
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String hello(String name) {
        return "Hello! " + name;
    }

}