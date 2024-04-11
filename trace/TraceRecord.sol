pragma solidity >=0.4.24 <0.6.11;
pragma experimental ABIEncoderV2;

import "./Table.sol";

contract TraceRecord {

    event InsertResult(string entity, int256 count);
    event test(string str, int256 begintime, int endtime);
    event UpdateResult(string entity, int256 count);
    event RemoveResult(string entity, string number, int256 count);
    event SelectResult(string entity);
    // 1654419600000, 1654592400000

    TableFactory tableFactory;
    KVTableFactory kvtableFactory;
    string RECORD_TABLE = "operation_record_test1";
    string USER_TABLE = "user_operation_record_test1";
    string RESOURCE_TABLE = "resource_operation_record_test1";
    string INDEX_TABLE = "index_table";

    uint index = 0;
    uint MAXPAGE = 150;
    uint i = 0;

    int begin;
    int number;
    int beginTime;
    int endTime;

    string[] recordTableProperties = ["operationRecordIndex", "userID", "resourceID", "value", "operation", "txBlockNum", "txHash", "time"];
    string[] userTableProperties = ["userID", "operationRecordIndex","time"];
    string[] resourceTableProperties = ["resourceID", "operationRecordIndex", "time"];
    constructor() public {
        tableFactory = TableFactory(0x1001);
        kvtableFactory = KVTableFactory(0x1010);
        tableFactory.createTable(RECORD_TABLE, "operationRecordIndex", "userID, resourceID, value, operation, txBlockNum, txHash, time");
        tableFactory.createTable(USER_TABLE, "userID", "operationRecordIndex, time");
        tableFactory.createTable(RESOURCE_TABLE, "resourceID", "operationRecordIndex, time");
        //        kvtableFactory.createTable(INDEX_TABLE, "key", "value");
        //        KVTable kvtable = kvtableFactory.openTable(INDEX_TABLE);
        //        Entry test;
        //        bool ok;
        //        (ok, test) = kvtable.get("index");
        //        if (ok) {
        //            index = uint(test.getInt("value"));
        //        } else {
        //            Entry test = kvtable.newEntry();
        //            test.set("key", "index");
        //            test.set("value", uint(0));
        //            kvtable.set("index", test);
        //        }
    }


    //    function defaultRecord() public view returns(uint) {
    //        return index;
    //    }
    //
    //    function queryByIndex(string index) public
    //    returns(string memory userID, string memory resID, string memory value, string memory operation, string memory txBlockNum, string memory txHash) {
    //        Table table = tableFactory.openTable(RECORD_TABLE);
    //        Condition cond = table.newCondition();
    //        Entry entry = table.select(index, cond).get(0);
    //        return (entry.getString(recordTableProperties[1]),
    //                entry.getString(recordTableProperties[2]),
    //                entry.getString(recordTableProperties[3]),
    //                entry.getString(recordTableProperties[4]),
    //                entry.getString(recordTableProperties[5]),
    //                entry.getString(recordTableProperties[6]),
    //                );
    //
    //    }

    function getEntryByIndex(uint index) public view returns (Entry) {
        Table table = tableFactory.openTable(RECORD_TABLE);
        Condition cond = table.newCondition();
        Entry entry = table.select(toString(index), cond).get(0);
        return entry;
    }


    function defaultRecord(int[] nums)
    public
    view
    returns (string[] memory userIDs, string[] memory resourceIDs, string[] memory values, string[] memory operations, string[] memory txBlockNums, string[] memory txHashs, uint totalNumber) {
        begin = nums[0];
        if (index > uint(begin)) {
            i = index - 1 - uint(begin);
        } else {
            string[] memory res = new string[](0);
            return (res, res, res, res, res, res, uint(0));
        }
        uint num = nums[1] < 0 ? 0 : uint(nums[1]);
        int tempIndex = int(index) - 1;
        userIDs = new string[](index < num ? index : num);
        resourceIDs = new string[](index < num ? index : num);
        values = new string[](index < num ? index : num);
        operations = new string[](index < num ? index : num);
        txBlockNums = new string[](index < num ? index : num);
        txHashs = new string[](index < num ? index : num);
        // i 代表所取表项在表中的index值
        // tempIndex 为了保证要么取得index个或取得num个，看两个人中较小的那个
        uint j = 0;
        for (j = 0; j < num; i--) {
            if (tempIndex < 0) {
                break;
            }
            Entry entry = getEntryByIndex(i);
            if (entry.getInt("time") > nums[2] && entry.getInt("time") < nums[3]) {
                userIDs[j] = entry.getString(recordTableProperties[1]);
                resourceIDs[j] = (entry.getString(recordTableProperties[2]));
                values[j] = (entry.getString(recordTableProperties[3]));
                operations[j] = (entry.getString(recordTableProperties[4]));
                txBlockNums[j] = (entry.getString(recordTableProperties[5]));
                txHashs[j] = (entry.getString(recordTableProperties[6]));
                tempIndex = tempIndex - 1;
                j++;
            }
            if (i == 0) {
                break;
            }
        }
        totalNumber = j;
    }

    function recordOperation(string memory userID, string memory resourceID, string memory value, string memory operation) public returns (uint) {
        Table table = tableFactory.openTable(RECORD_TABLE);
        Entry entry = table.newEntry();
        entry.set("operationRecordIndex", index);
        entry.set("userID", userID);
        entry.set("resourceID", resourceID);
        entry.set("value", value);
        entry.set("operation", operation);
        entry.set("time", int(block.timestamp));
        int res = table.insert(toString(index), entry);
        emit InsertResult("insert", int(block.timestamp));

        Table userTable = tableFactory.openTable(USER_TABLE);
        Entry userEntry = userTable.newEntry();
        userEntry.set("userID", userID);
        userEntry.set("operationRecordIndex", index);
        userEntry.set("time", int(block.timestamp));
        userTable.insert(userID, userEntry);


        Table resTable = tableFactory.openTable(RESOURCE_TABLE);
        Entry resEntry = resTable.newEntry();
        resEntry.set(resourceTableProperties[0], resourceID);
        resEntry.set(resourceTableProperties[1], index);
        resEntry.set(resourceTableProperties[2], int(block.timestamp));
        resTable.insert(resourceID, resEntry);
        index = index + 1;

        //        KVTable kvtable = kvtableFactory.openTable(INDEX_TABLE);
        //        Entry test = kvtable.newEntry();
        //        test.set("key", "index");
        //        test.set("value", index);
        //        kvtable.set("index", test);
        return index - 1;
    }

    function queryOperationByUserID(string memory userID, int[] page)
    view
    public
    returns (string[] memory resourceIDs, string[] memory values, string[] memory operations, string[] memory txBlockNums, string[] memory txHashs) // resourceIDs, values, operations,
    {
        begin = page[0];
        number = page[1];
        Entries entries = getUserEntries(userID, page[2], page[3]);
        if(begin > entries.size()) {
            string[] memory res = new string[](0);
            return (res, res, res, res, res);
        }
        resourceIDs = new string[](uint256(entries.size()));
        values = new string[](uint256(entries.size()));
        operations = new string[](uint256(entries.size()));
        txBlockNums = new string[](uint256(entries.size()));
        txHashs = new string[](uint256(entries.size()));
        Condition recordCondition = tableFactory.openTable(RECORD_TABLE).newCondition();
        uint count = 0;
        uint tempCount;
        if (begin + number > entries.size()) {
            tempCount = uint(entries.size());
        } else {
            tempCount = uint(number);
        }
        for (i =uint(entries.size() - begin - 1); count < tempCount; --i) {
            Entry tempRecord = getRecordEntry(entries.get(int(i)).getString(userTableProperties[1]));
            resourceIDs[count] = tempRecord.getString(recordTableProperties[2]);
            values[count] = tempRecord.getString(recordTableProperties[3]);
            operations[count] = tempRecord.getString(recordTableProperties[4]);
            txBlockNums[count] = tempRecord.getString(recordTableProperties[5]);
            txHashs[count] = tempRecord.getString(recordTableProperties[6]);
            count += 1;
            if (i == 0) {
                break;
            }
        }
        resourceIDs = arraySlice(resourceIDs, 0, count);
        values = arraySlice(values, 0, count);
        operations = arraySlice(operations, 0, count);
        txBlockNums = arraySlice(txBlockNums, 0, count);
        txHashs = arraySlice(txHashs, 0, count);
    }

    function queryOperationByResourceID(string memory resourceID, int[] page) public view
    returns (string[] memory userIDs, string[] memory values, string[] memory operations, string[] memory txBlockNums, string[] memory txHashs) {
        begin = page[0];
        number = page[1];
        Entries entries = getResourceEntries(resourceID, page[2], page[3]);
        if(begin > entries.size()) {
            string[] memory res = new string[](0);
            return (res, res, res, res, res);
        }
        userIDs = new string[](uint256(entries.size()));
        values = new string[](uint256(entries.size()));
        operations = new string[](uint256(entries.size()));
        txBlockNums = new string[](uint256(entries.size()));
        txHashs = new string[](uint256(entries.size()));
        uint count = 0;
        uint tempCount;
        if (begin + number > entries.size()) {
            tempCount = uint(entries.size());
        } else {
            tempCount = uint(number);
        }
        for (i =uint(entries.size() - begin - 1); count < tempCount; --i) {
            Entry tempRecord = getRecordEntry(entries.get(int(i)).getString(resourceTableProperties[1]));
            userIDs[count] = tempRecord.getString(recordTableProperties[1]);
            values[count] = tempRecord.getString(recordTableProperties[3]);
            operations[count] = tempRecord.getString(recordTableProperties[4]);
            txBlockNums[count] = tempRecord.getString(recordTableProperties[5]);
            txHashs[count] = tempRecord.getString(recordTableProperties[6]);
            count += 1;
            if (i == 0) {
                break;
            }
        }
        userIDs = arraySlice(userIDs, 0, count);
        values = arraySlice(values, 0, count);
        operations = arraySlice(operations, 0, count);
        txBlockNums = arraySlice(txBlockNums, 0, count);
        txHashs = arraySlice(txHashs, 0, count);
    }

    function arraySlice(string[] memory array, uint begin, uint end) public pure returns (string[] memory) {
        string[] memory res = new string[](uint256(end - begin));

        for (uint i = begin; i < end; i++) {
            res[i] = array[i];
        }
        return res;
    }


    function writeTxBlockNumAndTxHash(string memory index, string memory blockNum, string memory txHash) public {
        Table table = tableFactory.openTable(RECORD_TABLE);
        Condition condition = table.newCondition();
        Entry entry = table.select(index, condition).get(0);
        entry.set(recordTableProperties[5], blockNum);
        entry.set(recordTableProperties[6], txHash);
        int res = table.update(index, entry, condition);
        emit UpdateResult("update", res);
    }

    function getUserEntries(string memory userID, int beginTime, int endTime) public view returns (Entries) {
        Table table = tableFactory.openTable(USER_TABLE);
        Condition condition = table.newCondition();
        condition.GE("time", beginTime);
        condition.LE("time", endTime);
        Entries res = table.select(userID, condition);
        return res;
    }

    function getResourceEntries(string memory resourceID, int beginTime, int endTime) public view returns (Entries) {
        Table table = tableFactory.openTable(RESOURCE_TABLE);
        Condition condition = table.newCondition();
        condition.GE("time", beginTime);
        condition.LE("time", endTime);
        Entries res = table.select(resourceID, condition);
        return res;
    }

    function getEntriesSize(string memory ID, int beginTime, int endTime, int method) public view returns (int) {
        Table table;
        if (method == 0) {
            table = tableFactory.openTable(RESOURCE_TABLE);
        } else {
            table = tableFactory.openTable(USER_TABLE);
        }
        Condition cond = table.newCondition();
        cond.GE("time", beginTime);
        cond.LE("time", endTime);
        return table.select(ID, cond).size();
    }

    function getRecordEntry(string memory index) public view returns (Entry) {
        Table table = tableFactory.openTable(RECORD_TABLE);
        Condition condition = table.newCondition();
        Entry res = table.select(index, condition).get(0);
        return res;
    }

    function toString(uint256 value) private returns (string memory) {
        // Inspired by OraclizeAPI's implementation - MIT licence
        // https://github.com/oraclize/ethereum-api/blob/b42146b063c7d6ee1358846c198246239e9360e8/oraclizeAPI_0.4.25.sol

        if (value == 0) {
            return "0";
        }
        uint256 temp = value;
        uint256 digits;
        while (temp != 0) {
            digits++;
            temp /= 10;
        }
        bytes memory buffer = new bytes(digits);
        while (value != 0) {
            digits -= 1;
            buffer[digits] = bytes1(uint8(48 + uint256(value % 10)));
            value /= 10;
        }
        return string(buffer);
    }

}


