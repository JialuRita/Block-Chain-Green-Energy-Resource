package io.renren.modules.blockchain.entity;

import io.renren.modules.resources.entity.HnResourcesCollect;
import lombok.Data;

import java.util.List;

@Data
public class RecordRequestEntity {

    // 请求ID
    private String userID;
    // 请求资源ID,考虑到前端可以对资源进行批量操作,设置为List
    private List<String> resID;
//    private String resID;
    // operation 目前有：
    /*
    创建          => 需要上链数据
    修改=> 需要上链数据
    删除 => 不需要上链数据
    反驳 => 不需要上链数据
    录入   => 需要上链数据
     */
    private RecordOperation operation;

    /**
     * 上链数据，在上链时，需要被格式化。
     */
    private String upChainData;

}
