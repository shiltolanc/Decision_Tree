import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeNode {
    private String attribute;
    Map<String, TreeNode> children;
    private boolean isLeaf;
    private String label;
    private Double InformationGain;
    private Double Entropy;
    private List<String[]> subSet;

    TreeNode(String attribute, List<String[]> data, String label) {
        this.attribute = attribute;
        this.children = new HashMap<>();
        this.subSet = new ArrayList<>(data);
        this.isLeaf = false;
        this.label = label;
    }

    List<String[]> getData(){
        return subSet;
    }
    void setAttribute(String attribute){
        this.attribute = attribute;
    }

    String getAttribute(){
        return this.attribute;
    }

    boolean isLeaf(){
        return this.isLeaf;
    }
    void setLeaf(boolean leaf){
        this.isLeaf = leaf;
    }

    void setInformationGain(double infoG){
        this.InformationGain = infoG;
    }

    double getInformationGain(){
        return this.InformationGain;
    }

    void setEntropy(double entropy){
        this.Entropy = entropy;
    }

    double getEntropy(){
        return this.Entropy;
    }

    void setLabel(String label){
        this.label = label;
    }

    String getLabel(){
        return this.label;
    }

    public String toString(){
        final DecimalFormat df = new DecimalFormat("0.000000");
        double num = 0;
        for (String[] row : subSet) {
            if (Integer.parseInt(row[row.length - 1]) == 1){
                num++;
            }
        }
        if(isLeaf){
            return "leaf: {0: " + (subSet.size() - num) + ", 1: " + num + "}";
        } else {
            return attribute + " IG: " + df.format(InformationGain) + "  E: " + df.format(Entropy);
        }
    }
}