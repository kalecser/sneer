//package spikes.rene.jogoai;
//
//
//import javax.swing.JOptionPane;
//
//import sneer.foundation.lang.exceptions.NotImplementedYet;
//
//public class LearningAnimalsGame {
//
//
//       public static void main(String[] ignored) {
//   		     TreeElement _root = new Leaf("Monkey");
//               Player player = new Player();
//               do {
//                       player.acnowledge("Please imagine a kind of animal"
//                                       + " and I will try to guess what it is.");
//
//                       _root = _root.learnFrom(player);
//               } while (player.confirms("Do you want to play again?"));
//       }
//}
//
//
//interface TreeElement {
//       TreeElement learnFrom(Player player);
//}
//
//class Leaf implements TreeElement {
//
//       private final String _kindOfAnimal;
//
//       Leaf(String kindOfAnimal) {
//               _kindOfAnimal = kindOfAnimal;
//       }
//
//       public TreeElement learnFrom(Player player) {
//    	   throw new NotImplementedYet();
//       }
//}
//
//class Node implements TreeElement {
//
//       private final String _characteristic;
//       private TreeElement _yes;
//       private TreeElement _no;
//
//       public Node(TreeElement yes, TreeElement no, String characteristic) {
//               _characteristic = characteristic;
//               _yes = yes;
//               _no = no;
//       }
//
//       public TreeElement learnFrom(Player player) {
//    	   throw new NotImplementedYet();
//       }
//}