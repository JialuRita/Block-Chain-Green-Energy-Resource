package io.renren.modules.blockchain.client;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.renren.modules.blockchain.common.CommonClient;
import io.renren.modules.blockchain.contract.TraceRecord;
import io.renren.modules.blockchain.entity.RecordResponseEntity;
import io.renren.modules.blockchain.utils.SpringUtils;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple5;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple6;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple7;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.fisco.bcos.web3j.tx.txdecode.ResultEntity;
import org.fisco.bcos.web3j.tx.txdecode.TransactionDecoder;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class TraceRecordClient extends CommonClient implements ApplicationRunner {
    private TraceRecord traceRecord;

    public TraceRecordClient() {
        traceRecord = (TraceRecord) getContractMap("TraceRecord");
        //initABI();
    }

    @Override
    public void remove(String name) {

    }

    public String getAllNumber(String ID, String beginTime, String endTime, int method) throws ContractException {
        if (traceRecord == null) {
            traceRecord = (TraceRecord) getContractMap("TraceRecord");
        }
        return traceRecord.getEntriesSize(ID, new BigInteger(beginTime), new BigInteger(endTime), new BigInteger(String.valueOf(method))).toString();
    }

    public TransactionReceipt traceInfoRecord(String uID, String resID, String time, String operation) {
        if (traceRecord == null) {
            traceRecord = (TraceRecord) getContractMap("TraceRecord");
        }
        // TODO
        // 回掉一个方法，将交易所在区块的高度和交易回执的hash值存入链中。
        TransactionReceipt res = traceRecord.recordOperation(uID, resID, time, operation);
        if (res != null) {
            callBack(res);
        }

        return res;
    }

    public void callBack(TransactionReceipt transactionReceipt) {
        TransactionDecoder decoder = new TransactionDecoder(TraceRecord.ABI);
        try {
            String index = decoder.decodeOutputReturnObject(transactionReceipt.getInput(), transactionReceipt.getOutput()).getResult().get(0).getData().toString();
            System.out.println("recordIndex: " + index);
            traceRecord.writeTxBlockNumAndTxHash(index, transactionReceipt.getBlockNumber(), transactionReceipt.getTransactionHash());
        } catch (BaseException e) {
            e.printStackTrace();
        }
    }

    public List<RecordResponseEntity> getEntitiesRes(int method, String ID, String begin, String number, String beginTime, String endTime) {
        if (traceRecord == null) {
            traceRecord = (TraceRecord) getContractMap("TraceRecord");
        }
        System.out.println("beginTime: " + beginTime);
        System.out.println("endTime: " + endTime);
        Tuple5<List<String>, List<String>, List<String>, List<String>, List<String>> temp = null;
        List<RecordResponseEntity> res = new ArrayList<>();
        try {
            if (method == 0) {
                // resID
                temp = traceRecord.queryOperationByResourceID(ID, new ArrayList<>(Arrays.asList(
                        new BigInteger(begin), new BigInteger(number), new BigInteger(beginTime), new BigInteger(endTime))));
            } else if (method == 1) {
                // userID
                temp = traceRecord.queryOperationByUserID(ID, new ArrayList<>(Arrays.asList(
                        new BigInteger(begin), new BigInteger(number), new BigInteger(beginTime), new BigInteger(endTime))));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        assert temp != null;
        int len = temp.getValue1().size();
        for (int i = 0; i < len; i++) {
            RecordResponseEntity resEntity = new RecordResponseEntity();
            if (method == 0) {
                resEntity.setResID(ID);
                resEntity.setUserID(temp.getValue1().get(i));
            } else {
                resEntity.setUserID(ID);
                resEntity.setResID(temp.getValue1().get(i));
            }
            resEntity.setTime((temp.getValue2().get(i)));
            resEntity.setOperation(temp.getValue3().get(i));
            resEntity.setTxBlockNum(temp.getValue4().get(i));
            resEntity.setTxHash(temp.getValue5().get(i));
            res.add(resEntity);
        }
        return res;
    }


    public List<RecordResponseEntity> getRecentTraceRecord(String begin, String num, String beginTime, String endTime) throws ContractException {
        if (traceRecord == null) {
            traceRecord = (TraceRecord) getContractMap("TraceRecord");
        }
        List<RecordResponseEntity> res = new ArrayList<>();
        Tuple7<List<String>, List<String>, List<String>, List<String>, List<String>, List<String>, BigInteger > temp;
        try {
            if (Integer.parseInt(begin) < 0 || Integer.parseInt(num) < 0) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        temp = traceRecord.defaultRecord(new ArrayList<>(Arrays.asList(
                new BigInteger(begin), new BigInteger(num), new BigInteger(beginTime), new BigInteger(endTime))));
        int len = temp.getValue1().size();
        for (int i = 0; i < len; i++) {
            if (temp.getValue1().get(i).toString().equals("")) {
                continue;
            }
            RecordResponseEntity resEntity = new RecordResponseEntity();

            resEntity.setUserID(temp.getValue1().get(i));
            resEntity.setResID(temp.getValue2().get(i));
            resEntity.setTime((temp.getValue3().get(i)));
            resEntity.setOperation(temp.getValue4().get(i));
            resEntity.setTxBlockNum(temp.getValue5().get(i));
            resEntity.setTxHash(temp.getValue6().get(i));
            res.add(resEntity);
//            List<ResultEntity> tempRes = decodeInputAndOutput(tes).get("output").getResult();
//            RecordResponseEntity recordResponseEntity = new RecordResponseEntity(
//                    String.valueOf(idNow),
//                    temp.getValue1(),
//                    temp.getValue2(),
//                    temp.getValue3(),
//                    temp.getValue4(),
//                    temp.getValue5(),
//                    temp.getValue6());
        }
        res.add(new RecordResponseEntity(temp.getValue7().toString()));
        return res;
    }


    public Map<String, InputAndOutputResult> decodeInputAndOutput (TransactionReceipt receipt) throws BaseException {
        // 通过web3SDK获得解析input,output的json格式
        TransactionDecoder decoder = new TransactionDecoder(TraceRecord.ABI);
        Map<String, InputAndOutputResult> txResTemp = new HashMap<>();
        InputAndOutputResult outputTemp = decoder.decodeOutputReturnObject(receipt.getInput(), receipt.getOutput());
        InputAndOutputResult inputTemp = decoder.decodeInputReturnObject(receipt.getInput());
        // 将返回值的类型添加到function的定义中
        StringBuilder strb = new StringBuilder(outputTemp.getFunction());
        strb.append(" returns(");
        strb.append(outputTemp.getResult().stream().map(ResultEntity::getType).collect(Collectors.joining(",")));
        strb.append(")");
        outputTemp.setFunction(strb.toString());
        inputTemp.setFunction(strb.toString());
        txResTemp.put("output", outputTemp);
        txResTemp.put("input", inputTemp);
        return txResTemp;
    }

//    public String transferData(String timeTemp) {
//        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(timeTemp)*1000));
//    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        BcosSDK sdk = SpringUtils.getBean("bcosSDK");
        deploy("TraceRecord", TraceRecord.class, sdk, 1);
    }
}
