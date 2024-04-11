package io.renren.modules.blockchain.client;

import com.alibaba.fastjson.JSONArray;
import io.renren.modules.blockchain.entity.AbiValueTypeEntity;
import io.renren.modules.blockchain.utils.SpringUtils;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.abi.ABICodec;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.abi.FunctionEncoder;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.*;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.model.NodeVersion;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

@Service
public class BlockchainClient implements ApplicationRunner {

    private Client client;

    private String abi = "[{\"constant\":false,\"inputs\":[{\"name\":\"entity\",\"type\":\"string[]\"}],\"name\":\"update\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"entity\",\"type\":\"string[]\"}],\"name\":\"insert\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"number\",\"type\":\"string\"}],\"name\":\"selectByNumber\",\"outputs\":[{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"number\",\"type\":\"string\"}],\"name\":\"remove\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"entity\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"count\",\"type\":\"int256\"}],\"name\":\"InsertResult\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"entity\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"count\",\"type\":\"int256\"}],\"name\":\"UpdateResult\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"entity\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"number\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"count\",\"type\":\"int256\"}],\"name\":\"RemoveResult\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"entity\",\"type\":\"string\"}],\"name\":\"SelectResult\",\"type\":\"event\"}]";



    @Override
    public void run(ApplicationArguments args) throws Exception {

        BcosSDK sdk = SpringUtils.getBean("bcosSDK");
        client = sdk.getClient(1);
    }


    /**
     * 获取Client对象对应的群组最新块高
     *
     * @return*/
    public BlockNumber getBlockNumber(){
        return client.getBlockNumber();
    }


    /**
     * 查询指定合约地址对应的合约代码信息
     *
     */
    public Code getCode(String address){
        return client.getCode(address);
    }

    /**
     * 获取Client对应群组的交易统计信息，包括上链的交易数、上链失败的交易数目
     *
     * @return TotalTransactionCount: 交易统计信息，包括：
     * txSum: 上链的交易总量
     * blockNumber: 群组的当前区块高度
     * failedTxSum: 上链执行异常的交易总量
     */
    public TotalTransactionCount getTotalTransactionCount(){
        return client.getTotalTransactionCount();
    }

    /**
     *根据区块哈希获取区块信息
     * @param address:地址
     * true: 节点返回的区块中包含完整的交易信息
     * false: 节点返回的区块中仅包含交易哈希
     */
    public BcosBlock getBlockByHash(String address, boolean full){
        return client.getBlockByHash(address, full);
    }

    /**
     *根据区块高度获取区块信息
     * @param number:地址
     * true: 节点返回的区块中包含完整的交易信息
     * false: 节点返回的区块中仅包含交易哈希
     */
    public BcosBlock getBlockByNumber(BigInteger number, boolean full){
        return client.getBlockByNumber(number, full);
    }

    /**
     *根据区块高度获取区块哈希
     * @param number :地址
     * @return
     */
    public BlockHash getBlockHashByNumber(BigInteger number){
        return client.getBlockHashByNumber(number);
    }

