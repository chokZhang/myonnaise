package com.mengyuan1998.finger_dancing.Utilities.auto_complete;



import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * the node on the search tree
 * 1 node -> 0-n children
 *        -> 0-m values
 *        -> 1 parent
 *        -> 1 key
 */
public class TreeNode {
    private String key;
    private List<String> value;
    private List<TreeNode> children;
    private TreeNode parent;

    public TreeNode(String keys, String value, TreeNode parent){
        this.key = keys;
        this.value = new LinkedList<>();
        if(value != null){
            this.value.add(value);
        }
        this.parent = parent;
        children = new LinkedList<>();
    }

    public List<String> getNodeValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public TreeNode getParentNode() {
        return parent;
    }

    public List<TreeNode> getChildren() {
        // keep shuffle maintain the random for tree depth balance
        Collections.shuffle(children);
        return children;
    }

    public boolean isHasValue(){
        return value.size() != 0;
    }

    public boolean isHasChildren(){
        return children.size() != 0;
    }

    public void addValue(String value){
        this.value.add(value);
    }

    /**
     * backtrack to the root, get the keys sequence.
     * @return
     */
    public List<String> getKeySequence(){
        List<String> seq = new LinkedList<>();
        TreeNode curr = this;
        while (!curr.getKey().equals("root")){
            seq.add(curr.getKey());
            curr = curr.getParentNode();
        }
        Collections.reverse(seq);
        return seq;
    }



    public TreeNode addChild(@NonNull String key, String value){
        TreeNode new_one = new TreeNode(key, value, this);
        children.add(new_one);
        return new_one;
    }

    @Override
    public String toString(){
        return toString("   ");
    }

    public String toString(String indentation) {
//        return String.valueOf(getKeySequence());
        final StringBuilder buffer = new StringBuilder();
        buffer.append(indentation);
        buffer.append("\"").append(this.key).append("\"");
        if (this.value != null) {
            buffer.append(" {");
            buffer.append(this.value);
            buffer.append('}');
        }
        if (isHasChildren()) {
            for (TreeNode t : children) {
                buffer.append("\n");
                buffer.append(t.toString(indentation + "  "));
            }
        }
        return buffer.toString();
    }
}



//[]
//[(ddddd, [aaa, bbb, ddd, eee]), (aaaaa, [aaa, bbb, ccc, ddd])]