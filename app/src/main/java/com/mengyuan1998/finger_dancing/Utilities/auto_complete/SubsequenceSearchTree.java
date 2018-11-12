package com.mengyuan1998.finger_dancing.Utilities.auto_complete;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;


/**
 *
 */
public class SubsequenceSearchTree {
    private TreeNode root;

    public SubsequenceSearchTree(){
        root = new TreeNode("root", "root", null);

    }

    /**
     * 对于全树进行搜索，返回所有符合条件的node
     * @param target_key 目标的键值
     * @param active_nodes 需要开始进行搜索的点
     * @param multiple 如果存在多个结果,是否返回多个结果
     * @return result node, if target doesn't exist, return empty list;
     */
    public List<TreeNode> find(String target_key, List<TreeNode> active_nodes, boolean multiple){

        LinkedList<TreeNode> found_result = new LinkedList<>();
        Queue<TreeNode> seach_queue = new LinkedBlockingQueue<>();
        for(TreeNode node:active_nodes){
            seach_queue.add(node);
            while(!seach_queue.isEmpty()){
                TreeNode curr = seach_queue.remove();
                if (curr.getKey().equals(target_key)){
                    found_result.add(curr);
                    if(!multiple)
                        return found_result;
                }else {
                    seach_queue.addAll(curr.getChildren());
                }
            }
        }

        return found_result;
    }

    /**
     * wrap the @ find method, in this method only get one node.
     * given the key value, it will try to find on of the satisfy node in the tree
     * this find process has random ability
     * @param key
     * @return the node or null(doesn't find)
     */
    public TreeNode findSingleNode(String key) {
        return findSingleNode(key, root);
    }

    /**
     * wrap the @ find method, in this method only get one node.
     * @param target_key target
     * @param start_node where to start
     * @return the node or null(doesn't find)
     */
    public TreeNode findSingleNode(String target_key, TreeNode start_node){
        List<TreeNode> result = findMultipleNodes(target_key, start_node);
        if(result.size() == 0)
            return null;
        else
            return result.get(0);
    }


    /**
     * in this method, it will return all of the satisfy node
     * @param target_key key
     * @param start_node where to start
     * @return satisfy node
     */
    public List<TreeNode> findMultipleNodes(String target_key, TreeNode start_node){
        List<TreeNode> search_list = new LinkedList<>();
        search_list.add(start_node);
        List<TreeNode> result = find(target_key, search_list, true);
        return result;
    }

    /**
     * default case:
     *      set the root node as start node
     * @param target_key target
     * @return satisfy nodes
     */
    public List<TreeNode> findMultipleNodes(String target_key){
        return findMultipleNodes(target_key, root);
    }


    public TreeNode getRoot() {
        return root;
    }


    /**
     * add the sequence to the tree, first walk on the tree,
     * check whether the previous part of sequence fit the existed node
     * 2 condition:
     *      1. part of sequence doesn't satisfy:
     *          add the rest of nodes as children on the latest unsatisfied node
     *      2. all of the nodes satisfy the tree:
     *          only add the value on the last of the tree node which satisfy the sequence
     * @param seq to add
     * @param value of sequence
     */
    public void addSeqence(List<String> seq, String value){
        if(seq.size() == 0)
            return;
        TreeNode curr = root;
        //find the start node
        TreeNode next = findSingleNode(seq.get(0), curr);
        int iter_on_seq;
        if(next == null){
            iter_on_seq = 0;
            // non of part of this sequence doesn't exist in the tree
            // add from the start
        }else {
            // found the first node
            // check the each of node, whether them on the tree
            iter_on_seq = 1;
            while (iter_on_seq < seq.size()) {
                boolean found = false;
                for (TreeNode t : next.getChildren()) {
                    if (t.getKey().equals(seq.get(iter_on_seq))) {
                        curr = next;
                        next = t;
                        iter_on_seq ++;
                        found = true;
                        break;
                        // if found the node in the set of children
                        // step up and break the search on children
                    }
                }
                if (!found) {
                    curr = next;
                    break;
                    // after the children search loop, the next node haven't update
                    // means can't find satisfy node in the set of children
                    // update the curr node to the next
                }
            }

            if (iter_on_seq == seq.size()) {
                //if search process already down to the last element of sequence
                // only add the value on the last node
                next.addValue(value);
            }
        }
        // add rest of sequence as node to the tree
        while (iter_on_seq < seq.size()) {
            //start add remain node
            if (iter_on_seq != seq.size() - 1)
                curr = insertOnDedicateNode(curr, seq.get(iter_on_seq), null);
            else
                curr = insertOnDedicateNode(curr, seq.get(iter_on_seq), value);
            iter_on_seq ++;
        }
    }

