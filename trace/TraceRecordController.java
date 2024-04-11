package io.renren.modules.blockchain.controller;

import cn.hutool.json.JSONUtil;
import io.renren.common.utils.R;
import io.renren.modules.blockchain.client.BlockchainClient;
import io.renren.modules.blockchain.client.TraceRecordClient;
import io.renren.modules.blockchain.entity.RecordRequestEntity;
import io.renren.modules.blockchain.entity.RecordResponseEntity;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/blockchain/lsp")
public class TraceRecordController {

    @Autowired
    private TraceRecordClient traceRecordClient;

    @Autowired
    private BlockchainClient client;

    @PostMapping("/open/trace/traceInfoRecord")
    public R traceRecord(@RequestBody RecordRequestEntity request) {
        Map<String, Object> result = new HashMap<>();
        for (String resID : request.getResID()) {
            traceRecordClient.traceInfoRecord(request.getUserID(), resID, request.getUpChainData(), request.getOperation().toString());
        }
        System.out.println(request);
        result.put("success", "true");
        return R.ok(result);
    }

    @GetMapping("/open/trace/traceByUserID")
    public R traceByUserID(@RequestParam Map<String, Object> params) throws ContractException {
        Map<String, Object> result = new HashMap<>();
        result.put("userList",traceRecordClient.getEntitiesRes(1, (String)params.get("userID"),
                (String)params.get("begin"), (String) params.get("number"), (String) params.get("beginTime"), ((String)params.get("endTime"))));
        result.put("totalNum", traceRecordClient.getAllNumber( (String)params.get("userID"), (String) params.get("beginTime"), ((String)params.get("endTime")), 1));
        result.put("success", "true");
        return R.ok(result);
    }

    @GetMapping("/open/trace/traceByResourceID")
    public R traceByResourceID(@RequestParam Map<String, Object> params) throws ContractException {
        Map<String, Object> result = new HashMap<>();
        result.put("resList",traceRecordClient.getEntitiesRes(0, (String)params.get("resID"),
                (String)params.get("begin"), (String) params.get("number"), (String) params.get("beginTime"), ((String)params.get("endTime"))));
        result.put("totalNum", traceRecordClient.getAllNumber((String)params.get("resID"), (String) params.get("beginTime"), ((String)params.get("endTime")),0));
        result.put("success", "true");
        return R.ok(result);
    }

    @GetMapping("/open/trace/defaultTrace")
    public R defaultTrace(@RequestParam Map<String, Object> params) throws ContractException {
        Map<String, Object> result = new HashMap<>();
        List<RecordResponseEntity> temp = traceRecordClient.getRecentTraceRecord(
                (String)params.get("begin"),
                ((String) params.get("num")),
                ((String) params.get("beginTime")),
                ((String) params.get("endTime")));
        result.put("defaultList", temp.subList(0, temp.size() - 1));
        result.put("totalNum", temp.get(temp.size() - 1).getIndex());
        return R.ok(result);
        // 获得了最近的
    }

}
