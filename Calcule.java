/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static stage.Stage.c;

/**
 *
 * @author Adelina
 */
class Calcule {

    int mod(int i){
        if(i%2==1)
            return 2;
        return i%2;
    }
    
    int fact(int n){
        if(n==1){
            return 1;
        }
        else{
            return n*fact(n-1);
        }
    }
    
    int[][] to_compare(String[] values) {
        int [][] tab = new int[fact(values.length/2)][values.length/2+1];
        int i=0, j=0, k=0;
        for(k=0;k<fact(values.length/2);k++){
            for(j=0;j<values.length/2;j++){
                tab[k][j]=-1;
            }
        }
        k=0;
        for(i=0;i<fact(values.length/2);i++){
            for(j=i+1;j<values.length;j++){
                if(values[i].equals(values[j])){
                    tab[k][i/2]=i;
                    tab[k][j/2]=j;
                    
                    k++;
                }
            }
        }
        return tab;
    }

    
    void generate(String src, BufferedReader br, String rule) throws IOException {
        if(rule!=null){
            String sCurrentLine;
            FileWriter res = new FileWriter(new File(src + rule));
            while ((sCurrentLine = br.readLine()) != null) {
                if(sCurrentLine.contains(rule)){
                    res.write(sCurrentLine);
                    res.write("\n");
                }
            }
            res.close();
            br.close();
        }
    }

    
    private Set<Pair<String, Set>> check(String src, FileWriter result, String[] tableOfRules, int[][] to_compare, String[] values, int level) throws IOException {
        Set<Pair<String, Set>> res = new HashSet<>();
        if(level<tableOfRules.length){
            BufferedReader br = new BufferedReader(new FileReader(src + tableOfRules[level]));
            String currentLine;
            int i, j;
            while ((currentLine = br.readLine()) != null) {
                boolean mustBeAdded = true;
                boolean mustBeTested = true;
                String[] splited = currentLine.split(" ");
                if(splited[2].charAt(splited[2].length()-1)==('.')){
                    splited[2] = splited[2].substring(0, splited[2].length() - 1);
                }
                values[level*2]=splited[0];
                values[level*2+1]=splited[2];
                for(i=0; i<fact(values.length/2) && mustBeAdded; i++){
                    int negative=0;
                    mustBeTested = true;
                    for(j=0; j<to_compare[i].length && mustBeTested;j++){
                        if(to_compare[i][j]==-1)
                            negative++;
                        if(to_compare[i][j]>level*2+1){
                            mustBeTested=false;
                        }
                        
                    }
                    if(negative==3)
                        mustBeTested=false;
                    if(mustBeTested){
                        String var_a=null;
                        String var_b=null;
                        for(j=0;j<to_compare[i].length && var_b==null;j++){
                            if(to_compare[i][j]!=-1){
                                if(var_a==null){
                                    var_a=values[to_compare[i][j]];
                                }else{
                                    var_b=values[to_compare[i][j]];
                                }
                            }
                        }
                            mustBeAdded = mustBeAdded && var_a.equals(var_b);
                    }
                }
                if(mustBeAdded){
                    Set children = check(src, result, tableOfRules, to_compare, values, level+1);
                    if(!children.isEmpty() || level+1==tableOfRules.length){
                        res.add(new Pair(splited[0] + " " + splited[1] + " " + splited[2], children));
                    }
                }
            }
            
        }
        return res;
    }
    
    private void to_print(FileWriter result_rule, Set<Pair<String, Set>> results) throws IOException {
        Iterator<Pair<String, Set>> iterator = results.iterator();
        while(iterator.hasNext()) {
            Pair<String, Set> setElement = iterator.next();
            result_rule.write(setElement.first_value);
            result_rule.write("\n");
            to_print(result_rule, setElement.second_value);
        }
    }
    
    void transitions(String src, String type, int[][] to_compare, int nb_rules, String... rules) throws IOException {
        String fileName=src+type;
        String[] tableOfRules = new String[rules.length];
        String[] values = new String[nb_rules*2];
        Set<Pair<String, Set>> results = new HashSet<>();
        int i=0;
        for(String r : rules){
            tableOfRules[i]=r;
            i++;
        }
        FileWriter result_rule = new FileWriter(new File(fileName));
        int level=0;
        BufferedReader br = new BufferedReader(new FileReader(src + tableOfRules[level]));
        String currentLine;
        while ((currentLine = br.readLine()) != null) {
            String[] splited = currentLine.split(" ");
            if(splited[2].charAt(splited[2].length()-1)==('.')){
                splited[2] = splited[2].substring(0, splited[2].length() - 1);
            }
            values[level*2]=splited[0];
            values[level*2+1]=splited[2];
            Set<Pair<String, Set>> children = check(src, result_rule, tableOfRules, to_compare, values, level+1);
            if(!children.isEmpty() || level+1==tableOfRules.length){
                results.add(new Pair(splited[0] + " " + splited[1] + " " + splited[2], children));
            }
            
                
        }
        to_print(result_rule, results);
        result_rule.close();
        br.close();
    }
 
