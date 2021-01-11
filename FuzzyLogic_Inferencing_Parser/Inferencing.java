
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author maria
 */
public class Inferencing {

    private Vector<Stack<String>> variables = new Vector<>();
    private Vector<Stack<String>> levels = new Vector<>();
    private Vector<Stack<String>> operations = new Vector<>();
    private Vector<String> rules = new Vector<>();
    private Map<String, Float> output = new HashMap<>();
    private Map<String, Map<String, Float>> allRanges = new HashMap<String, Map<String, Float>>();
    private String outputName = "risk";

    public Inferencing(Map<String, Map<String, Float>> ranges, Map<String, Float> out, Vector<String> rulesVec, String outName) {

        rules = rulesVec;
        allRanges = ranges;
        output = out;
        outputName = outName;
        //  splitRules(ranges, rulesVec);

    }

    private Boolean splitRules(Vector<String> rulesVec, Map<String, Float> output) {

        for (int i = 0; i < rulesVec.size(); i++) {
            Stack<String> tempVar = new Stack<>();
            Stack<String> tempLev = new Stack<>();
            Stack<String> tempOp = new Stack<>();
            String[] temp = rulesVec.get(i).toLowerCase().split(" ");
            for (int d = 0; d < temp.length; d++) // System.out.print(temp[d]+" ");
            {
                if (!temp[0].equals("if")) {
                    return false;
                }
            }
            for (int j = 0; j < temp.length; j += 4) {

                if (!temp[j].equals("if") && !temp[j].equals("and")
                        && !temp[j].equals("or") && !temp[j].equals("then")) {
                    return false;
                }

                if (temp[j].equals("or") || temp[j].equals("and")) {
                    tempOp.add(temp[j]);
                }

                if (allRanges.containsKey(temp[j + 1])) {
                    if (!allRanges.get(temp[j + 1]).containsKey(temp[j + 3])) {
                        return false;
                    }

                } else if (temp[j + 1].equals(outputName)) {
                    if (!output.containsKey(temp[j + 3])) {
                        return false;
                    }
                } else {
                    return false;
                }

                tempVar.add(temp[j + 1]);
                if (!temp[j + 2].equals("is")) {
                    return false;
                }
                // System.out.println(temp[j + 1] + " " + temp[j + 3]);

                tempLev.add(temp[j + 3]);

            }

            variables.add(tempVar);

            levels.add(tempLev);
            operations.add(tempOp);


        }
        return true;
    }

    private float and(float a, float b) {
        return Math.min(a, b);
    }

    private float or(float a, float b) {
        return Math.max(a, b);
    }

    public boolean setOutput() {
        if (splitRules(rules, output)) {

            for (int i = 0; i < rules.size(); i++) {

                String targetVariable = variables.get(i).pop();
                String targetLevel = levels.get(i).pop();
                Stack<Float> values = new Stack<>();
                for (int d = 0; d < variables.get(i).size(); d++) {
                    System.out.println(variables.get(i).get(d)+" "+levels.get(i).get(d));
                    values.add(allRanges.get(variables.get(i).get(d)).get(levels.get(i).get(d)));
                }
            
                while (!values.empty() || !operations.get(i).empty()) {

                    if (operations.get(i).isEmpty()) {
                         float value = values.pop();
                          System.out.println("popValue "+ value);
                        
                        if (output.get(targetLevel) > 0) {
                            value = or(value, output.get(targetLevel));
                            output.replace(targetLevel, value);
                        } else {
                            output.replace(targetLevel, value);
                        }
                        break;
                    } else {

                        String operation = operations.get(i).pop();
                        Float value1 = values.pop();
                        Float value2 = values.pop();
                        
                        if (operation.equals("or")) {
                            Float r = or(value1, value2);
                            System.out.println(value1+ "  or " + value2+" = "+r);
                            values.add(r);
                          
                        } else if (operation.equals("and")) {
                            Float r = and(value1, value2);
                             System.out.println(value1+ "  and " + value2+" = "+r);
                           values.add(r);
                        } else {
                            return false;
                        }
                    }
                    
                }

            }
