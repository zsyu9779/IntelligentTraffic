package com.manager.traffic.dao;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HbaseUtils {
	static Configuration config = null;
	static {
		config = HBaseConfiguration.create();
		config.set("hbase.zookeeper.quorum", "slave1,slave2,slave3");
		config.set("hbase.zookeeper.property.clientPort", "2181");
	}
	public void createNameSpace(String tableName) throws MasterNotRunningException, ZooKeeperConnectionException, IOException{
		Configuration conf = HBaseConfiguration.create();
		HBaseAdmin admin = new HBaseAdmin(conf);
		//create namespace named "ns"
		admin.createNamespace(NamespaceDescriptor.create("ns").build());
		HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf("lap:"+tableName));
		admin.close();
	}
	public void createTable(String tableName, String[] familys) {
		try {
			HBaseAdmin admin = new HBaseAdmin(config);
			if (admin.tableExists(tableName)) {
				System.out.println(tableName
						+ " is already exists,Please create another table!");
			} else {
				HTableDescriptor desc = new HTableDescriptor(tableName);
				for (int i = 0; i < familys.length; i++) {
					HColumnDescriptor family = new HColumnDescriptor(familys[i]);
					family.setBlockCacheEnabled(true);
					family.setMaxVersions(5);
					family.setInMemory(true);
					desc.addFamily(family);
				}
				admin.createTable(desc);
				System.out.println("Create table \'" + tableName + "\' OK!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertData(String tableName, String rowKey, String family,
			String qualifier, String value) {
		HTableInterface table = null;
		try {
			HTablePool pool = new HTablePool(config, 1000);
			table = pool.getTable(tableName);
			
			List rows=new ArrayList();
			Put put = new Put(Bytes.toBytes(rowKey));
			put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier),
					Bytes.toBytes(value));
			put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier),
					Bytes.toBytes(value));
			Put put1 = new Put(Bytes.toBytes(rowKey));
			put1.add(Bytes.toBytes(family), Bytes.toBytes(qualifier),
					Bytes.toBytes(value));
			put1.add(Bytes.toBytes(family), Bytes.toBytes(qualifier),
					Bytes.toBytes(value));
			rows.add(put1);
			table.put(rows);
			System.out.println("insert a data successful!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				table.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteData(String tableName, String rowKey) {
		HTableInterface table = null;
		try {
			HTablePool pool = new HTablePool(config, 1000);
			table = pool.getTable(tableName);
			
			
			Delete del = new Delete(Bytes.toBytes(rowKey));
			table.delete(del);
			System.out.println("delete a data successful");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public Map<String,String> selectData(String tableName, String rowKey) {
		HTableInterface table = null;
		//String pwd = null;
		Map<String,String> map = null;
		try {

			HConnection hc = HConnectionManager.createConnection(config);
			table = hc.getTable(tableName);
			Result result = table.get(new Get(Bytes.toBytes(rowKey)));
			if (result.toString().equals("keyvalues=NONE")) {
				return map;
			}
			else{
				for (KeyValue kv : result.raw()) {
					//if (new String(kv.getQualifier()).equals("pwd"))
						//pwd =new String(kv.gnew String(kv.getValue()etValue());
						map = new HashMap<>();
						map.put(new String(kv.getQualifier()),new String(kv.getValue()));

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}




	public static void main(String[] args) {
		HbaseUtils ht = new HbaseUtils();
//		ht.createTable("htest", new String[] { "fcol1", "fcol2" });
//		ht.createTableSplit("htest", new String[]{"fcol1","fcol2"});
//		ht.createTableSplit2("htest", new String[]{"fcol1","fcol2"});
//		ht.deleteTable("htest");
//		for (int i = 0; i < 10000000; i++) {
//			ht.insertData("htest", "a"+i, "fcol1", "c1", "aaa"+i);
//			ht.insertData("htest", "a"+i, "fcol1", "c2", "bbb"+i);
//		}
//		ht.deleteData("htest", "a1");
//		ht.queryByRowKey("htest", "a1");
//				ht.queryAll("htest");
//		List list=new ArrayList();
//		list.add("fcol1,c1,aaa1");
//		list.add("fcol1,c1,aaa2");
//		list.add("fcol1,c1,aaa3");
//		ht.selectByFilter("htest",list );
	}
}