    /**
     *根据区块哈希获取区块头信息
     * @param blockHash: 区块哈希;
     * signatureList: true/false，表明返回的区块头中是否附带签名列表信息
     * true: 返回的区块头中带有区块签名列表信息；
     * false: 返回的区块头中不带区块签名列表信息。
     * @return
     */
    public BcosBlockHeader getBlockHeaderByHash(String blockHash, boolean signatureList){

        BcosTransactionReceipt transactionReceipt1 = client.getTransactionReceipt("0x5e5a96a75da9807a97d75ccf41104a923e171991cbb94f6f0914d92ec41bc4e7");
        Optional<TransactionReceipt> transactionReceipt2 = transactionReceipt1.getTransactionReceipt();
        TransactionReceipt receipt1 = transactionReceipt2.get();
        // 获取当前群组对应的密码学接口
        CryptoSuite cryptoSuite = client.getCryptoSuite();
        List<Object> argsObjects = new ArrayList<Object>();

        ABICodec abiCodec = new ABICodec(cryptoSuite);



        TransactionDecoderInterface decoder = new TransactionDecoderService(cryptoSuite);

        String s = decoder.decodeReceiptMessage(receipt1.getInput());
        try {


            List<Object> insert1 = abiCodec.decodeMethod(abi, "insert", receipt1.getOutput());
            List<TransactionReceipt.Logs> logs = receipt1.getLogs();
            for (TransactionReceipt.Logs log : logs) {
                List<Object> selectResult = abiCodec.decodeEvent(abi, "SelectResult", log.toEventLog());
                System.out.println(selectResult);
            }

            TransactionResponse incrementUint256 = decoder.decodeReceiptWithoutValues(abi, receipt1);
            TransactionResponse insert = decoder.decodeReceiptWithValues(abi, "insert", receipt1);
            TransactionResponse transactionResponse = decoder.decodeReceiptStatus(receipt1);
            System.out.println(insert);
        } catch (ABICodecException e) {
            e.printStackTrace();
        } catch (TransactionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        BcosBlock blockByHash = client.getBlockByHash(blockHash, true);
        String jsonrpc = blockByHash.getJsonrpc();

        // 获取最新区块的所有交易回执信息(cient初始化过程省略，详细可以参考快速入门)
        BcosTransactionReceiptsDecoder bcosTransactionReceiptsDecoder =
                client.getBatchReceiptsByBlockHashAndRange(
                        blockHash,
                        "0",
                        "-1");
        // 解码交易回执信息
        BcosTransactionReceiptsInfo.TransactionReceiptsInfo receiptsInfo = bcosTransactionReceiptsDecoder.decodeTransactionReceiptsInfo();
        // 获取回执所在的区块信息
        BcosTransactionReceiptsInfo.BlockInfo blockInfo = receiptsInfo.getBlockInfo();
        // 获取交易回执列表
        List<TransactionReceipt> receiptList = receiptsInfo.getTransactionReceipts();
        for (TransactionReceipt receipt : receiptList) {
            System.out.println(receipt.getBlockNumber());

        }

        return client.getBlockHeaderByHash(blockHash,signatureList);
    }

    /**
     * 根据交易哈希获取交易信息
     * @param hash 交易哈希
     * return BcosTransaction: 指定哈希对应的交易信息
     */
    public BcosTransaction getTransactionByHash(String hash){
        return client.getTransactionByHash(hash);
    }


    /**
     * 根据交易哈希获取交易信息，交易信息中带有交易Merkle证明
     * @return 指定哈希对应的交易信息
     */
    public TransactionWithProof getTransactionByHashWithProof(String transactionHash){
        return client.getTransactionByHashWithProof(transactionHash);

    }

    /**
     * 指定哈希对应的交易信息
     * @param blockNumber: 交易所在的区块高度
     * @param transactionIndex: 交易索引
     * @return BcosTransaction: 指定区块高度和交易索引对应的交易信息
     */
    public BcosTransaction getTransactionByBlockNumberAndIndex(BigInteger blockNumber, BigInteger transactionIndex){
        return client.getTransactionByBlockNumberAndIndex(blockNumber, transactionIndex);
    }

    /**
     * 根据交易哈希获取交易回执信息
     * @param transactionHash: 交易哈希
     * @return BcosTransactionReceipt: 交易哈希对应的回执信息。
     */
    public BcosTransactionReceipt getTransactionReceipt(String transactionHash){
        return client.getTransactionReceipt(transactionHash);
    }

    /**
     * 根据交易哈希获取交易回执信息，回执中带有Merkle证明
     * @param transactionHash: 交易哈希
     * @return TransactionReceiptWithProof: 带有Merkle证明的交易回执信息
     */
    public TransactionReceiptWithProof getTransactionReceiptByHashWithProof(String transactionHash){
        return client.getTransactionReceiptByHashWithProof(transactionHash);
    }

    /**
     * 获取交易池内待处理的交易列表
     * @return PendingTransactions: 交易池内未处理的交易列表
     */
    public PendingTransactions getPendingTransaction(){
        return client.getPendingTransaction();
    }

    /**
     * 获取交易池内未处理的交易数目
     * @return PendingTxSize: 交易池内未处理的交易数目
     */
    public PendingTxSize getPendingTxSize(){
        return client.getPendingTxSize();
    }


    /**
     * 启动指定节点的指定群组
     * @param groupId: 需要启动的群组ID;
     *     peerIpPort: 启动指定群组的节点IP:Port信息。
     * @return StartGroup: 群组启动状态
     */
    public StartGroup startGroup(Integer groupId, String peerIpPort){
        return client.startGroup(groupId, peerIpPort);
    }

    /**
     * 停止指定节点的指定群组
     * @param groupId: 需要停止的群组ID;
     *     peerIpPort: 群组所在的节点IP:Port信息。
     * @return StopGroup: 被停止的群组状态
     */
    public StopGroup stopGroup(Integer groupId, String peerIpPort){
        return client.stopGroup(groupId, peerIpPort);
    }

    /**
     * 删除指定节点的指定群组
     * @param
     *     groupId: 被删除的群组信息；
     *     peerIpPort: 群组所在的节点IP:Port信息。
     * @return RemoveGroup: 被删除的群组状态。
     */
    public RemoveGroup removeGroup(Integer groupId, String peerIpPort){
        return client.removeGroup(groupId, peerIpPort);
    }

    /**
     * 恢复指定节点被删除的群组
     * @param
     *     groupId: 需要恢复的群组ID；
     *     peerIpPort: 群组所在的节点IP:Port信息
     * @return RecoverGroup: 被恢复的群组状态
     */
    public RecoverGroup recoverGroup(Integer groupId, String peerIpPort){
        return client.recoverGroup(groupId, peerIpPort);
    }


    /**
     * 查询指定群组的状态
     * @param
     *     groupId: 被查询的群组ID;
     *     peerIpPort: 群组状态查询发送到的目标节点信息，包括IP:Port信息。
     * @return QueryGroupStatus: 被查询的群组状态
     */
    public QueryGroupStatus queryGroupStatus(Integer groupId, String peerIpPort){
        return client.queryGroupStatus(groupId, peerIpPort);
    }


    /**
     * 获取指定节点的群组列表
     * @param
     *     peerIpPort: 群组状态查询发送到的目标节点信息，包括IP:Port信息。
     * @return QueryGroupStatus: 被查询的群组状态
     */
    public GroupList getGroupList(String peerIpPort){
        return client.getGroupList(peerIpPort);
    }


    /**
     * 获取指定节点指定群组连接的节点列表
     * @param
     *     peerIpPort: 被查询的节点的IP:Port
     * @return QueryGroupStatus: 被查询的群组状态
     */
    public GroupPeers getGroupPeers(String peerIpPort){
        return client.getGroupPeers(peerIpPort);
    }


    /**
     * 获取指定节点的网络连接信息
     * @param
     *     endpoint: 被查询的节点的IP:Port
     * @return Peers: 指定节点的网络连接信息
     */
    public Peers getPeers(String endpoint){
        return client.getPeers(endpoint);
    }


    /**
     * 获取指定节点连接的节点列表
     * @param
     *     endpoint: 被查询的节点的IP:Port
     * @return NodeIDList: 指定节点连接的节点列表
     */
    public NodeIDList getNodeIDList(String endpoint){
        return client.getNodeIDList(endpoint);
    }

    /**
     * 获取Client对应群组的观察节点列表
     * @return SealerList: 观察节点列表
     */
    public SealerList getSealerList(){
        return client.getSealerList();
    }

    /**
     * 节点使用PBFT共识算法时，获取PBFT视图信息
     * @return PbftView: PBFT视图信息
     */
    public PbftView getPbftView(){
        return client.getPbftView();
    }

    /**
     * 获取节点版本信息
     * @param  ipAndPort:请求发送的目标节点，包括IP:Port信息。
     * @return NodeVersion: 查询获取的节点版本信息
     */
    public NodeVersion getNodeVersion(String ipAndPort){
        return client.getNodeVersion(ipAndPort);
    }


    /**
     * 获取节点的NodeID，Topic等信息
     * @param  ipAndPort:请求发送的目标节点，包括IP:Port信息。
     * @return NodeInfo: 查询获取的节点版本信息
     */
    public NodeInfo getNodeInfo(String ipAndPort){
        return client.getNodeInfo(ipAndPort);
    }

    /**
     * 获取节点共识状态
     * @return ConsensusStatus: 节点共识状态
     */
    public ConsensusStatus getConsensusStatus(){
        return client.getConsensusStatus();
    }


    /**
     * 获取节点同步状态
     * @return SyncStatus: 区块链节点同步状态
     */
    public SyncStatus getSyncStatus(){
        return client.getSyncStatus();
    }

    /**
     * 获取当前群组对应的密码学接口
     * @return cryptoSuite
     */
    public CryptoSuite getCryptoSuite(){
        return client.getCryptoSuite();
    }

    public TransactionResponse decodeReceiptStatus(TransactionReceipt receipt){
        TransactionDecoderInterface decoder = new TransactionDecoderService(getCryptoSuite());
        return decoder.decodeReceiptStatus(receipt);
    }

    /**
     * 编码ABI
     * @return
     */
    public Map<String, String> encodeAllFunction(){
        Map<String, String> result = new HashMap<>();
        AbiValueTypeEntity abiValueTypeEntity = new AbiValueTypeEntity();
        List<HashMap> hashMaps = JSONArray.parseArray(abi, HashMap.class);
        CryptoSuite cryptoSuite = getCryptoSuite();
        ABICodec abiCodec = new ABICodec(cryptoSuite);
        hashMaps.forEach(item->{
            if(item.get("type").toString().equals("function")){
                List<Object> argsObjects = new ArrayList<Object>();
                List<HashMap> inputs = JSONArray.parseArray(String.valueOf(item.get("inputs")), HashMap.class);
                inputs.forEach(in->{
                    argsObjects.add(abiValueTypeEntity.getValueTypeMap().get(in.get("type")));
                });
                try {
                    String e = abiCodec.encodeMethod(abi, item.get("name").toString(), argsObjects);
                    result.put(e.substring(0,10), item.get("name").toString());
                } catch (ABICodecException e) {
                    e.printStackTrace();
                }
            }
        });
        return result;
    }

    /**
     * TODO 测试函数
     */
    public String getInput(String transactionHashId,TransactionReceipt receipt ) throws ABICodecException, TransactionException, IOException {
        String abi = "[{\"constant\":false,\"inputs\":[{\"name\":\"entity\",\"type\":\"string[]\"}],\"name\":\"update\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"entity\",\"type\":\"string[]\"}],\"name\":\"insert\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"number\",\"type\":\"string\"}],\"name\":\"selectByNumber\",\"outputs\":[{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"number\",\"type\":\"string\"}],\"name\":\"remove\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"entity\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"count\",\"type\":\"int256\"}],\"name\":\"InsertResult\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"entity\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"count\",\"type\":\"int256\"}],\"name\":\"UpdateResult\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"entity\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"number\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"count\",\"type\":\"int256\"}],\"name\":\"RemoveResult\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"entity\",\"type\":\"string\"}],\"name\":\"SelectResult\",\"type\":\"event\"}]";

        // 获取当前群组对应的密码学接口
        CryptoSuite cryptoSuite = client.getCryptoSuite();

        List<Object> argsObjects = new ArrayList<Object>();
        List<Type> parameters = new ArrayList<>();

        FunctionEncoder functionEncoder = new FunctionEncoder(cryptoSuite);
        String s3 = functionEncoder.buildMethodSignature("insert", parameters);

        BcosTransactionReceipt transactionReceipt = client.getTransactionReceipt(transactionHashId);

        TransactionDecoderInterface decoder = new TransactionDecoderService(cryptoSuite);


        TransactionResponse transactionResponse1 = decoder.decodeReceiptStatus(receipt);

        String s1 = decoder.decodeReceiptMessage(receipt.getInput());
//        String s3 = decoder.decodeReceiptMessage(receipt.getOutput());

//        String to = receipt.getTo();
//        String s2 = decoder.decodeReceiptMessage(receipt.getLogs().get(0).getData());
        ABICodec abiCodec = new ABICodec(cryptoSuite);
        List<String> str = new ArrayList<>();
//        for(int i=0;i<13;i++){
//            str.add("60");
//        }
        argsObjects.add(str);
        String insert2 = abiCodec.encodeMethod(abi, "insert", argsObjects);


        String insert1 = abiCodec.encodeMethod(abi, "insert", argsObjects);
//        String selectByNumber = abiCodec.encodeMethod(abi, "selectByNumber", null);

//        TransactionResponse transactionResponse = decoder.decodeReceiptWithoutValues(abi, receipt);
//        BcosTransactionReceipt transactionReceipt = client.getTransactionReceipt(transactionHashId); //通过交易哈希得到交易回执

//        String s1 = decoder.decodeReceiptMessage(receipt.getTo());
//        try {
////            TransactionResponse insert = decoder.decodeReceiptWithValues(abi, "insert", receipt);
////            System.out.println(insert);
//        } catch (ABICodecException e) {
//            e.printStackTrace();
//        } catch (TransactionException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        String s = decoder.decodeReceiptMessage("0x4ed3885e000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000047465737400000000000000000000000000000000000000000000000000000000");
        return s;
    }

    public void testStatus(String blockHash, boolean signatureList, String transactionHash, TransactionReceipt receiptTest){

        // 获取当前群组对应的密码学接口
        CryptoSuite cryptoSuite = client.getCryptoSuite();
        TransactionDecoderInterface decoder = new TransactionDecoderService(cryptoSuite);

        BcosTransactionReceipt transactionReceipt1 = client.getTransactionReceipt(transactionHash);
        Optional<TransactionReceipt> transactionReceipt2 = transactionReceipt1.getTransactionReceipt();
        TransactionReceipt receipt1 = transactionReceipt2.get();


        String abi = "[{\"constant\":false,\"inputs\":[{\"name\":\"entity\",\"type\":\"string[]\"}],\"name\":\"update\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"entity\",\"type\":\"string[]\"}],\"name\":\"insert\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"number\",\"type\":\"string\"}],\"name\":\"selectByNumber\",\"outputs\":[{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"number\",\"type\":\"string\"}],\"name\":\"remove\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"count\",\"type\":\"int256\"}],\"name\":\"InsertResult\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"count\",\"type\":\"int256\"}],\"name\":\"UpdateResult\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"count\",\"type\":\"int256\"}],\"name\":\"RemoveResult\",\"type\":\"event\"}]";

        try {
            TransactionResponse incrementUint256 = decoder.decodeReceiptWithoutValues(abi, receipt1);
            TransactionResponse insert = decoder.decodeReceiptWithValues(abi, "insert", receipt1);
            TransactionResponse transactionResponse = decoder.decodeReceiptStatus(receipt1);



            TransactionResponse incrementUint2561 = decoder.decodeReceiptWithoutValues(abi, receiptTest);
            TransactionResponse insert1 = decoder.decodeReceiptWithValues(abi, "insert", receiptTest);
            TransactionResponse transactionResponse1 = decoder.decodeReceiptStatus(receiptTest);
            System.out.println(insert);
        } catch (ABICodecException e) {
            e.printStackTrace();
        } catch (TransactionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        BcosBlock blockByHash = client.getBlockByHash(blockHash, true);
        String jsonrpc = blockByHash.getJsonrpc();

        // 获取最新区块的所有交易回执信息(cient初始化过程省略，详细可以参考快速入门)
        BcosTransactionReceiptsDecoder bcosTransactionReceiptsDecoder =
                client.getBatchReceiptsByBlockHashAndRange(
                        blockHash,
                        "0",
                        "-1");
        // 解码交易回执信息
        BcosTransactionReceiptsInfo.TransactionReceiptsInfo receiptsInfo = bcosTransactionReceiptsDecoder.decodeTransactionReceiptsInfo();
        // 获取回执所在的区块信息
        BcosTransactionReceiptsInfo.BlockInfo blockInfo = receiptsInfo.getBlockInfo();
        // 获取交易回执列表
        List<TransactionReceipt> receiptList = receiptsInfo.getTransactionReceipts();
        for (TransactionReceipt receipt : receiptList) {
            System.out.println(receipt.getBlockNumber());

        }

}


}
