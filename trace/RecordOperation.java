package io.renren.modules.blockchain.entity;

public enum RecordOperation {
    CREATE, // 创建新资源
    ALL_DELETE, // 删除资源
    CHANGE,

    COLLECT_REFUSE, // 驳回资源创建申请
    COLLECT_APPROVAL, // 同意资源录入
    COLLECT_DELETE, // 可能是多个删除

    SAVE_CHANGE, // 修改资源
    SAVE_REFUSE,
    SAVE_APPROVAL,
    SAVE_DELETE, // 此时可能是多个删除


    APPRAISAL_CHANGE, //修改鉴定信息

    SHARE,
    NOT_SHARE

}
