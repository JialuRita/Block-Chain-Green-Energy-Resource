package io.renren.modules.blockchain.entity;



import lombok.Data;

/**
 * 溯源请求返回实体类
 */
@Data
public class RecordResponseEntity {


    /**
     * 在链上的index值
     */
    private String index;
    /**
     * 操作的用户ID
    */
    private String userID;

    /**
     * 操作的资源ID
     */
    private String resID;

    /**
     *  操作的时间
     */
    private String time;
    /**
     * 操作的种类, 对应在RecordOperation
     */
    private String operation;
    /**
     *  操作所在的区块数
     */
    private String txBlockNum;
    /**
     *  产生操作的交易Hash
     */
    private String txHash;

    public RecordResponseEntity() {

    }

    public RecordResponseEntity(String num) {
        index = num;
    }
    public RecordResponseEntity(String index, String value1, String value2, String value3, String value4, String value5, String value6) {
        this.index = index;
        userID = value1;
        resID = value2;
        time = value3;
        operation = value4;
        txBlockNum = value5;
        txHash = value6;
    }
}
