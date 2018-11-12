package com.mengyuan1998.finger_dancing.Utilities.auto_complete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * 手语句子补全
 * 单例模式 使用 getInstance获取对象 然后调用相关接口进行补全
 */
public class SentenceAutoCompleter {


    private static SentenceAutoCompleter instance = new SentenceAutoCompleter();
    private static String SENTENCE_TABLE = "[{\"value\": \"\\u8bf7\\u95ee\\u536b\\u751f\\u95f4\\u600e\\u4e48\\u8d70\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u536b\\u751f\\u95f4\", \"\\u600e\\u4e48\\u8d70\"]}, {\"value\": \"\\u8bf7\\u95ee\\u822a\\u7ad9\\u697c\\u5728\\u54ea\\u91cc\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u822a\\u7ad9\\u697c\", \"\\u5728\\u54ea\\u91cc\"]}, {\"value\": \"\\u8bf7\\u95ee\\u53d6\\u7968\\u53e3\\u5728\\u54ea\\u91cc\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u53d6\\u7968\\u53e3\", \"\\u5728\\u54ea\\u91cc\"]}, {\"value\": \"\\u8bf7\\u95ee\\u68c0\\u7968\\u53e3\\u5728\\u54ea\\u91cc\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u68c0\\u7968\\u53e3\", \"\\u5728\\u54ea\\u91cc\"]}, {\"value\": \"\\u8bf7\\u95ee\\u5728\\u54ea\\u91cc\\u5b89\\u68c0\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u5728\\u54ea\\u91cc\", \"\\u5b89\\u68c0\"]}, {\"value\": \"\\u8bf7\\u95ee\\u600e\\u4e48\\u5b89\\u68c0\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u600e\\u4e48\", \"\\u5b89\\u68c0\"]}, {\"value\": \"\\u8bf7\\u95ee\\u767b\\u673a\\u53e3\\u5728\\u54ea\\u91cc\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u767b\\u673a\\u53e3\", \"\\u5728\\u54ea\\u91cc\"]}, {\"value\": \"\\u8bf7\\u95ee\\u673a\\u7968\\u627e\\u4e0d\\u5230\\u4e86\\u600e\\u4e48\\u529e\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u673a\\u7968\", \"\\u627e\\u4e0d\\u5230\", \"\\u4e86\", \"\\u600e\\u4e48\\u529e\"]}, {\"value\": \"\\u8bf7\\u95ee\\u9519\\u8fc7\\u6211\\u7684\\u822a\\u73ed\\u600e\\u4e48\\u529e\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u9519\\u8fc7\", \"\\u6211\\u7684\", \"\\u822a\\u73ed\", \"\\u600e\\u4e48\\u529e\"]}, {\"value\": \"\\u8bf7\\u95ee\\u822a\\u73ed\\u5ef6\\u8bef\\u600e\\u4e48\\u529e\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u822a\\u73ed\", \"\\u5ef6\\u8bef\", \"\\u600e\\u4e48\\u529e\"]}, {\"value\": \"\\u8bf7\\u95ee\\u822a\\u73ed\\u5ef6\\u8bef\\u4e86\\u591a\\u4e45\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u822a\\u73ed\", \"\\u5ef6\\u8bef\", \"\\u4e86\", \"\\u591a\\u4e45\"]}, {\"value\": \"\\u8bf7\\u95ee\\u5728\\u54ea\\u91cc\\u6258\\u8fd0\\u884c\\u674e\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u5728\\u54ea\\u91cc\", \"\\u6258\\u8fd0\", \"\\u884c\\u674e\"]}, {\"value\": \"\\u8bf7\\u95ee\\u884c\\u674e\\u6258\\u8fd0\\u600e\\u4e48\\u529e\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u884c\\u674e\", \"\\u6258\\u8fd0\", \"\\u600e\\u4e48\", \"\\u529e\"]}, {\"value\": \"\\u8bf7\\u95ee\\u6258\\u8fd0\\u884c\\u674e\\u8d85\\u91cd\\u4e86\\u600e\\u4e48\\u529e\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u6258\\u8fd0\", \"\\u884c\\u674e\", \"\\u8d85\\u91cd\", \"\\u4e86\", \"\\u600e\\u4e48\", \"\\u529e\"]}, {\"value\": \"\\u8bf7\\u95ee\\u4ec0\\u4e48\\u4e0d\\u80fd\\u6258\\u8fd0\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u4ec0\\u4e48\", \"\\u4e0d\\u80fd\", \"\\u6258\\u8fd0\"]}, {\"value\": \"\\u8bf7\\u95ee\\u529e\\u7406\\u767b\\u673a\\u624b\\u7eed\\u9700\\u8981\\u4ec0\\u4e48\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u529e\\u7406\", \"\\u767b\\u673a\\u624b\\u7eed\", \"\\u9700\\u8981\", \"\\u4ec0\\u4e48\"]}, {\"value\": \"\\u8bf7\\u95ee\\u767b\\u673a\\u9700\\u8981\\u4ec0\\u4e48\\u8bc1\\u4ef6\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u767b\\u673a\", \"\\u9700\\u8981\", \"\\u4ec0\\u4e48\", \"\\u8bc1\\u4ef6\"]}, {\"value\": \"\\u8bf7\\u95ee\\u8eab\\u4efd\\u8bc1\\u627e\\u4e0d\\u5230\\u4e86\\u600e\\u4e48\\u529e\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u8eab\\u4efd\\u8bc1\", \"\\u627e\", \"\\u4e0d\\u5230\", \"\\u4e86\", \"\\u600e\\u4e48\", \"\\u529e\"]}, {\"value\": \"\\u8bf7\\u95ee\\u822a\\u73ed\\u600e\\u4e48\\u6539\\u7b7e\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u822a\\u73ed\", \"\\u600e\\u4e48\", \"\\u6539\\u7b7e\"]}, {\"value\": \"\\u8bf7\\u95ee\\u600e\\u4e48\\u9000\\u673a\\u7968\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u600e\\u4e48\", \"\\u9000\", \"\\u673a\\u7968\"]}, {\"value\": \"\\u8bf7\\u95ee\\u4e3a\\u4ec0\\u4e48\\u6ca1\\u6536\\u6211\\u7684\\u6253\\u706b\\u673a\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u4e3a\\u4ec0\\u4e48\", \"\\u6ca1\\u6536\", \"\\u6211\\u7684\", \"\\u6253\\u706b\\u673a\"]}, {\"value\": \"\\u8bf7\\u95ee\\u4e3a\\u4ec0\\u4e48\\u6ca1\\u6536\\u6211\\u7684\\u9999\\u6c34\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u4e3a\\u4ec0\\u4e48\", \"\\u6ca1\\u6536\", \"\\u6211\\u7684\", \"\\u9999\\u6c34\"]}, {\"value\": \"\\u8bf7\\u95ee\\u4e3a\\u4ec0\\u4e48\\u6ca1\\u6536\\u6211\\u7684\\u6c34\\u996e\\u6599\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u4e3a\\u4ec0\\u4e48\", \"\\u6ca1\\u6536\", \"\\u6211\\u7684\", \"\\u6c34\", \"\\u996e\\u6599\"]}, {\"value\": \"\\u8bf7\\u95ee\\u4e3a\\u4ec0\\u4e48\\u5145\\u7535\\u5b9d\\u4e0d\\u80fd\\u6258\\u8fd0\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u4e3a\\u4ec0\\u4e48\", \"\\u5145\\u7535\\u5b9d\", \"\\u4e0d\\u80fd\", \"\\u6258\\u8fd0\"]}, {\"value\": \"\\u8bf7\\u95ee\\u5316\\u5986\\u54c1\\u4e3a\\u4ec0\\u4e48\\u4e0d\\u80fd\\u5e26\\u4e0a\\u98de\\u673a\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u5316\\u5986\\u54c1\", \"\\u4e3a\\u4ec0\\u4e48\", \"\\u4e0d\\u80fd\", \"\\u5e26\\u4e0a\", \"\\u98de\\u673a\"]}, {\"value\": \"\\u8bf7\\u95ee\\u6258\\u8fd0\\u884c\\u674e\\u91cd\\u91cf\\u7684\\u8981\\u6c42\\u662f\\u4ec0\\u4e48\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u6258\\u8fd0\", \"\\u884c\\u674e\", \"\\u91cd\\u91cf\", \"\\u7684\", \"\\u8981\\u6c42\", \"\\u662f\", \"\\u4ec0\\u4e48\"]}, {\"value\": \"\\u8bf7\\u95ee\\u767b\\u673a\\u53e3\\u4e3a\\u4ec0\\u4e48\\u4f1a\\u53d8\\u66f4\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u767b\\u673a\\u53e3\", \"\\u4e3a\\u4ec0\\u4e48\", \"\\u4f1a\", \"\\u53d8\\u66f4\"]}, {\"value\": \"\\u8bf7\\u95ee\\u98de\\u673a\\u600e\\u4e48\\u9009\\u5ea7\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u98de\\u673a\", \"\\u600e\\u4e48\", \"\\u9009\\u5ea7\"]}, {\"value\": \"\\u8bf7\\u95ee\\u8fc7\\u4e86\\u767b\\u673a\\u53e3\\u4e4b\\u540e\\u600e\\u4e48\\u8d70\\uff1f\", \"keys\": [\"\\u8bf7\\u95ee\", \"\\u8fc7\", \"\\u4e86\", \"\\u767b\\u673a\\u53e3\", \"\\u4e4b\\u540e\", \"\\u600e\\u4e48\", \"\\u8d70\"]}]";
    private SubsequenceSearchTree tree;

    private SentenceAutoCompleter() {
        //load the dict to the tree
        tree = new SubsequenceSearchTree();

        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(SENTENCE_TABLE);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject e = jsonArray.getJSONObject(i);
                String v = e.getString("value");
                JSONArray keys = e.getJSONArray("keys");
                List<String> key_list = new LinkedList<>();
                for (int j = 0; j < keys.length(); j++) {
                    key_list.add(keys.getString(j));
                }
                tree.addSeqence(key_list, v);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static SentenceAutoCompleter getInstance() {
        return instance;
    }

    /**
     * 执行query
     * 传入key sequence(当前识别出来的手语词)，经过匹配后返回满足条件的补全后的句子
     *
     * @param keys  待补全的序列
     * @param exact 是否保证完全匹配识别句子
     * @return 匹配补全后的句子，如果不设置为exact为true则会返回按照匹配程度自大到小排列的补全结果
     */
    public List<String> executeValueQuery(List<String> keys, boolean exact) {
        return tree.querySequenceValue(keys, exact);
    }

    @Override
    public String toString() {
        return "Subsequence Tree:\n" + tree.toString();
    }
}