    /**
     * 对某一结点作为初始点，进行一系列的sub sequence query。
     * 算法会根据seq中各个key的顺序，逐步进行sub suquence query
     * @param seqs 待搜索的序列
     * @param root_node 起始节点
     * @param exact 是否模糊搜索
     * @return 满足seq各个位置key的搜索结果
     */
    private List<List<Pair<String, TreeNode>>>
                querySequence(List<String> seqs, TreeNode root_node, boolean exact){
        List<TreeNode> active_nodes = new LinkedList<>();
        List<List<Pair<String, TreeNode>>> level_query_result = new LinkedList<>();
        active_nodes.add(root_node);
        for(String k : seqs){
            List<TreeNode> previous_active_nodes = active_nodes;
            active_nodes = find(k, active_nodes, true);
            List<Pair<String, TreeNode>> level_nodes = getAllAccessibleValue(active_nodes);
            level_query_result.add(level_nodes);

            // 如果seq中第一个key不match，此时active node 为空
            // 因此将root重新加入active node，检测第二个key是否能从根结点开始有匹配。
            if(active_nodes.size() == 0 && !exact)
                active_nodes.addAll(previous_active_nodes);
        }
        Collections.reverse(level_query_result);
        // revers it, the exact satisfy node is the first one.
        return  level_query_result;
    }


    /**
     * sub sequence search
     * 返回满足的seq的node和他的value
     * @param seqs seq
     * @param exact need to fit all keys ?
     * @return the satisfy branches' contains value node and its value
     */
    public List<Pair<String, TreeNode>> querySequence(List<String> seqs, boolean exact){
        List<List<Pair<String, TreeNode>>> all_query_res = querySequence(seqs, root, exact);
        List<Pair<String, TreeNode>> ret_res = new LinkedList<>();
        if(exact) {
            for (Pair<String, TreeNode> t : all_query_res.get(0)) {
                ret_res.add(new Pair<>(t.x, t.y));
            }
        }else {
            Set<String> book = new HashSet<>();
            for (int i = 0; i < all_query_res.size(); i++){
                for (Pair<String, TreeNode> t : all_query_res.get(i)) {
                    if(! book.contains(t.x)){
                        book.add(t.x);
                        ret_res.add(new Pair<>(t.x, t.y));
                    }
                }
            }
        }
        return ret_res;
    }


    /**
     *
     * @param seqs
     * @param exact
     * @return 满足的seq对应的value
     */
    public List<String> querySequenceValue(List<String> seqs, boolean exact){
        List<Pair<String, TreeNode>> query_res = querySequence(seqs, exact);

        List<String> ret_res = new LinkedList<>();
        for(Pair<String, TreeNode> t: query_res){
            ret_res.add(t.x);
        }
        return ret_res;
    }

    /**
     * execute the query on seq, implicit the root as start point, and only return
     * the TreeNode of each branches
     * @param seqs seq
     * @return TreeNode of branches
     */
    public List<TreeNode> querySequenceNode(List<String> seqs, boolean exact){
        List<Pair<String, TreeNode>> query_res = querySequence(seqs, exact);

        List<TreeNode> ret_res = new LinkedList<>();
        for(Pair<String, TreeNode> t: query_res){
            ret_res.add(t.y);
        }
        return ret_res;
    }




    private TreeNode insertOnDedicateNode(TreeNode dedicate_node, String Key, String value){
        return dedicate_node.addChild(Key, value);
    }

    /**
     * 自该节点向子节点遍历  输出所有可能的叶子节点上的value
     * @param query_node the parent nodes which value come from
     * @return
     */
    public List<Pair<String, TreeNode>> getAllAccessibleValue(List<TreeNode> query_node){
        List<Pair<String, TreeNode>> result = new LinkedList<>();
        Queue<TreeNode> seach_queue = new LinkedBlockingQueue<>();
        for(TreeNode node : query_node) {
            seach_queue.add(node);
            while (!seach_queue.isEmpty()) {
                TreeNode curr = seach_queue.remove();
                if(curr.isHasValue()){
                    for(String v:curr.getNodeValue())
                        result.add(new Pair<>(v, curr));
                }
                seach_queue.addAll(curr.getChildren());
            }
        }
        return result;
    }


    @Override
    public String toString() {
        return root.toString();
    }
}