    class Pair<L, R>{
        L first_value;
        R second_value;
        public Pair(L first_value, R second_value){
            this.first_value=first_value;
            this.second_value=second_value;
        }
        <L,R> Pair<L,R> of(L first_value, R second_value){
            return new Pair<>(first_value, second_value);
        }
        
        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof Pair)) return false;
            Pair<L,R> castedObj = (Pair<L,R>)obj;
            return (this.first_value.equals(castedObj.first_value)) && (this.second_value.equals(castedObj.second_value));
        }
        
        @Override
        public int hashCode() {
            return this.first_value.hashCode()^this.second_value.hashCode();
        }
    }
     
    Map<String, Integer> set = new HashMap(0);
    Set<Integer> transitions = new HashSet<>();
    int state_a=-1, state_b=-1, state_c=-1;
    int nb_states=1;
    int stateF = 99999;
    void create_automata(String src, String type, String... rules) throws FileNotFoundException, IOException {
        transitions = new HashSet<>();
        set = new HashMap(0);
        nb_states=1;
        BufferedReader br_rule;
        Map<Integer, Map<String, Integer>> states = new HashMap<>();
        String fileName=src;
        String[] tableOfRules = new String[rules.length];
        int i=0;
        for(String r : rules){
            tableOfRules[i]=r;
            i++;
        }
        switch (type) {
            case "support":
                fileName+="support";
                break;
            case "confidence":
                fileName+="confidence";
                break;
            case "pca":
                fileName+=tableOfRules[i-1];
                break;
            default:
                break;
        }
        br_rule = new BufferedReader(new FileReader(fileName));
        String sCurrentLine;
        while ((sCurrentLine = br_rule.readLine()) != null) {
            String[] splited = sCurrentLine.split(" ");
            boolean alreadyFind=false;
            for(i=0;i<rules.length && alreadyFind==false;i++){
                if(i==0 && splited[1].equals(tableOfRules[i])){
                    alreadyFind=true;
                    state_a=0;
                    state_b=-1;
                    state_c=-1;
                    if(states.containsKey(state_a)){
                        set = states.get(state_a);
                        if(set.containsKey(splited[0])){
                            state_b=set.get(splited[0]);
                        }else{
                            state_b=nb_states;
                            nb_states++;
                            set.put(splited[0], state_b);
                            states.replace(state_a, set);
                            states.put(state_b, new HashMap<>());
                        }
                    }else{
                        set=new HashMap<>();
                        state_b=nb_states;
                        nb_states++;
                        set.put(splited[0], state_b);
                        states.put(state_b, new HashMap<>());
                        states.put(state_a, set);
                    }
                    if(rules.length==1){
                        state_c=stateF;
                        set=states.get(state_b);
                        set.put(splited[2], state_c);
                        states.replace(state_b, set);
                    }
                }
                else if(i==rules.length-1 && splited[1].equals(tableOfRules[i])){
                    alreadyFind=true;
                    state_c=-1;
                    if(state_b!=-1){
                        set=states.get(state_b);
                        if(!set.containsKey(splited[2])){
                            state_c=nb_states;
                            nb_states++;
                            set.put(splited[2], state_c);
                        }
                        else{
                            state_c=set.get(splited[2]);
                            set.replace(splited[2], state_c);
                        }
                        states.replace(state_b, set);
                        set=new HashMap<>();
                        set.put("Final", stateF);
                        states.put(state_c, set);
                    }
                }
                else if(splited[1].equals(tableOfRules[i])){
//                    System.out.println(" i = " + i + " : " + splited[1] + " = " + tableOfRules[i]);
                    alreadyFind=true;
                    state_c=-1;
                    if(state_b!=-1){
                        set=states.get(state_b);
                        if(!set.containsKey(splited[2])){
                            state_c=nb_states;
                            nb_states++;
                            set.put(splited[2], state_c);
                            states.replace(state_b, set);
                        }
                    }
                }
            }
        } 
        br_rule.close();
        FileWriter result;
        fileName = src + "automata_";
        switch (type) {
            case "support":
                fileName+="support";
                break;
            case "confidence":
                fileName+="confidence";
                break;
            case "pca":
                fileName+="pca";
                break;
            default:
                break;
        }
        result = new FileWriter(new File(fileName));
        states.keySet().forEach((key) ->{
            set=states.get(key);
            set.keySet().forEach((key2)->{
                try {
					result.write("\t");
	                result.write(key.toString());
	                result.write("\t");
	                result.write(set.get(key2).toString());
	                result.write("\t");
	                result.write(key2);
	                result.write("\n");
	                
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            });
        });
        result.close();
    }

        
    
    
    public class Node<T>{
        private final Map<T,Node<T>> transition = new HashMap<>();
        private int stateId=0;
        private int level = 0;
        private final Map<Integer, Node<T>> parents = new HashMap<>();
        public Node(int stateId, int level) {
            this.stateId = stateId;
            this.level = level;
        }
        public void addTransition(T label, int stateId) {
            Node<T> newChild = new Node<>(stateId, this.level+1);
            this.transition.put(label, newChild);
        }
        public void addParent(int level, Node<T> parent) {
            this.parents.put(level, parent);
        }
        public void addTransition(T label, Node<T> child) {
            this.transition.put(label, child);
        }
        public Map<T,Node<T>> getChildren() {
            return transition;
        }
        public int getStateId() {
            return stateId;
        }
        
        public void update_level() {
            this.getChildren().values().forEach((child)->{
                child.level=this.level+1;
                child.update_level();
            });
    }
    }
    
    
    
    public  Map<Integer, Node> construction(BufferedReader br, int nb_max) throws IOException{
        Map<Integer, Node> th = new HashMap<>();
        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null) {
            if(!"\n".equals(sCurrentLine) && !"\t".equals(sCurrentLine)){
                sCurrentLine = sCurrentLine.substring(1, sCurrentLine.length());
                String[] splited = sCurrentLine.split("\t");
                Node node;
                if(th.containsKey(Integer.parseInt(splited[0]))){
                    node = th.get(Integer.parseInt(splited[0]));
                    Node child;
                    if(th.containsKey(Integer.parseInt(splited[1]))){
                        child = th.get(Integer.parseInt(splited[1]));
                        if(child.level<=node.level){
                            child.level = node.level+1;
                            child.update_level();
                            th.replace(Integer.parseInt(splited[1]), child);
                        }
                    }else{
                        child = new Node<>(Integer.parseInt(splited[1]), node.level+1);
                        th.put(Integer.parseInt(splited[1]), child);
                    }
                    node.addTransition(splited[2], child);
                    th.replace(Integer.parseInt(splited[0]), node);
                }else{
                    node = new Node<>(Integer.parseInt(splited[0]), 0);
                    Node child;
                    if(th.containsKey(Integer.parseInt(splited[1]))){
                        child = th.get(Integer.parseInt(splited[1]));
                        if(child.level==0){
                            child.level=1;
                            child.update_level();
                        }
                    }else{
                        child = new Node<>(Integer.parseInt(splited[1]), node.level+1);
                        th.put(Integer.parseInt(splited[1]), child);
                    }
                    node.addTransition(splited[2], child);
                    th.put(Integer.parseInt(splited[0]), node);
                }
            }
        }
        br.close();
        return th;
    }
    
    Node result= new Node(0,0);
    public Node find_root(Map<Integer, Node> transitions) {
        transitions.keySet().forEach((key)->{
            Node r = transitions.get(key);
            if(r.level==0){
                result = r;
            }
        });
        return result;
    }
    
    static int first, second;
    
    public Set<Pair<String, String>> find_second(Node root, int first_value, String first_key, String second_key, HashMap ht){
        Set<Pair<String, String>> pairs = new HashSet<>();
        if(root.level==second){
            Pair p = new Pair(first_key, second_key);
            pairs.add(p);
            return pairs;
        }else{
            Set<Pair<String, String>> pairs2;
            if((ht.get(root.stateId))==null){
                root.transition.keySet().forEach((key) -> {
                    Set<Pair<String, String>> res = find_second((Node) root.transition.get(key), first_value, first_key, (String) key, ht);
                    Set<Pair<String, String>> temp = new HashSet<>();
                    Iterator<Pair<String, String>> iterator2 = res.iterator();
                    while(iterator2.hasNext()) {
                        Pair<String, String> setElement2 = iterator2.next();
                        Iterator<Pair<String, String>> iterator3 = pairs.iterator();
                        boolean exist=false;
                        while(iterator3.hasNext() && exist==false) {
                            Pair<String, String> setElement3 = iterator3.next();
                            if(setElement2.equals(setElement3)){
                                exist=true;
                            }
                        }if(exist==false)
                            temp.add(setElement2);
                    }
                    pairs.addAll(temp);
                });
                ht.put(root.stateId, pairs);
                return pairs;
            }else{
                pairs2=(Set<Pair<String, String>>) ht.get(root.stateId);
                Iterator<Pair<String, String>> iterator = pairs2.iterator();
                Set<Pair<String, String>> temp = new HashSet<>();
                while(iterator.hasNext()) {
                    Pair<String, String> setElement = iterator.next();
                    Pair<String, String> modified = new Pair(first_key, setElement.second_value);
                    Iterator<Pair<String, String>> iterator2 = pairs2.iterator();
                    boolean exist = false;
                    while(iterator2.hasNext() && exist==false) {
                        Pair<String, String> setElement2 = iterator2.next();
                        if(modified.equals(setElement2) || modified.equals(setElement))
                            exist=true;
                    }
                    if(exist==false){
                        Iterator<Pair<String, String>> iterator3 = temp.iterator();
                        boolean exist2 = false;
                        while(iterator3.hasNext() && exist2==false) {
                            Pair<String, String> setElement3 = iterator3.next();
                            if(modified.equals(setElement3))
                                exist2=true;
                        }
                        if(exist2==false){
                            temp.add(modified);
                        }
                    }
                }
                pairs2.addAll(temp);
                ht.replace(root.stateId, pairs2);
                return pairs2;
            }
        }
    }
    
    public Set<Pair<String, String>> find_first(Node root){
        Set<Pair<String, String>> pairs = new HashSet<>();
        Set<Pair<String, String>> temp = new HashSet<>();
        HashMap ht= new HashMap();
        if(root.level==first){
            root.transition.keySet().forEach((key) -> {
                Set<Pair<String, String>> res = find_second((Node) root.transition.get(key), root.stateId, (String) key, (String) key, ht);
                Iterator<Pair<String, String>> iterator2 = res.iterator();
                while(iterator2.hasNext()) {
                    Pair<String, String> setElement2 = iterator2.next();
                    Iterator<Pair<String, String>> iterator3 = pairs.iterator();
                    boolean exist=false;
                    while(iterator3.hasNext() && exist==false) {
                        Pair<String, String> setElement3 = iterator3.next();
                        if(setElement2.equals(setElement3)){
                            exist=true;
                        }
                    }if(exist==false){
                        temp.add(setElement2);
                    }
                }
                pairs.addAll(temp);
            });
        }else{
            root.transition.keySet().forEach((key) -> {
                Set<Pair<String, String>> res = find_first((Node) root.transition.get(key));
                Iterator<Pair<String, String>> iterator2 = res.iterator();
                while(iterator2.hasNext()) {
                    Pair<String, String> setElement2 = iterator2.next();
                    Iterator<Pair<String, String>> iterator3 = pairs.iterator();
                    boolean exist=false;
                    while(iterator3.hasNext() && exist==false) {
                        Pair<String, String> setElement3 = iterator3.next();
                        if(setElement2.equals(setElement3)){
                            exist=true;
                        }
                    }if(exist==false)
                        temp.add(setElement2);
                }
                pairs.addAll(temp);
            });
        }
        return pairs;
    }
    
    
    Set<String> list_r(String src, BufferedReader br) throws IOException {
        Set<String> list = new HashSet();
        String sCurrentLine;
        FileWriter result = new FileWriter(new File(src + "r"));
        while ((sCurrentLine = br.readLine()) != null) {
            sCurrentLine = sCurrentLine.substring(1, sCurrentLine.length());
            String[] splited = sCurrentLine.split("\t");
            if(splited[0].equals("0")){
                list.add(splited[2]);
                result.write(splited[2]);
                result.write("\n");
            }
            
            
        }
        result.close();
        return list;
    }
    
    void required_levels(int x, int y){
        if(x<y){
            first = x;
            second = y;
        }else{
            first=y;
            second = x;
        }
    }
    
    int support(BufferedReader br, int x, int y, int nb_max) throws IOException {
        required_levels(x,y);
        Map<Integer, Node> transition = construction(br, nb_max);
        Node root = find_root(transition);
        Set<Pair<String, String>> s = find_first(root);
        return s.size();
    }
    
    int standard_confidence(BufferedReader br, int x, int y, int nb_max) throws IOException {
        required_levels(x,y);
        Map<Integer, Node> transition = construction(br, nb_max);
        Node root = find_root(transition);
        Set<Pair<String, String>> s = find_first(root);
        return s.size();
    }
    
    long pca_confidence(String src, BufferedReader br_r, BufferedReader br_B, int x, int y, int nb_max, int[][] to_compare) throws IOException {
        required_levels(x,y);
        Map<Integer, Node> transition = construction(br_B, nb_max);
        Node root = find_root(transition);
        Set<Pair<String, String>> list = find_first(root);
        Set<String> list_r = list_r(src, br_r);
        long nbPairsBwithOneR;
        nbPairsBwithOneR= list.stream().filter((p) -> list_r.contains(p.first_value)).count();
        return nbPairsBwithOneR;
    }
    
}
