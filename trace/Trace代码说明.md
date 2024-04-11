## Trace代码说明

`Recordxxxx`实体类主要作为请求和返回值的封装

`Controller`类是对外接口

`TraceRecord.sol` 合约代码

`TraceReocrd.sol`是合约编译后提供给Java使用的接口代码

`xxxxClient` 是调用合约代码的客户端，向上为Controller服务。