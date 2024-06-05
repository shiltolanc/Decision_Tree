import java.io.*;
import java.util.*;

public class DecisionTree {

    public static List<String[]> data;
    public static List<String> attributes;

    public static int count = 0;

    private static List<String[]> readCSV(String filename) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                data.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static double calculateEntropy(List<String[]> dataSet, int find){
        double entropy = 0.0;
        double total = dataSet.size();

        List<String[]> subSet = new ArrayList<>(dataSet);
        double num = 0;
        for (String[] row : subSet) {
            if (Integer.parseInt(row[row.length - 1]) == find){
                num++;
            }
        }
        for(int i = 0; i < 2; i++){
            entropy -= (num/total) * (Math.log(num/total) / Math.log(2));
            num = total - num;
        }
        if(Double.isNaN(entropy)){
            entropy = 0;
        }
        return entropy;
    }

    private static double calculateInformationGain(String featureName, double entropyS, List<String[]> dataSet){
        double infoGain = 0.0;

        int rowNum = attributes.indexOf(featureName);
        List<String[]> subSet0 = new ArrayList<>(dataSet);
        List<String[]> subSet1 = new ArrayList<>();
        for(String[] row: dataSet){
            if(Integer.parseInt(row[rowNum]) == 1){
                subSet1.add(row);
                subSet0.remove(row);
            }
        }
        double eYtotal = subSet1.size();
        double eNtotal = subSet0.size();
        double eY = calculateEntropy(subSet1, 1);
        double eN = calculateEntropy(subSet0, 0);

        infoGain = entropyS - (eYtotal / dataSet.size()) * eY - (eNtotal / dataSet.size()) * eN;

        return infoGain;
    }

    private static TreeNode buildDecisionTree(List<String> attributes, List<String[]> dataSet, String label) {
        double infoGain = 0.0;
        String attribute = "";
        double entropyS = calculateEntropy(dataSet, 0);

        for(String attr: attributes){
            if(attr.equals("class")){break;}
            if(calculateInformationGain(attr, entropyS, dataSet) > infoGain){
                infoGain = calculateInformationGain(attr, entropyS, dataSet);
                attribute = attr;
            }
        }
        TreeNode node = new TreeNode(attribute,dataSet,label);
        node.setEntropy(entropyS);
        node.setInformationGain(infoGain);

        List<String[]> subSet = new ArrayList<>(dataSet);
        double num = 0;
        for (String[] row : subSet) {
            if (Integer.parseInt(row[row.length - 1]) == 1){
                num++;
            }
        }

        if(attributes.size() <= 1 || infoGain < 0.0001 || (num == 0 || (subSet.size()-num) == 0)){
            TreeNode leaf = new TreeNode(attribute, dataSet,label);
            leaf.setLeaf(true);
            return leaf;
        }

        int rowNum = attributes.indexOf(attribute);
        List<String[]> subSet0 = new ArrayList<>(dataSet);
        List<String[]> subSet1 = new ArrayList<>();
        for(String[] row: dataSet){
            if(Integer.parseInt(row[rowNum]) == 1){
                subSet1.add(row);
                subSet0.remove(row);
            }
        }

        node.children.put("0", buildDecisionTree(attributes,subSet0,"0"));
        node.children.put("1", buildDecisionTree(attributes,subSet1,"1"));

        return node;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java DecisionTree <train_file> <output_file>");
            return;
        }

        String trainFile = args[0];
        String outputFile = args[1];

        data = readCSV(trainFile);
        attributes = Arrays.asList(data.get(0));
        data.remove(0); // Remove header

        TreeNode root = new TreeNode("class",data,"");
        root.children.put("", buildDecisionTree(attributes.stream().filter(s -> !s.equals("class")).toList(),data,""));

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            print(root.children.get(""), "", writer);
            writer.close();
            System.out.println("Output written to output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        double accuracy = calculateAccuracy(root.children.get(""));
        System.out.println("Accuracy: " + ((accuracy / count ) * 100) + "%");
    }

    private static double calculateAccuracy(TreeNode node) {
        if (node.isLeaf()) {
            List<String[]> dataSet = node.getData();
            int totalInstances = dataSet.size();
            int classIndex = dataSet.get(0).length - 1;
            int majorityClassCount = 0;

            // Count the majority class instances
            for (String[] instance : dataSet) {
                if (Integer.parseInt(instance[classIndex]) == 1) {
                    majorityClassCount++;
                }

                if(majorityClassCount == 0){
                    majorityClassCount = dataSet.size();
                }
            }

            double purity = (double) majorityClassCount / totalInstances;
            count++;

            return purity;
        } else {
            double accuracy = 0.0;
            for (TreeNode child : node.children.values()) {
                accuracy += calculateAccuracy(child);
            }
            return accuracy;
        }
    }

    static void print(TreeNode node, String space, BufferedWriter writer) throws IOException {
        writer.write(space + node + "\n");
        if (!node.children.isEmpty()) {
            for (Map.Entry<String, TreeNode> n : node.children.entrySet()) {
                String p = space;
                writer.write(space + "--" + node.getAttribute() + " == " + n.getKey() + "-- \n");
                print(n.getValue(), p += "   ", writer);
            }
        }
    }
}