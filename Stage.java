/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Adelina
 */
public class Stage {
    static Calcule c = new Calcule();
    public static void main(String[] args) throws FileNotFoundException, IOException {
        int nb_rules=3;
        int[][] to_compare = c.to_compare(new String[] {"a", "b", "a", "c", "b", "c"});
        String[] rules = new String[nb_rules];
        rules[0] = "<hasAcademicAdvisor>";
        rules[1] = "<graduatedFrom>";
        rules[2] = "<worksAt>";
        String[] B = new String[nb_rules-1];
        for(int i=0; i<nb_rules-1;i++){
            B[i] = rules[i];
        }
//        String rule1 = "<created>";
//        String rule2 = "<directed>";
//        String rule3 = "<produced>";
        String src = "/Users/Adeline/NetBeansProjects/Stage/src/stage/";
        //String file = src + "yago_sample.tsv" ;
        String file = src + "yago2core_facts.clean.notypes.tsv" ;
        for(int i=0; i<nb_rules;i++){
            BufferedReader br_g = new BufferedReader(new FileReader(file));
            System.out.println("Generation for rule " + rules[i]);
            c.generate(src, br_g, rules[i]);
            br_g.close();
        }
        System.out.println("Transitions for support");
        c.transitions(src, "support", to_compare, nb_rules, rules);
        System.out.println("Transitions for confidence");
        c.transitions(src, "confidence", to_compare, nb_rules, B);
        System.out.println("Creation automata for support");
        c.create_automata(src, "support", rules);
        System.out.println("Creation automata for confidence");
        c.create_automata(src, "confidence", B);
        System.out.println("Creation automata for pca confidence");
        c.create_automata(src, "pca", rules[rules.length-1]);
        
        
        BufferedReader br_regle = new BufferedReader(new FileReader(src + "automata_support"));
        BufferedReader br = new BufferedReader(new FileReader(src + "automata_confidence"));
        BufferedReader br_pca = new BufferedReader(new FileReader(src + "automata_pca"));
        int x=0, y=2;
        double sup = c.support(br_regle, x, y, 999999); 
        br_regle.close();
        System.out.println("Support = " + sup);
        System.out.println();
        System.out.println();
        System.out.println();
        double standard_conf = sup / c.standard_confidence(br, x, y, 999999); 
        System.out.println("Standard confidence = " + standard_conf);
        br.close();
        br = new BufferedReader(new FileReader(src + "automata_confidence"));
        double pca_conf = sup / c.pca_confidence(src, br_pca, br, x, y, 999999, to_compare);
        br_pca.close();
        System.out.println("PCA confidence = " + pca_conf);
    }
}